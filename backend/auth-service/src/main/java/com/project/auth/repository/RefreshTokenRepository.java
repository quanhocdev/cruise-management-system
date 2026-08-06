package com.project.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.auth.model.RefreshToken;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    // Tìm token theo ID định danh (jti)
    Optional<RefreshToken> findByJti(String jti);

    // Xóa token khi user Logout
    void deleteByJti(String jti);

    // Xóa tất cả refresh token cũ của 1 user
    void deleteByUserId(String userId);

    // Dùng cho Cron Job xóa các token đã hết hạn
    void deleteByExpiresAtBefore(Instant time);
}