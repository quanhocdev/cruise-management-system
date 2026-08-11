package com.project.tour.controller;

import com.project.tour.config.JwtConfig;
import com.project.tour.config.SecurityConfig;
import com.project.tour.service.TourPackageService;
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

@WebMvcTest(TourPackageController.class)
@Import({SecurityConfig.class, JwtConfig.class})
@TestPropertySource(properties = "jwt.secret=cruise-management-system-local-secret-key-2026")
class TourPackageControllerSecurityTests {

    @Autowired MockMvc mockMvc;
    @MockitoBean TourPackageService service;

    @Test
    void noTokenReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/packages")).andExpect(status().isUnauthorized());
    }

    @Test
    void passengerCanReadPackages() throws Exception {
        mockMvc.perform(get("/api/v1/packages").with(role("PASSENGER")))
            .andExpect(status().isOk());
    }

    @Test
    void passengerCannotCreatePackage() throws Exception {
        mockMvc.perform(post("/api/v1/packages").with(role("PASSENGER"))
                .contentType("application/json").content(validBody()))
            .andExpect(status().isForbidden());
    }

    @Test
    void schedulerCanCreatePackage() throws Exception {
        mockMvc.perform(post("/api/v1/packages").with(role("SCHEDULER"))
                .contentType("application/json").content(validBody()))
            .andExpect(status().isCreated());
    }

    @Test
    void adminCanCreatePackage() throws Exception {
        mockMvc.perform(post("/api/v1/packages").with(role("ADMIN"))
                .contentType("application/json").content(validBody()))
            .andExpect(status().isCreated());
    }

    private org.springframework.test.web.servlet.request.RequestPostProcessor role(String role) {
        return jwt().authorities(new SimpleGrantedAuthority("ROLE_" + role));
    }

    private String validBody() {
        return "{\"name\":\"Ha Long Discovery\",\"numberOfDays\":3,"
            + "\"numberOfNights\":2,\"description\":\"Three-day cruise\"}";
    }
}
