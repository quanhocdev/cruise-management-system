
package com.project.chat.controller;

import com.project.chat.dto.webhook.DialogflowWebhookRequest;
import com.project.chat.dto.webhook.DialogflowWebhookResponse;
import com.project.chat.service.webhook.DialogflowWebhookService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Map;

@RestController
@RequestMapping("/api/webhook")
public class DialogflowWebhookController {

    private final DialogflowWebhookService webhookService;
    private final String webhookSecret;

    public DialogflowWebhookController(
            DialogflowWebhookService webhookService,
            @Value("${dialogflow.webhook.secret:}") String webhookSecret) {

        this.webhookService = webhookService;
        this.webhookSecret = webhookSecret;
    }

    @PostMapping
    public ResponseEntity<DialogflowWebhookResponse> handleWebhook(
            @RequestHeader(value = "X-Webhook-Secret", required = false) String requestSecret,
            @RequestBody(required = false) DialogflowWebhookRequest request) {

        // Không cho phép webhook hoạt động nếu chưa cấu hình secret.
        if (webhookSecret == null || webhookSecret.isBlank()) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(new DialogflowWebhookResponse(
                            "Webhook chưa được cấu hình."));
        }

        // Xác thực request đến từ Dialogflow.
        if (requestSecret == null
                || !MessageDigest.isEqual(
                        webhookSecret.getBytes(StandardCharsets.UTF_8),
                        requestSecret.getBytes(StandardCharsets.UTF_8))) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new DialogflowWebhookResponse(
                            "Unauthorized webhook request."));
        }

        if (request == null
                || request.queryResult() == null
                || request.queryResult().intent() == null) {

            return ResponseEntity.ok(
                    new DialogflowWebhookResponse(
                            "Hệ thống chưa nhận diện được yêu cầu."));
        }

        String intentName = request.queryResult().intent().displayName();

        Map<String, Object> params = request.queryResult().parameters();

        String replyMessage = webhookService.handleDialogflowIntent(
                intentName,
                params);

        return ResponseEntity.ok(
                new DialogflowWebhookResponse(replyMessage));
    }
}
