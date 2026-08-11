package com.project.auth.controller;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.project.auth.dto.JwtResponse;
import com.project.auth.dto.LoginRequest;
import com.project.auth.exception.AppException;
import com.project.auth.model.Users;
import com.project.auth.service.AuthService;
import com.project.auth.service.JwtService;
import com.project.auth.service.redis.TokenRedisService;
import com.project.auth.util.CookieUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;
    private final TokenRedisService tokenRedisService;

    public AuthController(
            AuthService authService,
            JwtService jwtService,
            TokenRedisService tokenRedisService) {
        this.authService = authService;
        this.jwtService = jwtService;
        this.tokenRedisService = tokenRedisService;
    }

    /**
     * API ĐĂNG NHẬP
     */
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response) {
        Users user = authService.login(request);

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        // Lưu ACCESS TOKEN vào Redis whitelist
        String accessJti = jwtService.extractJti(accessToken);
        Instant accessExpiresAt = jwtService.extractExpiration(accessToken);
        Duration accessTtl = Duration.between(Instant.now(), accessExpiresAt);
        tokenRedisService.saveAccessToken(accessJti, user.getId(), accessTtl);

        // Lưu REFRESH TOKEN vào Redis whitelist
        String refreshJti = jwtService.extractJti(refreshToken);
        Instant refreshExpiresAt = jwtService.extractExpiration(refreshToken);
        Duration refreshTtl = Duration.between(Instant.now(), refreshExpiresAt);
        tokenRedisService.saveRefreshToken(refreshJti, user.getId(), refreshTtl);

        // Set Cookie thông qua CookieUtil
        CookieUtil.addCookie(response, CookieUtil.ACCESS_TOKEN_COOKIE_NAME, accessToken,
                jwtService.getAccessCookieMaxAgeInSeconds());
        CookieUtil.addCookie(response, CookieUtil.REFRESH_TOKEN_COOKIE_NAME, refreshToken,
                jwtService.getRefreshCookieMaxAgeInSeconds());

        JwtResponse responseBody = new JwtResponse(
                accessToken,
                refreshToken,
                user.getUsername(),
                user.getRole().getName());

        return ResponseEntity.ok(responseBody);
    }

    /**
     * API LÀM MỚI TOKEN (Refresh Token)
     */
    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refresh(
            HttpServletRequest request,
            HttpServletResponse response) {
        String refreshToken = CookieUtil.getCookieValue(request, CookieUtil.REFRESH_TOKEN_COOKIE_NAME);

        if (refreshToken == null) {
            String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                refreshToken = authHeader.substring(7);
            }
        }

        if (refreshToken == null || refreshToken.isBlank()) {
            CookieUtil.clearAuthCookies(response);
            throw new AppException("Refresh Token không tồn tại", HttpStatus.UNAUTHORIZED);
        }

        // Kiểm tra refresh token cũ (Service tự throw AppException nếu không hợp lệ)
        Users user = authService.refresh(refreshToken);

        // Tạo ACCESS TOKEN mới
        String newAccessToken = jwtService.generateAccessToken(user);

        // Lưu ACCESS JTI mới vào Redis whitelist
        String newAccessJti = jwtService.extractJti(newAccessToken);
        Instant newAccessExpiresAt = jwtService.extractExpiration(newAccessToken);
        Duration newAccessTtl = Duration.between(Instant.now(), newAccessExpiresAt);
        tokenRedisService.saveAccessToken(newAccessJti, user.getId(), newAccessTtl);

        // Set Access Token Cookie mới
        CookieUtil.addCookie(response, CookieUtil.ACCESS_TOKEN_COOKIE_NAME, newAccessToken,
                jwtService.getAccessCookieMaxAgeInSeconds());

        return ResponseEntity.ok(Map.of(
                "accessToken", newAccessToken,
                "message", "Access token refreshed"));
    }

    /**
     * API KIỂM TRA THÔNG TIN USER ĐANG ĐĂNG NHẬP (/me)
     */
    @GetMapping("/me")
    public ResponseEntity<Map<String, String>> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AppException("Chưa đăng nhập", HttpStatus.UNAUTHORIZED);
        }

        String username = authentication.getName();
        String role = authentication.getAuthorities().stream()
                .map(auth -> auth.getAuthority())
                .filter(authority -> authority.startsWith("ROLE_"))
                .map(authority -> authority.replace("ROLE_", ""))
                .findFirst()
                .orElse("GUEST");

        return ResponseEntity.ok(Map.of(
                "username", username,
                "role", role));
    }

    /**
     * API ĐĂNG XUẤT
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(
            HttpServletRequest request,
            HttpServletResponse response) {
        String accessToken = CookieUtil.getCookieValue(request, CookieUtil.ACCESS_TOKEN_COOKIE_NAME);
        String refreshToken = CookieUtil.getCookieValue(request, CookieUtil.REFRESH_TOKEN_COOKIE_NAME);

        if (accessToken == null || accessToken.isBlank()) {
            String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                accessToken = authHeader.substring(7);
            }
        }

        // Xóa JTI trong Redis
        if (accessToken != null && !accessToken.isBlank()) {
            try {
                String accessJti = jwtService.extractJti(accessToken);
                if (accessJti != null) {
                    tokenRedisService.deleteAccessToken(accessJti);
                }
            } catch (Exception ignored) {
            }
        }

        if (refreshToken != null && !refreshToken.isBlank()) {
            try {
                String refreshJti = jwtService.extractJti(refreshToken);
                if (refreshJti != null) {
                    tokenRedisService.deleteRefreshToken(refreshJti);
                }
            } catch (Exception ignored) {
            }
        }

        // Clear toàn bộ Auth Cookie
        CookieUtil.clearAuthCookies(response);

        return ResponseEntity.ok(Map.of("message", "Đăng xuất thành công"));
    }
}