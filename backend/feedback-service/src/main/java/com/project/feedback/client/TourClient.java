package com.project.feedback.client;

import com.project.feedback.exception.FeedbackException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import java.util.UUID;

@Component
public class TourClient {
    private final RestClient client; private final String apiKey;
    public TourClient(RestClient.Builder builder, @Value("${tour-service.url}") String url,
                      @Value("${internal.api-key}") String apiKey) {
        this.client = builder.baseUrl(url).build(); this.apiKey = apiKey;
    }
    public FeedbackTourContext context(UUID tourId) {
        try {
            FeedbackTourContext result = client.get().uri("/internal/tours/{id}/feedback-context", tourId)
                .header("X-Internal-Api-Key", apiKey).retrieve().body(FeedbackTourContext.class);
            if (result == null) throw new FeedbackException(HttpStatus.BAD_GATEWAY, "Tour service returned an empty response");
            return result;
        } catch (FeedbackException ex) { throw ex; }
        catch (RuntimeException ex) { throw new FeedbackException(HttpStatus.BAD_GATEWAY, "Cannot verify tour completion"); }
    }

    public FeedbackTargetContext targetContext(UUID tourId, String targetType, UUID targetId) {
        try {
            FeedbackTargetContext result = client.get()
                .uri("/internal/tours/{tourId}/feedback-targets/{targetType}/{targetId}", tourId, targetType, targetId)
                .header("X-Internal-Api-Key", apiKey).retrieve().body(FeedbackTargetContext.class);
            if (result == null) throw new FeedbackException(HttpStatus.BAD_GATEWAY, "Tour service returned an empty response");
            return result;
        } catch (FeedbackException ex) { throw ex; }
        catch (RuntimeException ex) { throw new FeedbackException(HttpStatus.BAD_GATEWAY, "Cannot verify feedback target"); }
    }
}
