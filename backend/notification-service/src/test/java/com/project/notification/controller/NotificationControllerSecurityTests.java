package com.project.notification.controller;

import com.project.notification.config.*;
import com.project.notification.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({NotificationController.class, InternalNotificationController.class})
@Import({SecurityConfig.class, JwtConfig.class})
@TestPropertySource(properties = {"jwt.secret=cruise-management-system-local-secret-key-2026", "internal.api-key=test-key"})
class NotificationControllerSecurityTests {
    @Autowired MockMvc mockMvc;
    @MockitoBean NotificationService service;
    @Test void userEndpointsRequireJwt() throws Exception { mockMvc.perform(get("/api/v1/notifications")).andExpect(status().isUnauthorized()); }
    @Test void authenticatedUserCanListOwnNotifications() throws Exception {
        mockMvc.perform(get("/api/v1/notifications").with(jwt().jwt(j -> j.claim("userId", 7L)))).andExpect(status().isOk());
    }
    @Test void internalCreateRejectsMissingKey() throws Exception {
        mockMvc.perform(post("/internal/notifications").contentType("application/json").content(body())).andExpect(status().isUnauthorized());
    }
    @Test void internalCreateAcceptsCorrectKey() throws Exception {
        mockMvc.perform(post("/internal/notifications").header("X-Internal-Api-Key", "test-key")
            .contentType("application/json").content(body())).andExpect(status().isCreated());
    }
    private String body() { return "{\"recipientUserId\":7,\"type\":\"PAYMENT_SUCCESS\",\"title\":\"Paid\",\"message\":\"Done\"}"; }
}
