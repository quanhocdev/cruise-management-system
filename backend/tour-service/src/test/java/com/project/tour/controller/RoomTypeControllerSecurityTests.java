package com.project.tour.controller;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.project.tour.config.JwtConfig;
import com.project.tour.config.SecurityConfig;
import com.project.tour.controller.room.RoomTypeController;
import com.project.tour.service.room.RoomTypeService;

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

@WebMvcTest(RoomTypeController.class)
@Import({ SecurityConfig.class, JwtConfig.class })
@TestPropertySource(properties = {
        "jwt.secret=cruise-management-system-local-secret-key-2026"
})
class RoomTypeControllerSecurityTests {

    private static final String SECRET = "cruise-management-system-local-secret-key-2026";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RoomTypeService roomTypeService;

    @Test
    void requestWithoutTokenReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/room-types"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void passengerCannotCreateRoomType() throws Exception {
        performCreate("PASSENGER").andExpect(status().isForbidden());
    }

    @Test
    void schedulerCannotCreateRoomType() throws Exception {
        performCreate("SCHEDULER").andExpect(status().isForbidden());
    }

    @Test
    void adminCanCreateRoomType() throws Exception {
        performCreate("ADMIN").andExpect(status().isCreated());
    }

    @Test
    void authenticatedPassengerCanReadRoomTypes() throws Exception {
        mockMvc.perform(get("/api/v1/room-types")
                .header("Authorization", "Bearer " + accessToken("PASSENGER")))
                .andExpect(status().isOk());
    }

    @Test
    void refreshTokenIsRejected() throws Exception {
        mockMvc.perform(get("/api/v1/room-types")
                .header("Authorization", "Bearer " + token(null, "REFRESH")))
                .andExpect(status().isUnauthorized());
    }

    private org.springframework.test.web.servlet.ResultActions performCreate(
            String role) throws Exception {
        return mockMvc.perform(post("/api/v1/room-types")
                .header("Authorization", "Bearer " + accessToken(role))
                .contentType("application/json")
                .content("{\"name\":\"Deluxe Room\",\"description\":\"Ocean view\"}"));
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
        NimbusJwtEncoder encoder = new NimbusJwtEncoder(new ImmutableSecret<>(key));
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();

        return encoder.encode(
                JwtEncoderParameters.from(header, claims.build())).getTokenValue();
    }
}
