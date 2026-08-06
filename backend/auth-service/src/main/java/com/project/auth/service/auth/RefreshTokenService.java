package com.project.auth.service.auth;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.auth.model.RefreshToken;
import com.project.auth.repository.RefreshTokenRepository;
import com.project.auth.security.TokenProvider;

import java.time.Instant;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenProvider tokenProvider;
    private final JwtDecoder jwtDecoder;

    public RefreshTokenService(
            RefreshTokenRepository refreshTokenRepository,
            TokenProvider tokenProvider,
            JwtDecoder jwtDecoder
    ) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.tokenProvider = tokenProvider;
        this.jwtDecoder = jwtDecoder;
    }

    public String refreshAccessToken(String refreshTokenValue) {
        // 1. Decode refresh token
        Jwt jwt = jwtDecoder.decode(refreshTokenValue);

        // 2. Lấy jti
        String jti = jwt.getId();
        if (jti == null) {
            throw new RuntimeException("Refresh token không chứa jti hợp lệ");
        }

        // 3. Tìm refresh token trong DB
        RefreshToken refreshToken = refreshTokenRepository.findByJti(jti)
                .orElseThrow(() -> new RuntimeException("Refresh token không tồn tại hoặc đã bị hủy"));

        // 4. Kiểm tra thời gian hết hạn
        if (refreshToken.getExpiresAt().isBefore(Instant.now())) {
            throw new RuntimeException("Refresh token đã hết hạn");
        }

        // 5. Sinh access token mới dựa trên User ID và Role từ DB
        return tokenProvider.generateAccessToken(
                refreshToken.getUserId(),
                refreshToken.getRole()
        );
    }

    @Transactional
    public void deleteOldRefreshToken(String userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }
}