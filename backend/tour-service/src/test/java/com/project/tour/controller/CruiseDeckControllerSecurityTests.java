package com.project.tour.controller;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.project.tour.config.JwtConfig;
import com.project.tour.config.SecurityConfig;
import com.project.tour.controller.cruise.CruiseDeckController;
import com.project.tour.exception.DuplicateResourceException;
import com.project.tour.service.cruise.CruiseDeckService;

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
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CruiseDeckController.class)
@Import({ SecurityConfig.class, JwtConfig.class })
@TestPropertySource(properties = {
                "jwt.secret=cruise-management-system-local-secret-key-2026"
})
class CruiseDeckControllerSecurityTests {

        private static final String SECRET = "cruise-management-system-local-secret-key-2026";

        private static final UUID CRUISE_ID = UUID.randomUUID();

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private CruiseDeckService cruiseDeckService;

        @Test
        void requestWithoutTokenReturnsUnauthorized() throws Exception {
                mockMvc.perform(get(decksUrl()))
                                .andExpect(status().isUnauthorized());
        }

        @Test
        void passengerCannotCreateDeck() throws Exception {
                performCreate("PASSENGER", 1)
                                .andExpect(status().isForbidden());
        }

        @Test
        void schedulerCannotCreateDeck() throws Exception {
                performCreate("SCHEDULER", 1)
                                .andExpect(status().isForbidden());
        }

        @Test
        void adminCanCreateDeck() throws Exception {
                performCreate("ADMIN", 1)
                                .andExpect(status().isCreated());
        }

        @Test
        void authenticatedPassengerCanReadDecks() throws Exception {
                mockMvc.perform(get(decksUrl())
                                .header("Authorization", "Bearer " + accessToken("PASSENGER")))
                                .andExpect(status().isOk());
        }

        @Test
        void refreshTokenIsRejected() throws Exception {
                mockMvc.perform(get(decksUrl())
                                .header("Authorization", "Bearer " + token(null, "REFRESH")))
                                .andExpect(status().isUnauthorized());
        }

        @Test
        void deckAboveCruiseLimitReturnsBadRequest() throws Exception {
                when(cruiseDeckService.createCruiseDeck(
                                eq(CRUISE_ID),
                                any())).thenThrow(new IllegalArgumentException(
                                                "Deck number must not exceed cruise total decks: 12"));

                performCreate("ADMIN", 13)
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.status").value(400));
        }

        @Test
        void duplicateDeckReturnsConflict() throws Exception {
                when(cruiseDeckService.createCruiseDeck(
                                eq(CRUISE_ID),
                                any())).thenThrow(new DuplicateResourceException(
                                                "Deck number 1 already exists in cruise: " + CRUISE_ID));

                performCreate("ADMIN", 1)
                                .andExpect(status().isConflict())
                                .andExpect(jsonPath("$.status").value(409));
        }

        private org.springframework.test.web.servlet.ResultActions performCreate(
                        String role,
                        int deckNumber) throws Exception {
                return mockMvc.perform(post(decksUrl())
                                .header("Authorization", "Bearer " + accessToken(role))
                                .contentType("application/json")
                                .content("{\"deckNumber\":" + deckNumber + "}"));
        }

        private String decksUrl() {
                return "/api/v1/cruises/" + CRUISE_ID + "/decks";
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
                                MacAlgorithm.HS256.getName());

                NimbusJwtEncoder encoder = new NimbusJwtEncoder(
                                new ImmutableSecret<>(key));

                JwsHeader header = JwsHeader
                                .with(MacAlgorithm.HS256)
                                .build();

                return encoder.encode(
                                JwtEncoderParameters.from(header, claims.build())).getTokenValue();
        }
}
