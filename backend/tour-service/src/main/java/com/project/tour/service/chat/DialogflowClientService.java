package com.project.tour.service.chat;

import com.google.cloud.dialogflow.v2.DetectIntentRequest;
import com.google.cloud.dialogflow.v2.DetectIntentResponse;
import com.google.cloud.dialogflow.v2.QueryInput;
import com.google.cloud.dialogflow.v2.SessionName;
import com.google.cloud.dialogflow.v2.SessionsClient;
import com.google.cloud.dialogflow.v2.TextInput;

import jakarta.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.UUID;

@Service
public class DialogflowClientService {

    private final String projectId;
    private final SessionsClient sessionsClient;
    private static final Logger log = LoggerFactory.getLogger(DialogflowClientService.class);

    public DialogflowClientService(
            @Value("${google.cloud.dialogflow.project-id}") String projectId) throws IOException {
        this.projectId = projectId;
        this.sessionsClient = SessionsClient.create();
    }

    public DialogflowResult detectIntent(
            String message,
            String sessionId) {
        String effectiveSessionId = sessionId == null || sessionId.isBlank()
                ? UUID.randomUUID().toString()
                : sessionId.trim();

        if (!effectiveSessionId.matches("[a-zA-Z0-9_-]{1,36}")) {
            throw new IllegalArgumentException("Session ID không hợp lệ");
        }

        SessionName session = SessionName.of(
                projectId,
                effectiveSessionId);

        TextInput textInput = TextInput.newBuilder()
                .setText(message)
                .setLanguageCode("vi")
                .build();

        QueryInput queryInput = QueryInput.newBuilder()
                .setText(textInput)
                .build();

        DetectIntentRequest request = DetectIntentRequest.newBuilder()
                .setSession(session.toString())
                .setQueryInput(queryInput)
                .build();

        DetectIntentResponse response = sessionsClient.detectIntent(request);

        var queryResult = response.getQueryResult();

        String fulfillmentText = queryResult.getFulfillmentText();

        log.info("Dialogflow query: {}", queryResult.getQueryText());
        log.info("Dialogflow intent: {}",
                queryResult.getIntent().getDisplayName());
        log.info("Dialogflow fulfillmentText: [{}]", fulfillmentText);
        log.info("Dialogflow fulfillmentMessages: {}",
                queryResult.getFulfillmentMessagesList());
        log.info("Dialogflow webhookPayload: {}",
                queryResult.getWebhookPayload());
        log.info("Dialogflow diagnosticInfo: {}",
                queryResult.getDiagnosticInfo());

        if (fulfillmentText == null || fulfillmentText.isBlank()) {
            fulfillmentText = "Dạ, tôi chưa tìm được câu trả lời phù hợp. "
                    + "Bạn có thể diễn đạt câu hỏi theo cách khác không ạ?";
        }
        return new DialogflowResult(
                fulfillmentText,
                effectiveSessionId,
                queryResult.getIntent().getDisplayName());
    }

    @PreDestroy
    public void close() {
        sessionsClient.close();
    }

    public record DialogflowResult(
            String message,
            String sessionId,
            String intentName) {
    }
}