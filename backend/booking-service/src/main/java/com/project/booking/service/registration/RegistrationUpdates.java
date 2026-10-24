package com.project.booking.service.registration;

import java.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.client.RestClient;
import com.project.booking.service.account.UsageCapacityLock;

@Component
public class RegistrationUpdates {
    private final JdbcTemplate jdbc;
    private final RegistrationCatalog catalog;
    private final RegistrationService service;
    private final UsageCapacityLock capacity;
    private final TransactionTemplate tx;
    private final RestClient http;
    private final String key;
    public RegistrationUpdates(JdbcTemplate jdbc,RegistrationCatalog catalog,RegistrationService service,UsageCapacityLock capacity,
            PlatformTransactionManager manager,RestClient.Builder builder,@Value("${notification-service.url}") String url,@Value("${INTERNAL_API_KEY:}") String key){
        this.jdbc=jdbc;this.catalog=catalog;this.service=service;this.capacity=capacity;this.tx=new TransactionTemplate(manager);this.key=key;
        var factory=new org.springframework.http.client.JdkClientHttpRequestFactory();factory.setReadTimeout(java.time.Duration.ofSeconds(10));
        this.http=builder.baseUrl(url).requestFactory(factory).build();
    }
    @Scheduled(fixedDelay=60000,initialDelay=60000)
    public void refreshSchedules(){
        var rows=jdbc.queryForList("select r.id,r.booking_id,r.target_type,r.target_id,b.tour_package_id,b.created_by_user_id from booking.activity_registrations r join booking.bookings b on b.id=r.booking_id where r.status='CONFIRMED' order by r.last_checked_at limit 20");
        for(var row:rows){
            long id=((Number)row.get("id")).longValue();
            try {
                var target=catalog.get((String)row.get("target_type"),(UUID)row.get("target_id"),(UUID)row.get("tour_package_id"));
                if(target.startTime()==null || target.endTime()==null)continue;
                tx.executeWithoutResult(s->{
                    jdbc.queryForObject("select id from booking.bookings where id=? for update",Long.class,row.get("booking_id"));
                    capacity.acquire(target.type(),target.id());var old=service.read(id);
                    if(!old.status().equals("CONFIRMED"))return;
                    boolean cancelled="CANCELLED".equals(target.status());
                    if(cancelled || !old.startTime().equals(target.startTime()) || !old.endTime().equals(target.endTime())){
                        jdbc.update("update booking.activity_registrations set starts_at=?,ends_at=?,status=?,updated_at=CURRENT_TIMESTAMP where id=?",target.startTime(),target.endTime(),cancelled?"CANCELLED":"CONFIRMED",id);
                        service.notify((UUID)row.get("created_by_user_id"),((Number)row.get("booking_id")).longValue(),id,"URGENT",cancelled?"Hoạt động đã hủy":"Lịch hoạt động thay đổi",
                                old.name()+(cancelled?" đã bị hủy. Chưa phát sinh phí tham gia.":" cập nhật thời gian: "+target.startTime()+" đến "+target.endTime()));
                    }
                });
            } catch(RuntimeException ignored) {
                // Keep the existing reservation when the catalogue is temporarily unavailable.
            } finally {jdbc.update("update booking.activity_registrations set last_checked_at=CURRENT_TIMESTAMP where id=?",id);}
        }
    }
    @Scheduled(fixedDelay=10000,initialDelay=10000)
    public void deliver(){
        if(key.isBlank())return;
        for(var row:jdbc.queryForList("select * from booking.registration_notifications where delivered=false order by created_at limit 20")){
            try{
                var payload=new LinkedHashMap<String,Object>();
                payload.put("clientRequestId",row.get("id"));payload.put("recipientId",row.get("recipient_id"));
                payload.put("title",row.get("title"));payload.put("message",row.get("message"));
                payload.put("priority",row.get("priority"));payload.put("bookingId",row.get("booking_id"));
                payload.put("registrationId",row.get("registration_id"));
                http.post().uri("/internal/activity-inbox").header("X-Internal-Api-Key",key).body(payload).retrieve().toBodilessEntity();
                jdbc.update("update booking.registration_notifications set delivered=true where id=?",row.get("id"));
            }catch(RuntimeException ignored){ break; }
        }
    }
}
