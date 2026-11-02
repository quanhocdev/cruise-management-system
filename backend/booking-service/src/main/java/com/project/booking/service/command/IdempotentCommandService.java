package com.project.booking.service.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
import java.util.function.Supplier;

@Service
public class IdempotentCommandService {
    private final JdbcTemplate jdbc;
    private final ObjectMapper json = new ObjectMapper().findAndRegisterModules();

    public IdempotentCommandService(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    @Transactional
    public <T> ResponseEntity<T> execute(String operation, UUID requestId, Object payload,
                                       Class<T> responseType, Supplier<T> command) {
        // Existing clients may omit the key; they retain their original behavior.
        if (requestId == null) return ResponseEntity.status(201).body(command.get());
        if (requestId.version() != 4 || requestId.variant() != 2) {
            throw new CommandException("INVALID_CLIENT_REQUEST_ID", "clientRequestId must be UUID v4", HttpStatus.BAD_REQUEST);
        }
        String actor = actorId();
        String hash = CanonicalPayload.hash(payload);
        int inserted = jdbc.update("""
            INSERT INTO booking.request_dedup(operation_type, client_request_id, actor_id, payload_hash)
            VALUES (?, ?, ?, ?) ON CONFLICT DO NOTHING
            """, operation, requestId, actor, hash);
        var stored = jdbc.queryForMap("""
            SELECT actor_id, payload_hash, response_status, response_body FROM booking.request_dedup
            WHERE operation_type = ? AND client_request_id = ? FOR UPDATE
            """, operation, requestId);
        if (!actor.equals(stored.get("actor_id"))) {
            throw new CommandException("REQUEST_OWNER_MISMATCH", "Request belongs to another user", HttpStatus.FORBIDDEN);
        }
        if (!hash.equals(stored.get("payload_hash"))) {
            throw new CommandException("IDEMPOTENCY_CONFLICT", "clientRequestId was used with a different payload", HttpStatus.CONFLICT);
        }
        if (inserted == 0) {
            if (stored.get("response_status") == null) {
                throw new CommandException("REQUEST_INCOMPLETE", "Request has no committed result", HttpStatus.CONFLICT);
            }
            try {
                T result = json.readValue((String) stored.get("response_body"), responseType);
                return ResponseEntity.status(((Number) stored.get("response_status")).intValue())
                        .header("Idempotent-Replay", "true").body(result);
            } catch (com.fasterxml.jackson.core.JsonProcessingException ex) {
                throw new IllegalStateException("Cannot restore command result", ex);
            }
        }
        T result = command.get();
        try {
            jdbc.update("""
                UPDATE booking.request_dedup SET response_status = 201, response_body = ?
                WHERE operation_type = ? AND client_request_id = ?
                """, json.writeValueAsString(result), operation, requestId);
        } catch (com.fasterxml.jackson.core.JsonProcessingException ex) {
            throw new IllegalStateException("Cannot store command result", ex);
        }
        return ResponseEntity.status(201).body(result);
    }

    private String actorId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new CommandException("AUTHENTICATION_REQUIRED", "Authentication required", HttpStatus.UNAUTHORIZED);
        }
        if (auth instanceof JwtAuthenticationToken jwt) {
            Object id = jwt.getTokenAttributes().get("userId");
            if (id != null) return "user:" + id;
            return "subject:" + jwt.getToken().getSubject();
        }
        return "subject:" + auth.getName();
    }
}
