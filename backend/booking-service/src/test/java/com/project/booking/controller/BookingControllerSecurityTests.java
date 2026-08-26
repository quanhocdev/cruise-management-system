package com.project.booking.controller;

import com.project.booking.config.*;
import com.project.booking.dto.*;
import com.project.booking.service.BookingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.math.BigDecimal;
import java.util.UUID;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({BookingController.class, InternalBookingController.class})
@Import({SecurityConfig.class, JwtConfig.class})
@TestPropertySource(properties = {
    "jwt.secret=cruise-management-system-local-secret-key-2026",
    "internal.api-key=test-internal-key"
})
class BookingControllerSecurityTests {
    @Autowired MockMvc mockMvc;
    @MockitoBean BookingService service;

    @Test void createRequiresJwt() throws Exception {
        mockMvc.perform(post("/api/v1/bookings").contentType("application/json").content(body()))
            .andExpect(status().isUnauthorized());
    }
    @Test void authenticatedUserCanCreate() throws Exception {
        when(service.create(any(), eq(7L))).thenReturn(null);
        mockMvc.perform(post("/api/v1/bookings").with(jwt().jwt(j -> j.claim("userId", 7L)))
            .contentType("application/json").content(body())).andExpect(status().isCreated());
    }
    @Test void availableRoomsRequiresJwt() throws Exception {
        mockMvc.perform(get("/api/v1/bookings/voyages/{id}/available-rooms", UUID.randomUUID()))
            .andExpect(status().isUnauthorized());
    }
    @Test void internalEndpointRejectsMissingKey() throws Exception {
        mockMvc.perform(get("/internal/bookings/1/payment-context")).andExpect(status().isUnauthorized());
    }
    @Test void internalEndpointAcceptsCorrectKey() throws Exception {
        when(service.getPaymentContext(1L)).thenReturn(new BookingPaymentContext(
            1L, 7L, new BigDecimal("1000000"), com.project.booking.model.enums.BookingStatus.PENDING_PAYMENT));
        mockMvc.perform(get("/internal/bookings/1/payment-context")
            .header("X-Internal-Api-Key", "test-internal-key")).andExpect(status().isOk());
    }
    private String body() {
        return "{\"voyageId\":\"11111111-1111-1111-1111-111111111111\","
            + "\"primaryContactName\":\"Nguyen Van A\",\"primaryContactPhone\":\"0900000000\","
            + "\"totalAmount\":1000000,\"passengers\":[{\"fullName\":\"Nguyen Van A\","
            + "\"dateOfBirth\":\"1990-01-01\",\"gender\":\"MALE\"}]}";
    }
}
