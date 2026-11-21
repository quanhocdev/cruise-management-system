
package com.project.gateway.filter;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
public class ClientIpGlobalFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(ClientIpGlobalFilter.class);

    private static final String CLIENT_IP_HEADER = "X-Client-IP";
    private static final String NGROK_CLIENT_IP_HEADER = "X-Client-IP";
    private static final String NGROK_SECRET_HEADER = "X-Ngrok-Proxy-Token";

    @Value("${app.security.trusted-ngrok.secret:}")
    private String trustedNgrokSecret;

    @Override
    public Mono<Void> filter(
            ServerWebExchange exchange,
            GatewayFilterChain chain) {

        InetSocketAddress remoteAddress = exchange.getRequest().getRemoteAddress();

        String remoteIp = resolveRemoteIp(remoteAddress);

        String clientIp = remoteIp;
        String source = "DIRECT";

        String suppliedSecret = exchange.getRequest()
                .getHeaders()
                .getFirst(NGROK_SECRET_HEADER);

        String ngrokClientIp = exchange.getRequest()
                .getHeaders()
                .getFirst(NGROK_CLIENT_IP_HEADER);

        // Chỉ tin IP do Ngrok cung cấp khi secret hợp lệ.
        if (isTrustedNgrokRequest(suppliedSecret)
                && isValidIp(ngrokClientIp)) {

            clientIp = normalizeIp(ngrokClientIp);
            source = "NGROK";
        }

        final String finalClientIp = clientIp;
        final String finalSource = source;

        log.info(
                "[GATEWAY IP] Method: {}, Path: {}, Remote IP: {}, Client IP: {}, Source: {}",
                exchange.getRequest().getMethod(),
                exchange.getRequest().getURI().getPath(),
                remoteIp,
                finalClientIp,
                finalSource);

        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(request -> request.headers(headers -> {
                    // Không giữ header IP do client tự gửi.
                    headers.remove(CLIENT_IP_HEADER);

                    // Không chuyển tiếp secret của Ngrok xuống microservice.
                    headers.remove(NGROK_SECRET_HEADER);

                    // Gateway tự thiết lập IP đã xác định.
                    headers.set(CLIENT_IP_HEADER, finalClientIp);
                }))
                .build();

        return chain.filter(mutatedExchange);
    }

    private boolean isTrustedNgrokRequest(String suppliedSecret) {

        if (trustedNgrokSecret == null
                || trustedNgrokSecret.isBlank()
                || suppliedSecret == null
                || suppliedSecret.isBlank()) {
            return false;
        }

        return MessageDigest.isEqual(
                trustedNgrokSecret.getBytes(StandardCharsets.UTF_8),
                suppliedSecret.getBytes(StandardCharsets.UTF_8));
    }

    private String resolveRemoteIp(InetSocketAddress remoteAddress) {

        if (remoteAddress == null || remoteAddress.getAddress() == null) {
            return "unknown";
        }

        return remoteAddress.getAddress().getHostAddress();
    }

    private boolean isValidIp(String ip) {

        if (ip == null || ip.isBlank()) {
            return false;
        }

        String value = ip.trim();

        // Chỉ chấp nhận ký tự hợp lệ của địa chỉ IPv4/IPv6.
        if (!value.matches("[0-9a-fA-F:.]+")) {
            return false;
        }

        try {
            InetAddress.getByName(value);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    private String normalizeIp(String ip) {

        try {
            return InetAddress.getByName(ip.trim()).getHostAddress();
        } catch (Exception exception) {
            return "unknown";
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
