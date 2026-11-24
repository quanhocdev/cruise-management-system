
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

                System.out.println("[WEBHOOK] Controller received request.");

                System.out.println("[WEBHOOK] Server secret configured: "
                                + (webhookSecret != null && !webhookSecret.isBlank()));

                System.out.println("[WEBHOOK] Request header present: "
                                + (requestSecret != null && !requestSecret.isBlank()));

                // Không cho phép webhook hoạt động nếu chưa cấu hình secret.
                if (webhookSecret == null || webhookSecret.isBlank()) {
                        System.out.println("[WEBHOOK AUTH] Server secret is missing.");

                        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                                        .body(new DialogflowWebhookResponse(
                                                        "Webhook chưa được cấu hình."));
                }

                // Xác thực request đến từ Dialogflow.
                if (requestSecret == null
                                || !MessageDigest.isEqual(
                                                webhookSecret.getBytes(StandardCharsets.UTF_8),
                                                requestSecret.getBytes(StandardCharsets.UTF_8))) {

                        System.out.println("[WEBHOOK AUTH] Secret mismatch or missing.");

                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                        .body(new DialogflowWebhookResponse(
                                                        "Unauthorized webhook request."));
                }

                System.out.println("[WEBHOOK AUTH] Authentication successful.");

                if (request == null
                                || request.queryResult() == null
                                || request.queryResult().intent() == null) {

                        System.out.println("[WEBHOOK] Invalid request body.");

                        return ResponseEntity.ok(
                                        new DialogflowWebhookResponse(
                                                        "Hệ thống chưa nhận diện được yêu cầu."));
                }

                String intentName = request.queryResult().intent().displayName();

                Map<String, Object> params = request.queryResult().parameters();

                System.out.println("[WEBHOOK] Intent: " + intentName);

                String replyMessage = webhookService.handleDialogflowIntent(
                                intentName,
                                params);

                System.out.println("[WEBHOOK] Response generated successfully.");

                return ResponseEntity.ok(
                                new DialogflowWebhookResponse(replyMessage));
        }
}
