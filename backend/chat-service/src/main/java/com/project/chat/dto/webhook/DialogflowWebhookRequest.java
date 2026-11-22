
package com.project.chat.dto.webhook;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DialogflowWebhookRequest(
        String responseId,
        QueryResult queryResult) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record QueryResult(
            String queryText,
            Map<String, Object> parameters,
            Intent intent) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Intent(
            String name,
            String displayName) {
    }
}
