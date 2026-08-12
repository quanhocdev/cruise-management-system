package com.project.tour.controller;

import com.project.tour.config.JwtConfig;
import com.project.tour.config.SecurityConfig;
import com.project.tour.controller.cruise.CruiseAreaController;
import com.project.tour.service.CruiseAreaService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CruiseAreaController.class)
@Import({ SecurityConfig.class, JwtConfig.class })
@TestPropertySource(properties = "jwt.secret=cruise-management-system-local-secret-key-2026")
class CruiseAreaControllerSecurityTests {

    private static final String URL = "/api/v1/decks/" + UUID.randomUUID() + "/areas";

    @Autowired
    MockMvc mockMvc;
    @MockitoBean
    CruiseAreaService areaService;

    @Test
    void noTokenReturnsUnauthorized() throws Exception {
        mockMvc.perform(get(URL)).andExpect(status().isUnauthorized());
    }

    @Test
    void passengerCannotCreateArea() throws Exception {
        mockMvc.perform(post(URL)
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_PASSENGER")))
                .contentType("application/json")
                .content(validBody()))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminCanCreateArea() throws Exception {
        mockMvc.perform(post(URL)
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .contentType("application/json")
                .content(validBody()))
                .andExpect(status().isCreated());
    }

    @Test
    void passengerCanReadAreas() throws Exception {
        mockMvc.perform(get(URL)
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_PASSENGER"))))
                .andExpect(status().isOk());
    }

    private String validBody() {
        return "{\"name\":\"Restaurant\",\"description\":\"Main dining area\"}";
    }
}
