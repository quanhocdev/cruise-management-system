package com.project.feedback.service;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import com.project.feedback.dto.CreateFeedbackRequest;
import com.project.feedback.dto.FeedbackResponse;
import com.project.feedback.exception.FeedbackException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.TreeMap;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FeedbackCommands {
    private final JdbcTemplate jdbc;
    private final FeedbackService feedback;
    private final ObjectMapper json = JsonMapper.builder().findAndAddModules().build();

    public FeedbackCommands(JdbcTemplate jdbc, FeedbackService feedback) {
        this.jdbc = jdbc;
        this.feedback = feedback;
    }

    @Transactional
    public ResponseEntity<FeedbackResponse> create(CreateFeedbackRequest request, Long userId) {
        var key = request.clientRequestId();
        if (request.bookingPassengerId() != null && key == null)
            throw new FeedbackException(HttpStatus.BAD_REQUEST, "CLIENT_REQUEST_ID_REQUIRED");
        if (key == null) return ResponseEntity.status(201).body(feedback.create(request, userId));
        if (key.version() != 4 || key.variant() != 2)
            throw new FeedbackException(HttpStatus.BAD_REQUEST, "INVALID_CLIENT_REQUEST_ID");
        String hash = hash(request);
        int inserted = jdbc.update("""
            INSERT INTO feedback.create_requests(client_request_id,reviewer_user_id,payload_hash)
            VALUES (?,?,?) ON CONFLICT DO NOTHING
            """, key, userId, hash);
        var stored = jdbc.queryForMap(
            "SELECT reviewer_user_id,payload_hash,response_body FROM feedback.create_requests WHERE client_request_id=? FOR UPDATE", key);
        if (!userId.equals(stored.get("reviewer_user_id")))
            throw new FeedbackException(HttpStatus.FORBIDDEN, "REQUEST_OWNER_MISMATCH");
        if (!hash.equals(stored.get("payload_hash")))
            throw new FeedbackException(HttpStatus.CONFLICT, "IDEMPOTENCY_CONFLICT");
        try {
            if (inserted == 0) {
                var response = json.readValue((String) stored.get("response_body"), FeedbackResponse.class);
                return ResponseEntity.status(201).header("Idempotent-Replay", "true").body(response);
            }
            var response = feedback.create(request, userId);
            jdbc.update("UPDATE feedback.create_requests SET response_body=? WHERE client_request_id=?",
                json.writeValueAsString(response), key);
            return ResponseEntity.status(201).body(response);
        } catch (tools.jackson.core.JacksonException e) {
            throw new IllegalStateException("Cannot store feedback response", e);
        }
    }

    private String hash(CreateFeedbackRequest request) {
        try {
            var values = json.convertValue(request, new tools.jackson.core.type.TypeReference<TreeMap<String,Object>>() {});
            values.remove("clientRequestId");
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                .digest(json.writeValueAsString(values).getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid feedback request", e);
        }
    }
}
