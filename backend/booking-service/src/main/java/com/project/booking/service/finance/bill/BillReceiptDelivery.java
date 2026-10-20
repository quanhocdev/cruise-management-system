package com.project.booking.service.finance.bill;

import com.project.common.event.BillPaidEvent;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.concurrent.TimeUnit;

@Component
@DependsOn("registrationSchemaInitializer")
public class BillReceiptDelivery {
    private final JdbcTemplate jdbc;
    private final KafkaTemplate<String,Object> kafka;
    public BillReceiptDelivery(JdbcTemplate jdbc,KafkaTemplate<String,Object> kafka){this.jdbc=jdbc;this.kafka=kafka;}
    @Scheduled(fixedDelayString="${bill.receipt-delivery-delay-ms:15000}")
    @Transactional
    public void deliver(){
        var json=new com.fasterxml.jackson.databind.ObjectMapper().findAndRegisterModules();
        for(var row:jdbc.queryForList("SELECT bill_id,payload FROM booking.bill_paid_deliveries WHERE delivered_at IS NULL ORDER BY bill_id LIMIT 20 FOR UPDATE SKIP LOCKED")) {
            try {
                var event=json.readValue((String)row.get("payload"),BillPaidEvent.class);
                kafka.send("bill-paid-topic",row.get("bill_id").toString(),event).get(10,TimeUnit.SECONDS);
                jdbc.update("UPDATE booking.bill_paid_deliveries SET delivered_at=CURRENT_TIMESTAMP WHERE bill_id=?",row.get("bill_id"));
            } catch(Exception e) {
                if(e instanceof InterruptedException) Thread.currentThread().interrupt();
                throw new IllegalStateException("Bill receipt delivery will be retried",e);
            }
        }
    }
}
