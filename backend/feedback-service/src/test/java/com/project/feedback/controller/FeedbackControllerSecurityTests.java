package com.project.feedback.controller;

import com.project.feedback.config.*;
import com.project.feedback.service.FeedbackService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.util.UUID;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({FeedbackController.class, AdminFeedbackController.class})
@Import({SecurityConfig.class, JwtConfig.class})
@TestPropertySource(properties = "jwt.secret=cruise-management-system-local-secret-key-2026")
class FeedbackControllerSecurityTests {
    @Autowired MockMvc mockMvc; @MockitoBean FeedbackService service;
    UUID id = UUID.fromString("11111111-1111-1111-1111-111111111111");
    @Test void publicCanReadTourFeedback() throws Exception { mockMvc.perform(get("/api/v1/feedbacks/tours/{id}", id)).andExpect(status().isOk()); }
    @Test void createRequiresAuthentication() throws Exception { mockMvc.perform(post("/api/v1/feedbacks")
        .contentType("application/json").content(body())).andExpect(status().isUnauthorized()); }
    @Test void passengerCanCreateFeedback() throws Exception { mockMvc.perform(post("/api/v1/feedbacks")
        .with(jwt().jwt(j -> j.claim("userId", 7L))).contentType("application/json").content(body())).andExpect(status().isCreated()); }
    @Test void passengerCannotModerate() throws Exception { mockMvc.perform(patch("/api/admin/feedbacks/1/moderation")
        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_PASSENGER")))
        .contentType("application/json").content("{\"status\":\"HIDDEN\",\"reason\":\"Spam\"}"))
        .andExpect(status().isForbidden()); }
    @Test void adminCanModerate() throws Exception { mockMvc.perform(patch("/api/admin/feedbacks/1/moderation")
        .with(jwt().jwt(j -> j.claim("userId", 1L)).authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
        .contentType("application/json").content("{\"status\":\"HIDDEN\",\"reason\":\"Spam\"}"))
        .andExpect(status().isOk()); }
    private String body() { return "{\"bookingId\":10,\"rating\":5,\"content\":\"Excellent\",\"imageUrls\":[]}"; }
}
