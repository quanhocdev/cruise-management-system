package com.project.payment.controller;

import com.project.payment.config.*;
import com.project.payment.dto.*;
import com.project.payment.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({PaymentController.class, PaymentCallbackController.class})
@Import({SecurityConfig.class, JwtConfig.class})
@TestPropertySource(properties = {
    "jwt.secret=cruise-management-system-local-secret-key-2026",
    "vnpay.frontend-result-url=http://localhost:5173/payment/result"
})
class PaymentControllerSecurityTests {
    @Autowired MockMvc mockMvc;
    @MockitoBean PaymentService service;

    @Test void createRequiresToken() throws Exception {
        mockMvc.perform(post("/api/v1/payments").contentType("application/json").content(validBody()))
            .andExpect(status().isUnauthorized());
    }
    @Test void passengerCanCreatePayment() throws Exception {
        when(service.createPayment(any(), eq(7L), anyString())).thenReturn(new PaymentResponse());
        mockMvc.perform(post("/api/v1/payments")
            .with(jwt().jwt(j -> j.claim("userId", 7L)).authorities(new SimpleGrantedAuthority("ROLE_PASSENGER")))
            .contentType("application/json").content(validBody())).andExpect(status().isCreated());
    }
    @Test void vnPayIpnIsPublic() throws Exception {
        when(service.handleVnPayIpn(anyMap())).thenReturn(new VnPayIpnResponse("00", "Confirm success"));
        mockMvc.perform(get("/api/v1/payments/vnpay/ipn?vnp_TxnRef=10"))
            .andExpect(status().isOk());
    }
    @Test void createPaymentRejectsFractionalVndAmount() throws Exception {
        mockMvc.perform(post("/api/v1/payments")
            .with(jwt().jwt(j -> j.claim("userId", 7L)).authorities(new SimpleGrantedAuthority("ROLE_PASSENGER")))
            .contentType("application/json")
            .content("{\"referenceId\":100,\"referenceType\":\"BOOKING\","
                + "\"amount\":1000.50,\"method\":\"VNPAY\"}"))
            .andExpect(status().isBadRequest());
    }
    private String validBody() {
        return "{\"referenceId\":100,\"referenceType\":\"BOOKING\","
            + "\"amount\":1000000,\"method\":\"VNPAY\"}";
    }
}
