package com.project.tour.controller;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.project.tour.config.JwtConfig;
import com.project.tour.config.SecurityConfig;
import com.project.tour.service.PortService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PortController.class)
@Import({SecurityConfig.class, JwtConfig.class})
@TestPropertySource(properties = {
    "jwt.secret=cruise-management-system-local-secret-key-2026"
})
class PortControllerSecurityTests {

    private static final String SECRET =
        "cruise-management-system-local-secret-key-2026";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PortService portService;

    @Test
    void requestWithoutTokenReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/ports"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void passengerCannotCreatePort() throws Exception {
        mockMvc.perform(post("/api/v1/ports")
                .header("Authorization", "Bearer " + accessToken("PASSENGER"))
                .contentType("application/json")
                .content(validPortJson()))
            .andExpect(status().isForbidden());
    }

    @Test
    void schedulerCanCreatePort() throws Exception {
        mockMvc.perform(post("/api/v1/ports")
                .header("Authorization", "Bearer " + accessToken("SCHEDULER"))
                .contentType("application/json")
                .content(validPortJson()))
            .andExpect(status().isCreated());
    }

    @Test
    void authenticatedPassengerCanReadPorts() throws Exception {
        mockMvc.perform(get("/api/v1/ports")
                .header("Authorization", "Bearer " + accessToken("PASSENGER")))
            .andExpect(status().isOk());
    }

    @Test
    void refreshTokenIsRejected() throws Exception {
        mockMvc.perform(get("/api/v1/ports")
                .header("Authorization", "Bearer " + refreshToken()))
            .andExpect(status().isUnauthorized());
    }

    private String accessToken(String role) {
        return token(role, "ACCESS");
    }

    private String refreshToken() {
        return token(null, "REFRESH");
    }

    private String token(String role, String tokenType) {
        Instant now = Instant.now();

        JwtClaimsSet.Builder claims = JwtClaimsSet.builder()
            .subject("test-user-id")
            .issuedAt(now)
            .expiresAt(now.plusSeconds(300))
            .claim("tokenType", tokenType);

        if (role != null) {
            claims.claim("scope", role);
        }

        SecretKey key = new SecretKeySpec(
            SECRET.getBytes(StandardCharsets.UTF_8),
            MacAlgorithm.HS256.getName()
        );

        NimbusJwtEncoder encoder = new NimbusJwtEncoder(
            new ImmutableSecret<>(key)
        );

        JwsHeader header = JwsHeader
            .with(MacAlgorithm.HS256)
            .build();

        return encoder.encode(
            JwtEncoderParameters.from(header, claims.build())
        ).getTokenValue();
    }

    private String validPortJson() {
        return """
            {
              "name": "Saigon Port",
              "city": "Ho Chi Minh City",
              "country": "Vietnam",
              "address": "District 4",
              "latitude": 10.7598,
              "longitude": 106.7072,
              "description": "Passenger port"
            }
            """;
    }
}
