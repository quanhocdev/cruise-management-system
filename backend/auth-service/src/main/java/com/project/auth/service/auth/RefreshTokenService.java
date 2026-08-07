package com.project.auth.service.auth;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.auth.model.RefreshToken;
import com.project.auth.model.enums.UserRole;
import com.project.auth.repository.RefreshTokenRepository;
import com.project.auth.security.TokenProvider;

import java.time.Instant;
import java.util.Optional;

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

    @Transactional
    public String refreshAccessToken(String refreshTokenValue) {
        Jwt jwt;
        try {
            jwt = jwtDecoder.decode(refreshTokenValue);
        } catch (JwtException e) {
            throw new RuntimeException("Refresh token không hợp lệ hoặc đã hết hạn", e);
        }

        String jti = jwt.getId();
        if (jti == null) {
            throw new RuntimeException("Refresh token không chứa jti hợp lệ");
        }

        RefreshToken refreshToken = refreshTokenRepository.findByJti(jti)
                .orElseThrow(() -> new RuntimeException("Refresh token không tồn tại hoặc đã bị hủy"));

        if (refreshToken.getExpiresAt().isBefore(Instant.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new RuntimeException("Refresh token đã hết hạn, vui lòng đăng nhập lại");
        }

        return tokenProvider.generateAccessToken(
                refreshToken.getUserId(),
                refreshToken.getRole()
        );
    }

    @Transactional
    public void deleteOldRefreshToken(String userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }

    @Transactional
    public void saveRefreshToken(String userId, String jti, UserRole role, Instant expiresAt) {
        // 🟢 SỬA LỖI HIBERNATE CONFLICT:
        // Tìm token cũ của User nếu đã có trong DB
        Optional<RefreshToken> existingTokenOpt = refreshTokenRepository.findByUserId(userId);

        if (existingTokenOpt.isPresent()) {
            // Cập nhật thông tin trên chính Entity cũ thay vì xóa/chèn mới làm rác Hibernate Cache
            RefreshToken existingToken = existingTokenOpt.get();
            existingToken.setJti(jti);
            existingToken.setRole(role);
            existingToken.setExpiresAt(expiresAt);
            refreshTokenRepository.save(existingToken);
        } else {
            // Nếu chưa có thì tạo mới
            RefreshToken newToken = new RefreshToken(jti, userId, role, expiresAt);
            refreshTokenRepository.save(newToken);
        }
    }
}