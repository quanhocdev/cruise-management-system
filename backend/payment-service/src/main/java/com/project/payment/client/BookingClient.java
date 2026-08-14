package com.project.payment.client;

import com.project.payment.exception.PaymentException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class BookingClient {
    private final RestClient client;
    private final String apiKey;

    public BookingClient(RestClient.Builder builder,
        @Value("${booking-service.url:http://booking-service:8082}") String baseUrl,
        @Value("${internal.api-key}") String apiKey) {
        this.client = builder.baseUrl(baseUrl).build(); this.apiKey = apiKey;
    }

    public BookingPaymentContext getPaymentContext(Long bookingId) {
        try {
            BookingPaymentContext context = client.get()
                .uri("/internal/bookings/{id}/payment-context", bookingId)
                .header("X-Internal-Api-Key", apiKey)
                .retrieve().body(BookingPaymentContext.class);
            if (context == null) throw new PaymentException("Booking service returned an empty response");
            return context;
        } catch (PaymentException ex) { throw ex; }
        catch (Exception ex) { throw new PaymentException("Cannot validate booking: " + ex.getMessage()); }
    }

    public void confirmPayment(Long bookingId, Long paymentId) {
        try {
            client.put().uri("/internal/bookings/{id}/payment-confirmation", bookingId)
                .header("X-Internal-Api-Key", apiKey).contentType(MediaType.APPLICATION_JSON)
                .body(new ConfirmPaymentRequest(paymentId)).retrieve().toBodilessEntity();
        } catch (Exception ex) { throw new PaymentException("Cannot confirm booking payment: " + ex.getMessage()); }
    }

    private record ConfirmPaymentRequest(Long paymentId) {}
}
