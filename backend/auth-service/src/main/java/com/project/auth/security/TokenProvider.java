package com.project.auth.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import com.project.auth.model.enums.UserRole;

import java.time.Instant;

@Component
public class TokenProvider {

    private final JwtEncoder jwtEncoder;

    @Value("${jwt.access-expiration}")
    private long accessExpirationMs;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpirationMs;

    public TokenProvider(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    /**
     * Sinh Access Token chứa claim 'scope' đại diện cho Role
     */
    public String generateAccessToken(String userId, UserRole role) {
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("cruise-app")
                .issuedAt(now)
                .expiresAt(now.plusMillis(accessExpirationMs))
                .subject(userId)
                .claim("scope", role.name()) // Gán Role vào scope cho SecurityConfig đọc
                .build();

        JwsHeader jwsHeader = JwsHeader.with(MacAlgorithm.HS256).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
    }

    /**
     * Sinh Refresh Token chứa jti (JWT ID) duy nhất
     */
    public String generateRefreshToken(String userId, String jti) {
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("cruise-app")
                .issuedAt(now)
                .expiresAt(now.plusMillis(refreshExpirationMs))
                .subject(userId)
                .id(jti) // Định danh JTI phục vụ lưu DB và revoked check
                .build();

        JwsHeader jwsHeader = JwsHeader.with(MacAlgorithm.HS256).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
    }

    public long getRefreshExpirationMs() {
        return refreshExpirationMs;
    }
}