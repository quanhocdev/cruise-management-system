package com.project.tour.controller;

import com.project.tour.dto.webhook.DialogflowWebhookRequest;
import com.project.tour.dto.webhook.DialogflowWebhookResponse;
import com.project.tour.service.webhook.DialogflowWebhookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/webhook")
public class DialogflowWebhookController {

    private final DialogflowWebhookService webhookService;

    public DialogflowWebhookController(DialogflowWebhookService webhookService) {
        this.webhookService = webhookService;
    }

    @PostMapping
    public ResponseEntity<DialogflowWebhookResponse> handleWebhook(@RequestBody DialogflowWebhookRequest request) {
        if (request == null || request.queryResult() == null || request.queryResult().intent() == null) {
            return ResponseEntity.ok(new DialogflowWebhookResponse("Dạ, hệ thống chưa nhận diện được yêu cầu."));
        }

        String intentName = request.queryResult().intent().displayName();
        var params = request.queryResult().parameters();

        // Xử lý qua Service
        String replyMessage = webhookService.handleDialogflowIntent(intentName, params);

        return ResponseEntity.ok(new DialogflowWebhookResponse(replyMessage));
    }
}