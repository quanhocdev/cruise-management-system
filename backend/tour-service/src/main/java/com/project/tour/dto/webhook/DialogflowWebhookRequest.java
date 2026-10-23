package com.project.tour.dto.webhook;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DialogflowWebhookRequest(
        String responseId,
        QueryResult queryResult) {
    public record QueryResult(
            String queryText,
            Map<String, Object> parameters,
            Intent intent) {
    }

    public record Intent(
            String name,
            String displayName) {
    }
}