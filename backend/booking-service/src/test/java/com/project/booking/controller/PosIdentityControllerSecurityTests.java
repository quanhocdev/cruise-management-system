package com.project.booking.controller;

import com.project.booking.config.*;
import com.project.booking.service.PosIdentityService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.verifyNoInteractions;

@WebMvcTest(PosIdentityController.class)
@Import({SecurityConfig.class, JwtConfig.class})
@TestPropertySource(properties = "jwt.secret=cruise-management-system-local-secret-key-2026")
class PosIdentityControllerSecurityTests {
    @Autowired MockMvc mvc;
    @MockitoBean PosIdentityService service;
    private static final String BODY = "{\"passengerVoyageId\":7,\"scanType\":\"QR\"}";

    @Test void guestCannotIssueCredential() throws Exception {
        mvc.perform(post("/api/admin/pos-terminals/credentials").contentType("application/json").content(BODY))
            .andExpect(status().isUnauthorized());
        verifyNoInteractions(service);
    }
    @Test void passengerCannotIssueCredential() throws Exception {
        mvc.perform(post("/api/admin/pos-terminals/credentials").with(jwt().authorities(new SimpleGrantedAuthority("ROLE_PASSENGER")))
            .contentType("application/json").content(BODY)).andExpect(status().isForbidden());
    }
    @Test void adminCanIssueCredential() throws Exception {
        mvc.perform(post("/api/admin/pos-terminals/credentials").with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
            .contentType("application/json").content(BODY)).andExpect(status().isCreated());
    }
    @Test void identityNeedsDeviceHeaders() throws Exception {
        mvc.perform(post("/api/v1/pos/identify").contentType("application/json")
            .content("{\"scanType\":\"NFC\",\"scannedValue\":\"04A1B2C3\"}"))
            .andExpect(status().isUnauthorized());
        verifyNoInteractions(service);
    }
    @Test void malformedPayloadRejectedBeforeService() throws Exception {
        mvc.perform(post("/api/v1/pos/identify").header("X-Terminal-Code", "POS-TEST").header("X-POS-Key", "test")
            .contentType("application/json").content("{\"scanType\":\"OTHER\",\"scannedValue\":\"x\"}"))
            .andExpect(status().isBadRequest());
        verifyNoInteractions(service);
    }
    @Test void checkInNeedsDeviceHeaders() throws Exception {
        mvc.perform(post("/api/v1/pos/check-in").contentType("application/json")
            .content("{\"scanType\":\"NFC\",\"scannedValue\":\"04A1B2C3\"}"))
            .andExpect(status().isUnauthorized());
        verifyNoInteractions(service);
    }
}
