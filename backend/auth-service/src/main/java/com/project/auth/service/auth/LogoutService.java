package com.project.auth.service.auth;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.auth.repository.RefreshTokenRepository;

@Service
public class LogoutService {

    private final RefreshTokenRepository refreshTokenRepository;

    public LogoutService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Transactional
    public void deleteRefreshToken(String jti) {
        refreshTokenRepository.deleteByJti(jti);
    }
}