package com.project.tour.controller.passenger;

import com.project.tour.config.*;
import com.project.tour.service.passenger.PassengerCatalogService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PassengerCatalogController.class)
@Import({SecurityConfig.class, JwtConfig.class})
@TestPropertySource(properties = "jwt.secret=cruise-management-system-local-secret-key-2026")
class PassengerCatalogControllerSecurityTests {
    @Autowired MockMvc mockMvc;
    @MockitoBean PassengerCatalogService service;

    @Test void catalogRequiresAuthentication() throws Exception {
        mockMvc.perform(get("/api/passenger/tours")).andExpect(status().isUnauthorized());
    }

    @Test void nonPassengerCannotReadCatalog() throws Exception {
        mockMvc.perform(get("/api/passenger/tours")
            .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
            .andExpect(status().isForbidden());
    }

    @Test void passengerCanReadCatalog() throws Exception {
        when(service.getOpenTours()).thenReturn(List.of());
        mockMvc.perform(get("/api/passenger/tours")
            .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_PASSENGER"))))
            .andExpect(status().isOk());
    }
}
