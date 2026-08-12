package com.project.tour.controller;

import com.project.tour.config.JwtConfig;
import com.project.tour.config.SecurityConfig;
import com.project.tour.controller.policy.PolicyController;
import com.project.tour.service.policy.PolicyService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PolicyController.class)
@Import({ SecurityConfig.class, JwtConfig.class })
@TestPropertySource(properties = "jwt.secret=cruise-management-system-local-secret-key-2026")
class PolicyControllerSecurityTests {
    @Autowired
    MockMvc mockMvc;
    @MockitoBean
    PolicyService policyService;

    @Test
    void noTokenReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/policies")).andExpect(status().isUnauthorized());
    }

    @Test
    void passengerCanReadPolicies() throws Exception {
        mockMvc.perform(get("/api/v1/policies").with(jwt().authorities(
                new SimpleGrantedAuthority("ROLE_PASSENGER")))).andExpect(status().isOk());
    }

    @Test
    void passengerCannotCreatePolicy() throws Exception {
        mockMvc.perform(post("/api/v1/policies").with(jwt().authorities(
                new SimpleGrantedAuthority("ROLE_PASSENGER")))
                .contentType("application/json").content(validBody()))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminCanCreatePolicy() throws Exception {
        mockMvc.perform(post("/api/v1/policies").with(jwt().authorities(
                new SimpleGrantedAuthority("ROLE_ADMIN")))
                .contentType("application/json").content(validBody()))
                .andExpect(status().isCreated());
    }

    private String validBody() {
        return "{\"type\":\"CANCEL\",\"title\":\"Cancellation\",\"content\":\"Refund terms\"}";
    }
}
