package com.project.notification.inbox;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@DependsOn("activityInboxSchema")
public class ActivityInbox {
    public record Event(@NotNull UUID clientRequestId,@NotNull @Positive Long recipientId,
                        @NotBlank @Size(max=255) String title,@NotBlank @Size(max=2000) String message) {}
    private final JdbcTemplate jdbc;
    private final String key;
    public ActivityInbox(JdbcTemplate jdbc,@Value("${INTERNAL_API_KEY:}") String key){this.jdbc=jdbc;this.key=key;}
    @PostMapping("/internal/activity-inbox")
    public ResponseEntity<?> receive(@RequestHeader(value="X-Internal-Api-Key",defaultValue="") String supplied,@Valid @RequestBody Event event){
        if(key.isBlank() || !MessageDigest.isEqual(key.getBytes(StandardCharsets.UTF_8),supplied.getBytes(StandardCharsets.UTF_8)))return ResponseEntity.status(403).build();
        if(event.clientRequestId().version()!=4 || event.clientRequestId().variant()!=2)
            return ResponseEntity.badRequest().body(Map.of("code","INVALID_CLIENT_REQUEST_ID"));
        int inserted=jdbc.update("insert into notification.activity_inbox(id,recipient_id,title,message) values (?,?,?,?) on conflict do nothing",event.clientRequestId(),event.recipientId(),event.title(),event.message());
        var stored=jdbc.queryForMap("select * from notification.activity_inbox where id=?",event.clientRequestId());
        if(!Objects.equals(stored.get("recipient_id"),event.recipientId()) || !Objects.equals(stored.get("title"),event.title()) || !Objects.equals(stored.get("message"),event.message()))
            return ResponseEntity.status(409).body(Map.of("code","IDEMPOTENCY_CONFLICT"));
        return ResponseEntity.status(201).header("Idempotent-Replay",Boolean.toString(inserted==0)).body(Map.of("id",event.clientRequestId()));
    }
    @GetMapping("/api/v1/notifications/activities")
    public ResponseEntity<?> list(JwtAuthenticationToken jwt,@RequestParam(defaultValue="0") int page){
        if(jwt==null || jwt.getAuthorities().stream().noneMatch(a->a.getAuthority().equals("ROLE_PASSENGER")))return ResponseEntity.status(403).build();
        if(page<0 || page>100000)return ResponseEntity.badRequest().build();
        long user;
        try{user=Long.parseLong(String.valueOf(jwt.getTokenAttributes().get("userId")));}catch(Exception e){return ResponseEntity.status(403).build();}
        return ResponseEntity.ok(jdbc.query("select id,title,message,created_at from notification.activity_inbox where recipient_id=? order by created_at desc,id limit 20 offset ?",
                (rs,row)->Map.of("id",rs.getObject("id").toString(),"title",rs.getString("title"),"message",rs.getString("message"),"createdAt",rs.getTimestamp("created_at").toInstant().toString()),user,(long)page*20));
    }
    @Configuration
    public static class Schema {
        @Bean @DependsOn("entityManagerFactory")
        DataSourceInitializer activityInboxSchema(DataSource dataSource){
            var init=new DataSourceInitializer();init.setDataSource(dataSource);
            init.setDatabasePopulator(new ResourceDatabasePopulator(new ClassPathResource("db/migration/V20261019_01__activity_inbox.sql"),
                    new ClassPathResource("db/migration/V20261020_02__bill_receipt_events.sql")));return init;
        }
    }
}
