package com.project.auth.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import com.project.auth.model.Users;

@Service
public class JwtService {

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;

    @Value("${jwt.access-expiration}") 
    private long accessTokenExpiration;

    @Value("${jwt.refresh-expiration}") 
    private long refreshTokenExpiration;

    public JwtService(JwtEncoder jwtEncoder, JwtDecoder jwtDecoder) {
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
    }

    public String generateAccessToken(Users user) {
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(user.getUsername())
                .issuedAt(now)
                .expiresAt(now.plus(accessTokenExpiration, ChronoUnit.MILLIS))
                .claim("scope", user.getRole().name())
                .claim("userId", user.getId())
                .claim("email", user.getEmail())
                .claim("tokenType", "ACCESS")
                .build();

        return encodeToken(claims);
    }

    public String generateRefreshToken(Users user) {
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .id(UUID.randomUUID().toString()) // 🟢 BẮT BUỘC: Tạo jti ngẫu nhiên cho Refresh Token
                .subject(user.getUsername())
                .issuedAt(now)
                .expiresAt(now.plus(refreshTokenExpiration, ChronoUnit.MILLIS))
                .claim("tokenType", "REFRESH")
                .build();

        return encodeToken(claims);
    }

    public String extractUsername(String token) {
        return jwtDecoder.decode(token).getSubject();
    }

    // 🟢 HÀM BỔ SUNG: Trích xuất jti (JWT ID) từ Token
    public String extractJti(String token) {
        Jwt jwt = jwtDecoder.decode(token);
        return jwt.getId();
    }

    // 🟢 HÀM BỔ SUNG: Trích xuất ngày hết hạn dạng Instant từ Token
    public Instant extractExpiration(String token) {
        Jwt jwt = jwtDecoder.decode(token);
        return jwt.getExpiresAt();
    }

    public boolean isRefreshToken(String token) {
        try {
            Jwt jwt = jwtDecoder.decode(token);
            String tokenType = jwt.getClaimAsString("tokenType");
            return "REFRESH".equals(tokenType) && jwt.getExpiresAt() != null && jwt.getExpiresAt().isAfter(Instant.now());
        } catch (Exception e) {
            return false;
        }
    }

    public int getAccessCookieMaxAgeInSeconds() {
        return (int) (accessTokenExpiration / 1000);
    }

    public int getRefreshCookieMaxAgeInSeconds() {
        return (int) (refreshTokenExpiration / 1000);
    }

    private String encodeToken(JwtClaimsSet claims) {
        return jwtEncoder.encode(
                JwtEncoderParameters.from(
                        JwsHeader.with(MacAlgorithm.HS256).build(),
                        claims
                )
        ).getTokenValue();
    }
}