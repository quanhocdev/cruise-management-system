
package com.project.chat.service.chat;

import com.google.cloud.dialogflow.v2.DetectIntentRequest;
import com.google.cloud.dialogflow.v2.DetectIntentResponse;
import com.google.cloud.dialogflow.v2.EventInput;
import com.google.cloud.dialogflow.v2.QueryInput;
import com.google.cloud.dialogflow.v2.SessionName;
import com.google.cloud.dialogflow.v2.SessionsClient;
import com.google.cloud.dialogflow.v2.TextInput;

import jakarta.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.UUID;

@Service
public class DialogflowClientService {

    private static final Logger log = LoggerFactory.getLogger(DialogflowClientService.class);

    private static final String LANGUAGE_CODE = "vi";
    private static final String WELCOME_EVENT = "WELCOME";

    private final String projectId;
    private final SessionsClient sessionsClient;

    public DialogflowClientService(
            @Value("${google.cloud.dialogflow.project-id}") String projectId)
            throws IOException {

        if (projectId == null || projectId.isBlank()) {
            throw new IllegalArgumentException(
                    "DIALOGFLOW_PROJECT_ID chưa được cấu hình");
        }

        this.projectId = projectId.trim();
        this.sessionsClient = SessionsClient.create();

        log.info("Dialogflow SessionsClient initialized for project: {}",
                this.projectId);
    }

    public DialogflowResult detectIntent(
            String message,
            String sessionId) {

        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException(
                    "Nội dung tin nhắn không được để trống");
        }

        String effectiveSessionId = resolveSessionId(sessionId);

        SessionName session = SessionName.of(
                projectId,
                effectiveSessionId);

        TextInput textInput = TextInput.newBuilder()
                .setText(message.trim())
                .setLanguageCode(LANGUAGE_CODE)
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

        String intentName = queryResult.hasIntent()
                ? queryResult.getIntent().getDisplayName()
                : null;

        log.info("Dialogflow query: {}", queryResult.getQueryText());
        log.info("Dialogflow intent: {}", intentName);
        log.info("Dialogflow fulfillmentText: [{}]", fulfillmentText);
        log.debug("Dialogflow fulfillmentMessages: {}",
                queryResult.getFulfillmentMessagesList());
        log.debug("Dialogflow webhookPayload: {}",
                queryResult.getWebhookPayload());
        log.debug("Dialogflow diagnosticInfo: {}",
                queryResult.getDiagnosticInfo());

        if (fulfillmentText == null || fulfillmentText.isBlank()) {
            fulfillmentText = "Dạ, tôi chưa tìm được câu trả lời phù hợp. "
                    + "Bạn có thể diễn đạt câu hỏi theo cách khác không ạ?";
        }

        return new DialogflowResult(
                fulfillmentText,
                effectiveSessionId,
                intentName);
    }

    public DialogflowResult detectWelcomeIntent(String sessionId) {

        String effectiveSessionId = resolveSessionId(sessionId);

        SessionName session = SessionName.of(
                projectId,
                effectiveSessionId);

        EventInput eventInput = EventInput.newBuilder()
                .setName(WELCOME_EVENT)
                .setLanguageCode(LANGUAGE_CODE)
                .build();

        QueryInput queryInput = QueryInput.newBuilder()
                .setEvent(eventInput)
                .build();

        DetectIntentRequest request = DetectIntentRequest.newBuilder()
                .setSession(session.toString())
                .setQueryInput(queryInput)
                .build();

        DetectIntentResponse response = sessionsClient.detectIntent(request);

        var queryResult = response.getQueryResult();

        String fulfillmentText = queryResult.getFulfillmentText();

        if (fulfillmentText == null || fulfillmentText.isBlank()) {
            fulfillmentText = "Dạ chào quý khách! Tôi có thể hỗ trợ gì cho bạn ạ?";
        }

        String intentName = queryResult.hasIntent()
                ? queryResult.getIntent().getDisplayName()
                : null;

        log.info("Dialogflow welcome intent: {}", intentName);
        log.info("Dialogflow welcome response: {}", fulfillmentText);

        return new DialogflowResult(
                fulfillmentText,
                effectiveSessionId,
                intentName);
    }

    private String resolveSessionId(String sessionId) {

        String effectiveSessionId = sessionId == null || sessionId.isBlank()
                ? UUID.randomUUID().toString()
                : sessionId.trim();

        if (!effectiveSessionId.matches("[a-zA-Z0-9_-]{1,36}")) {
            throw new IllegalArgumentException(
                    "Session ID không hợp lệ");
        }

        return effectiveSessionId;
    }

    @PreDestroy
    public void close() {
        if (sessionsClient != null) {
            sessionsClient.close();
            log.info("Dialogflow SessionsClient closed");
        }
    }

    public record DialogflowResult(
            String message,
            String sessionId,
            String intentName) {
    }
}
