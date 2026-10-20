package com.project.payment.refund;

import static com.project.payment.refund.RefundModels.*;
import com.project.payment.config.VnPayConfig;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class VnPayRefundProvider implements RefundProvider {
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").withZone(ZoneId.of("Asia/Ho_Chi_Minh"));
    private static final String REFUND_FIELDS = "vnp_RequestId vnp_Version vnp_Command vnp_TmnCode vnp_TransactionType vnp_TxnRef vnp_Amount vnp_TransactionNo vnp_TransactionDate vnp_CreateBy vnp_CreateDate vnp_IpAddr vnp_OrderInfo";
    private static final String QUERY_FIELDS = "vnp_RequestId vnp_Version vnp_Command vnp_TmnCode vnp_TxnRef vnp_TransactionDate vnp_CreateDate vnp_IpAddr vnp_OrderInfo";
    private static final String RESPONSE_FIELDS = "vnp_ResponseId vnp_Command vnp_ResponseCode vnp_Message vnp_TmnCode vnp_TxnRef vnp_Amount vnp_BankCode vnp_PayDate vnp_TransactionNo vnp_TransactionType vnp_TransactionStatus vnp_OrderInfo";
    private final VnPayConfig config;
    private final URI endpoint;
    private final String serverIp;
    private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5))
            .followRedirects(HttpClient.Redirect.NEVER).build();
    private final ObjectMapper json = new ObjectMapper();

    public VnPayRefundProvider(VnPayConfig config,
            @Value("${vnpay.transaction-api-url:https://sandbox.vnpayment.vn/merchant_webapi/api/transaction}") String endpoint,
            @Value("${vnpay.server-ip:127.0.0.1}") String serverIp) {
        this.config = config;
        this.endpoint = URI.create(endpoint);
        this.serverIp = serverIp;
        boolean local = Set.of("127.0.0.1","localhost","::1","[::1]").contains(this.endpoint.getHost());
        if (!("https".equals(this.endpoint.getScheme()) || (local && "http".equals(this.endpoint.getScheme()))))
            throw new IllegalArgumentException("VNPay API requires HTTPS");
    }

    @Override public Outcome submit(Refund refund) { return exchange(refund,false); }
    @Override public Outcome reconcile(Refund refund) { return exchange(refund,true); }

    public Outcome queryPayment(com.project.payment.model.Payment payment) {
        try {
            config.requireCredentials();
            var p=new LinkedHashMap<String,String>();
            p.put("vnp_RequestId",UUID.randomUUID().toString().replace("-",""));
            p.put("vnp_Version","2.1.0");p.put("vnp_Command","querydr");p.put("vnp_TmnCode",config.getTmnCode());
            p.put("vnp_TxnRef",payment.getId().toString());p.put("vnp_TransactionDate",DATE.format(payment.getCreatedAt()));
            p.put("vnp_CreateDate",DATE.format(Instant.now()));p.put("vnp_IpAddr",serverIp);
            p.put("vnp_OrderInfo","Query payment "+payment.getId());p.put("vnp_SecureHash",sign(p,QUERY_FIELDS));
            var request=HttpRequest.newBuilder(endpoint).timeout(Duration.ofSeconds(15)).header("Content-Type","application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json.writeValueAsString(p))).build();
            var response=http.send(request,HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if(response.statusCode()!=200 || response.body().length()>65536)return Outcome.unknown("PROVIDER_HTTP_ERROR");
            return verifyPayment(payment,json.readValue(response.body(),new TypeReference<Map<String,String>>(){}));
        } catch(InterruptedException e){Thread.currentThread().interrupt();return Outcome.unknown("PROVIDER_INTERRUPTED");}
        catch(Exception e){return Outcome.unknown("PROVIDER_UNCONFIRMED");}
    }

    Outcome verifyPayment(com.project.payment.model.Payment payment,Map<String,String> p) {
        String signature=p.get("vnp_SecureHash");
        if(signature==null || !MessageDigest.isEqual(sign(p,RESPONSE_FIELDS+" vnp_PromotionCode vnp_PromotionAmount")
                .getBytes(StandardCharsets.US_ASCII),signature.toLowerCase(Locale.ROOT).getBytes(StandardCharsets.US_ASCII)))
            return Outcome.unknown("INVALID_PROVIDER_SIGNATURE");
        if(!"querydr".equals(p.get("vnp_Command")) || !config.getTmnCode().equals(p.get("vnp_TmnCode"))
                || !payment.getId().toString().equals(p.get("vnp_TxnRef")) || !"01".equals(p.get("vnp_TransactionType"))
                || !payment.getAmount().movePointRight(2).toBigIntegerExact().toString().equals(p.get("vnp_Amount")))
            return Outcome.unknown("PROVIDER_REFERENCE_MISMATCH");
        if("00".equals(p.get("vnp_ResponseCode"))) {
            String state=p.get("vnp_TransactionStatus"),number=p.get("vnp_TransactionNo");
            if("00".equals(state) && number!=null && number.matches("[0-9]{1,15}"))return new Outcome("SUCCEEDED","00",number);
            if("02".equals(state))return new Outcome("REJECTED","02",number);
        }
        return Outcome.unknown("PROVIDER_UNCONFIRMED");
    }

    private Outcome exchange(Refund r, boolean query) {
        try {
            config.requireCredentials();
            Map<String,String> params = request(r,query);
            HttpRequest request = HttpRequest.newBuilder(endpoint).timeout(Duration.ofSeconds(15))
                    .header("Content-Type","application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json.writeValueAsString(params))).build();
            var response = http.send(request,HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            byte[] bytes = response.body().getBytes(StandardCharsets.UTF_8);
            if (response.statusCode()!=200 || bytes.length>65536) return Outcome.unknown("PROVIDER_HTTP_ERROR");
            Map<String,String> data = json.readValue(bytes,new TypeReference<>(){});
            return verify(r,data,query);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return Outcome.unknown("PROVIDER_INTERRUPTED");
        } catch (Exception e) {
            // A timeout or invalid response does not prove the refund failed at the provider.
            return Outcome.unknown("PROVIDER_UNCONFIRMED");
        }
    }

    Map<String,String> request(Refund r,boolean query) {
        var p = new LinkedHashMap<String,String>();
        p.put("vnp_RequestId",query?UUID.randomUUID().toString().replace("-",""):r.id().toString().replace("-",""));
        p.put("vnp_Version","2.1.0");
        p.put("vnp_Command",query?"querydr":"refund");
        p.put("vnp_TmnCode",config.getTmnCode());
        p.put("vnp_TxnRef",Long.toString(r.paymentId()));
        p.put("vnp_TransactionDate",DATE.format(r.originalCreatedAt()));
        p.put("vnp_CreateDate",DATE.format(Instant.now()));
        p.put("vnp_IpAddr",serverIp);
        p.put("vnp_OrderInfo",r.orderInfo());
        if (!query) {
            p.put("vnp_TransactionType",r.transactionType());
            p.put("vnp_Amount",r.amount().movePointRight(2).toBigIntegerExact().toString());
            p.put("vnp_TransactionNo",r.originalTransactionNo());
            p.put("vnp_CreateBy",Long.toString(r.actorId()));
        } else if (r.providerTransactionNo()!=null) p.put("vnp_TransactionNo",r.providerTransactionNo());
        p.put("vnp_SecureHash",sign(p,query?QUERY_FIELDS:REFUND_FIELDS));
        return p;
    }

    Outcome verify(Refund r,Map<String,String> p,boolean query) {
        String signature = p.get("vnp_SecureHash");
        if (signature==null || !MessageDigest.isEqual(sign(p,RESPONSE_FIELDS+(query?" vnp_PromotionCode vnp_PromotionAmount":""))
                .getBytes(StandardCharsets.US_ASCII),signature.toLowerCase(Locale.ROOT).getBytes(StandardCharsets.US_ASCII)))
            return Outcome.unknown("INVALID_PROVIDER_SIGNATURE");
        if (!Objects.equals(p.get("vnp_Command"),query?"querydr":"refund")
                || !Objects.equals(p.get("vnp_TmnCode"),config.getTmnCode())
                || !Objects.equals(p.get("vnp_TxnRef"),Long.toString(r.paymentId()))
                || !Objects.equals(p.get("vnp_OrderInfo"),r.orderInfo())
                || !Objects.equals(p.get("vnp_Amount"),r.amount().movePointRight(2).toBigIntegerExact().toString())
                || !Objects.equals(p.get("vnp_TransactionType"),r.transactionType())
                || (query && r.providerTransactionNo()!=null && !Objects.equals(p.get("vnp_TransactionNo"),r.providerTransactionNo())))
            return Outcome.unknown("PROVIDER_REFERENCE_MISMATCH");
        String code = p.getOrDefault("vnp_ResponseCode","");
        String state = p.getOrDefault("vnp_TransactionStatus","");
        String number = p.get("vnp_TransactionNo");
        if ("00".equals(code)) {
            if ("00".equals(state) && number!=null && number.matches("[0-9]{1,15}"))
                return new Outcome("SUCCEEDED",code,number);
            if ("09".equals(state)) return new Outcome("REJECTED","REFUND_REJECTED",number);
            if (Set.of("01","05","06").contains(state)) return new Outcome("PROCESSING",code,number);
        }
        if (!query && Set.of("02","03","91","95","97").contains(code)) return new Outcome("REJECTED",code,null);
        return Outcome.unknown("PROVIDER_UNCONFIRMED");
    }

    private String sign(Map<String,String> data,String fields) {
        try {
            String value = String.join("|",Arrays.stream(fields.split(" ")).map(k -> Objects.toString(data.get(k),"")).toList());
            Mac mac = Mac.getInstance("HmacSHA512");
            mac.init(new SecretKeySpec(config.getHashSecret().getBytes(StandardCharsets.UTF_8),"HmacSHA512"));
            return HexFormat.of().formatHex(mac.doFinal(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) { throw new IllegalStateException("Cannot sign VNPay request",e); }
    }
}
