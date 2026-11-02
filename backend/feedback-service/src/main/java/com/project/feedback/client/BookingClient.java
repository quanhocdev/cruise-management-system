package com.project.feedback.client;

import java.util.UUID;

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
        this.client = builder.clone().baseUrl(url).build(); this.apiKey = apiKey;
    }
    public FeedbackEligibility eligibility(Long bookingId, UUID userId) {
        return eligibility(bookingId, userId, null);
    }
    public record Guests(Long bookingId, java.util.UUID tourId,
                         java.util.List<com.project.feedback.dto.FeedbackOptions.Guest> passengers) {}
    public Guests passengers(Long bookingId,UUID userId) {
        try {
            Guests result=client.get().uri("/internal/bookings/{id}/feedback-passengers?userId={userId}",bookingId,userId)
                .header("X-Internal-Api-Key",apiKey).retrieve().body(Guests.class);
            if(result==null || !bookingId.equals(result.bookingId()) || result.tourId()==null || result.passengers()==null)
                throw new FeedbackException(HttpStatus.BAD_GATEWAY,"Invalid booking feedback context");
            return result;
        } catch (FeedbackException e) { throw e; }
        catch (org.springframework.web.client.HttpClientErrorException e) {
            if(e.getStatusCode().value()==404) throw new FeedbackException(HttpStatus.NOT_FOUND,"Booking is unavailable for feedback");
            throw new FeedbackException(HttpStatus.BAD_GATEWAY,"Cannot verify booking participation");
        } catch (RuntimeException e) { throw new FeedbackException(HttpStatus.BAD_GATEWAY,"Cannot verify booking participation"); }
    }
    public FeedbackEligibility eligibility(Long bookingId, UUID userId, Long bookingPassengerId) {
        try {
            FeedbackEligibility result = client.get()
                .uri(uri -> {
                    uri.path("/internal/bookings/{id}/feedback-eligibility").queryParam("userId", userId);
                    if (bookingPassengerId != null) uri.queryParam("bookingPassengerId", bookingPassengerId);
                    return uri.build(bookingId);
                })
                .header("X-Internal-Api-Key", apiKey).retrieve().body(FeedbackEligibility.class);
            if (result == null) throw new FeedbackException(HttpStatus.BAD_GATEWAY, "Booking service returned an empty response");
            return result;
        } catch (FeedbackException ex) { throw ex; }
        catch (org.springframework.web.client.HttpClientErrorException ex) {
            if (ex.getStatusCode().value() == 403 || ex.getStatusCode().value() == 404)
                throw new FeedbackException(HttpStatus.NOT_FOUND, "Booking is unavailable for feedback");
            if (ex.getStatusCode().value() == 422)
                throw new FeedbackException(HttpStatus.UNPROCESSABLE_ENTITY, "Select a passenger from this booking");
            throw new FeedbackException(HttpStatus.BAD_GATEWAY, "Cannot verify booking participation");
        }
        catch (RuntimeException ex) { throw new FeedbackException(HttpStatus.BAD_GATEWAY, "Cannot verify booking participation"); }
    }
}
