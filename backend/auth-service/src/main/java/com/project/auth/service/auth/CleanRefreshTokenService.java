package com.project.auth.service.auth;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.project.auth.repository.RefreshTokenRepository;

import java.time.Instant;

@Service
public class CleanRefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    public CleanRefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    /**
     * Tự động xóa refresh token đã hết hạn trong DB lúc 2 giờ sáng mỗi ngày
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanExpiredRefreshTokens() {
        refreshTokenRepository.deleteByExpiresAtBefore(Instant.now());
        System.out.println("[CRUISE SCHEDULER] Đã dọn dẹp các Refresh Token hết hạn trong Database.");
    }
}