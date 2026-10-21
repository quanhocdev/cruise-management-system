package com.project.notification.inbox;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.*;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@DependsOn("activityInboxSchema")
public class ActivityInbox {
    public record Event(@NotNull UUID clientRequestId, @NotNull @Positive Long recipientId,
                        @NotBlank @Size(max=255) String title, @NotBlank @Size(max=2000) String message,
                        @Pattern(regexp="NORMAL|URGENT") String priority,
                        @Positive Long bookingId, @Positive Long registrationId) {
        public Event(UUID key, Long recipient, String title, String message) {
            this(key,recipient,title,message,"NORMAL",null,null);
        }
    }
    public record Notice(UUID id, String title, String message, Instant createdAt, String priority,
                         Long bookingId, Long registrationId, Instant readAt) {}
    public record ReadCommand(@NotNull UUID clientRequestId) {}
    public record ReadResult(UUID id, Instant readAt) {}
    private final JdbcTemplate jdbc;
    private final String key;
    private final TransactionTemplate transaction;

    public ActivityInbox(JdbcTemplate jdbc, @Value("${INTERNAL_API_KEY:}") String key,
                         PlatformTransactionManager manager) {
        this.jdbc=jdbc; this.key=key; this.transaction=new TransactionTemplate(manager);
    }

    @PostMapping("/internal/activity-inbox")
    public ResponseEntity<?> receive(@RequestHeader(value="X-Internal-Api-Key",defaultValue="") String supplied,
                                     @Valid @RequestBody Event event) {
        if(key.isBlank() || !MessageDigest.isEqual(key.getBytes(StandardCharsets.UTF_8),supplied.getBytes(StandardCharsets.UTF_8)))
            return ResponseEntity.status(403).build();
        if(!uuid4(event.clientRequestId()))
            return ResponseEntity.badRequest().body(Map.of("code","INVALID_CLIENT_REQUEST_ID"));
        if(event.registrationId()!=null && event.bookingId()==null)
            return ResponseEntity.badRequest().body(Map.of("code","INVALID_EVENT"));
        String priority=event.priority()==null?"NORMAL":event.priority();
        int inserted=jdbc.update("""
            insert into notification.activity_inbox(id,recipient_id,title,message,priority,booking_id,registration_id)
            values (?,?,?,?,?,?,?) on conflict do nothing
            """,event.clientRequestId(),event.recipientId(),event.title(),event.message(),priority,event.bookingId(),event.registrationId());
        var stored=jdbc.queryForMap("select * from notification.activity_inbox where id=?",event.clientRequestId());
        if(!Objects.equals(stored.get("recipient_id"),event.recipientId()) || !Objects.equals(stored.get("title"),event.title())
                || !Objects.equals(stored.get("message"),event.message()) || !Objects.equals(stored.get("priority"),priority)
                || !Objects.equals(stored.get("booking_id"),event.bookingId()) || !Objects.equals(stored.get("registration_id"),event.registrationId()))
            return conflict();
        return ResponseEntity.status(201).header("Idempotent-Replay",Boolean.toString(inserted==0))
                .body(Map.of("id",event.clientRequestId()));
    }

    @GetMapping("/api/v1/notifications/activities")
    public List<Notice> list(JwtAuthenticationToken jwt, @RequestParam(defaultValue="0") int page,
                            @RequestParam(defaultValue="false") boolean unreadOnly) {
        long user=user(jwt);
        if(page<0 || page>100000)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"INVALID_PAGE");
        return jdbc.query("""
            select * from notification.activity_inbox where recipient_id=? and (not ? or read_at is null)
            order by created_at desc,id limit 20 offset ?
            """,this::notice,user,unreadOnly,(long)page*20);
    }

    @GetMapping("/api/v1/notifications/activities/unread-count")
    public Map<String,Long> unreadCount(JwtAuthenticationToken jwt) {
        return Map.of("unreadCount",jdbc.queryForObject("select count(*) from notification.activity_inbox where recipient_id=? and read_at is null",Long.class,user(jwt)));
    }

    @PostMapping("/api/v1/notifications/activities/{id}/read")
    public ResponseEntity<?> markRead(@PathVariable UUID id, @Valid @RequestBody ReadCommand command, JwtAuthenticationToken jwt) {
        long user=user(jwt);
        if(!uuid4(command.clientRequestId()))return ResponseEntity.badRequest().body(Map.of("code","INVALID_CLIENT_REQUEST_ID"));
        String hash=payloadHash(id);
        return transaction.execute(status->{
            // Ownership is checked before looking up another request's response.
            var owned=jdbc.queryForList("select id from notification.activity_inbox where id=? and recipient_id=?",id,user);
            if(owned.isEmpty())throw new ResponseStatusException(HttpStatus.NOT_FOUND,"NOTIFICATION_NOT_FOUND");
            int inserted=jdbc.update("""
                insert into notification.activity_read_requests(operation_type,client_request_id,recipient_id,payload_hash,notification_id,read_at)
                values ('ACTIVITY_READ',?,?,?,?,CURRENT_TIMESTAMP) on conflict do nothing
                """,command.clientRequestId(),user,hash,id);
            var saved=jdbc.queryForMap("select * from notification.activity_read_requests where operation_type='ACTIVITY_READ' and client_request_id=?",command.clientRequestId());
            if(!Objects.equals(saved.get("recipient_id"),user) || !Objects.equals(saved.get("payload_hash"),hash))return conflict();
            Instant readAt;
            if(inserted==0) {
                readAt=((java.sql.Timestamp)saved.get("read_at")).toInstant();
            } else {
                readAt=jdbc.queryForObject("""
                    update notification.activity_inbox set read_at=COALESCE(read_at,CURRENT_TIMESTAMP)
                    where id=? and recipient_id=? returning read_at
                    """,(r,n)->r.getTimestamp(1).toInstant(),id,user);
                jdbc.update("update notification.activity_read_requests set read_at=? where operation_type='ACTIVITY_READ' and client_request_id=?",
                        java.sql.Timestamp.from(readAt),command.clientRequestId());
            }
            return ResponseEntity.ok().header("Idempotent-Replay",Boolean.toString(inserted==0)).body(new ReadResult(id,readAt));
        });
    }

    private Notice notice(ResultSet r,int row) throws SQLException {
        var read=r.getTimestamp("read_at");
        return new Notice(r.getObject("id",UUID.class),r.getString("title"),r.getString("message"),r.getTimestamp("created_at").toInstant(),
                r.getString("priority"),r.getObject("booking_id",Long.class),r.getObject("registration_id",Long.class),read==null?null:read.toInstant());
    }
    private long user(JwtAuthenticationToken jwt) {
        if(jwt==null || jwt.getAuthorities().stream().noneMatch(a->a.getAuthority().equals("ROLE_PASSENGER")))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        try { return Long.parseLong(String.valueOf(jwt.getTokenAttributes().get("userId"))); }
        catch(NumberFormatException e) { throw new ResponseStatusException(HttpStatus.UNAUTHORIZED); }
    }
    private static boolean uuid4(UUID value) { return value!=null && value.version()==4 && value.variant()==2; }
    private static String payloadHash(UUID id) {
        try { return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(
                ("{\"notificationId\":\""+id+"\"}").getBytes(StandardCharsets.UTF_8))); }
        catch(java.security.NoSuchAlgorithmException e) { throw new IllegalStateException(e); }
    }
    private static ResponseEntity<?> conflict() { return ResponseEntity.status(409).body(Map.of("code","IDEMPOTENCY_CONFLICT")); }

    @Configuration
    public static class Schema {
        @Bean @DependsOn("entityManagerFactory")
        DataSourceInitializer activityInboxSchema(DataSource dataSource) {
            var init=new DataSourceInitializer();init.setDataSource(dataSource);
            init.setDatabasePopulator(new ResourceDatabasePopulator(new ClassPathResource("db/migration/V20261019_01__activity_inbox.sql"),
                    new ClassPathResource("db/migration/V20261020_02__bill_receipt_events.sql"),
                    new ClassPathResource("db/migration/V20261021_03__activity_inbox_state.sql")));
            return init;
        }
    }
}
