package com.project.tour.controller;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.project.tour.config.JwtConfig;
import com.project.tour.config.SecurityConfig;
import com.project.tour.exception.DuplicateResourceException;
import com.project.tour.service.CruiseService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CruiseController.class)
@Import({SecurityConfig.class, JwtConfig.class})
@TestPropertySource(properties = {
    "jwt.secret=cruise-management-system-local-secret-key-2026"
})
class CruiseControllerSecurityTests {

    private static final String SECRET =
        "cruise-management-system-local-secret-key-2026";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CruiseService cruiseService;

    @Test
    void requestWithoutTokenReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/cruises"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void passengerCannotCreateCruise() throws Exception {
        performCreate("PASSENGER")
            .andExpect(status().isForbidden());
    }

    @Test
    void schedulerCannotCreateCruise() throws Exception {
        performCreate("SCHEDULER")
            .andExpect(status().isForbidden());
    }

    @Test
    void adminCanCreateCruise() throws Exception {
        performCreate("ADMIN")
            .andExpect(status().isCreated());
    }

    @Test
    void authenticatedPassengerCanReadCruises() throws Exception {
        mockMvc.perform(get("/api/v1/cruises")
                .header("Authorization", "Bearer " + accessToken("PASSENGER")))
            .andExpect(status().isOk());
    }

    @Test
    void refreshTokenIsRejected() throws Exception {
        mockMvc.perform(get("/api/v1/cruises")
                .header("Authorization", "Bearer " + token(null, "REFRESH")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void duplicateCruiseCodeReturnsConflict() throws Exception {
        when(cruiseService.createCruise(any()))
            .thenThrow(new DuplicateResourceException(
                "Cruise code already exists: OCEAN-STAR-01"
            ));

        performCreate("ADMIN")
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.status").value(409))
            .andExpect(jsonPath("$.message").value(
                "Cruise code already exists: OCEAN-STAR-01"
            ));
    }

    private org.springframework.test.web.servlet.ResultActions performCreate(
        String role
    ) throws Exception {
        return mockMvc.perform(post("/api/v1/cruises")
            .header("Authorization", "Bearer " + accessToken(role))
            .contentType("application/json")
            .content(validCruiseJson()));
    }

    private String accessToken(String role) {
        return token(role, "ACCESS");
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

    private String validCruiseJson() {
        return """
            {
              "name": "Ocean Star",
              "code": "OCEAN-STAR-01",
              "description": "Multi-day cruise",
              "totalDecks": 12,
              "maxPassengers": 2500
            }
            """;
    }
}
