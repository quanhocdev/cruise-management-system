package com.project.tour.controller;

import com.project.tour.config.JwtConfig;
import com.project.tour.config.SecurityConfig;
import com.project.tour.service.ScheduleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ScheduleController.class)
@Import({SecurityConfig.class, JwtConfig.class})
@TestPropertySource(properties = "jwt.secret=cruise-management-system-local-secret-key-2026")
class ScheduleControllerSecurityTests {
    @Autowired MockMvc mockMvc;
    @MockitoBean ScheduleService service;
    @Test void noTokenReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/schedules")).andExpect(status().isUnauthorized());
    }
    @Test void passengerCanReadSchedules() throws Exception {
        mockMvc.perform(get("/api/v1/schedules").with(role("PASSENGER"))).andExpect(status().isOk());
    }
    @Test void passengerCannotCreateSchedule() throws Exception {
        mockMvc.perform(post("/api/v1/schedules").with(role("PASSENGER"))
            .contentType("application/json").content(validBody())).andExpect(status().isForbidden());
    }
    @Test void schedulerCanCreateSchedule() throws Exception {
        mockMvc.perform(post("/api/v1/schedules").with(role("SCHEDULER"))
            .contentType("application/json").content(validBody())).andExpect(status().isCreated());
    }
    private org.springframework.test.web.servlet.request.RequestPostProcessor role(String role) {
        return jwt().authorities(new SimpleGrantedAuthority("ROLE_" + role));
    }
    private String validBody() {
        return "{\"tourPackageId\":\"00000000-0000-0000-0000-000000000001\","
            + "\"cruiseId\":\"00000000-0000-0000-0000-000000000002\",\"code\":\"HL-001\","
            + "\"startDate\":\"2026-09-01\",\"endDate\":\"2026-09-03\",\"capacity\":100}";
    }
}
