package com.project.feedback.client;

import com.project.feedback.exception.FeedbackException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class BookingClient {
    private final RestClient client; private final String apiKey;
    public BookingClient(RestClient.Builder builder, @Value("${booking-service.url}") String url,
                         @Value("${internal.api-key}") String apiKey) {
        this.client = builder.baseUrl(url).build(); this.apiKey = apiKey;
    }
    public FeedbackEligibility eligibility(Long bookingId, Long userId) {
        try {
            FeedbackEligibility result = client.get()
                .uri(uri -> uri.path("/internal/bookings/{id}/feedback-eligibility").queryParam("userId", userId).build(bookingId))
                .header("X-Internal-Api-Key", apiKey).retrieve().body(FeedbackEligibility.class);
            if (result == null) throw new FeedbackException(HttpStatus.BAD_GATEWAY, "Booking service returned an empty response");
            return result;
        } catch (FeedbackException ex) { throw ex; }
        catch (RuntimeException ex) { throw new FeedbackException(HttpStatus.BAD_GATEWAY, "Cannot verify booking participation"); }
    }
}
