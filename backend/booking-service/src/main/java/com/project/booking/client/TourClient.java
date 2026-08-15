package com.project.booking.client;

import com.project.booking.exception.BookingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import java.util.UUID;

@Component
public class TourClient {
    private final RestClient client; private final String apiKey;
    public TourClient(RestClient.Builder builder,
        @Value("${tour-service.url:http://tour-service:8088}") String baseUrl,
        @Value("${internal.api-key}") String apiKey) {
        this.client = builder.baseUrl(baseUrl).build(); this.apiKey = apiKey;
    }
    public TourScheduleContext getSchedule(UUID voyageId) {
        try {
            TourScheduleContext result = client.get().uri("/internal/tours/{id}/booking-context", voyageId)
                .header("X-Internal-Api-Key", apiKey).retrieve().body(TourScheduleContext.class);
            if (result == null) throw new BookingException(HttpStatus.BAD_GATEWAY, "Tour service returned an empty response");
            return result;
        } catch (BookingException ex) { throw ex; }
        catch (Exception ex) { throw new BookingException(HttpStatus.BAD_GATEWAY, "Cannot validate voyage: " + ex.getMessage()); }
    }
}
