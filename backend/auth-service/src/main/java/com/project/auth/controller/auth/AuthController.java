package com.project.auth.controller.auth;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.project.auth.dto.auth.JwtResponse;
import com.project.auth.dto.auth.LoginRequestDTO;
import com.project.auth.model.Users;
import com.project.auth.service.AuthService;
import com.project.auth.service.JwtService;
import com.project.auth.service.redis.TokenRedisService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
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
            TokenRedisService tokenRedisService
    ) {
        this.authService = authService;
        this.jwtService = jwtService;
        this.tokenRedisService = tokenRedisService;
    }

    /**
     * API ĐĂNG NHẬP (Cookie cho Web & JSON Body cho Android)
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO request) {
        try {
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

            ResponseCookie accessCookie = ResponseCookie.from("accessToken", accessToken)
                    .httpOnly(true)
                    .secure(false) // Bật true khi deploy HTTPS
                    .path("/")
                    .maxAge(jwtService.getAccessCookieMaxAgeInSeconds())
                    .sameSite("Lax")
                    .build();

            ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                    .httpOnly(true)
                    .secure(false)
                    .path("/")
                    .maxAge(jwtService.getRefreshCookieMaxAgeInSeconds())
                    .sameSite("Lax")
                    .build();

            JwtResponse responseBody = new JwtResponse(
                    accessToken,
                    refreshToken,
                    user.getUsername(),
                    user.getRole().name()
            );

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                    .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                    .body(responseBody);

        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }

    /**
     * API LÀM MỚI TOKEN (Refresh Token)
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(HttpServletRequest request) {
        try {
            String refreshToken = null;

            if (request.getCookies() != null) {
                for (Cookie cookie : request.getCookies()) {
                    if ("refreshToken".equals(cookie.getName())) {
                        refreshToken = cookie.getValue();
                        break;
                    }
                }
            }

            if (refreshToken == null) {
                String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    refreshToken = authHeader.substring(7);
                }
            }

            if (refreshToken == null || refreshToken.isBlank()) {
                throw new RuntimeException("Refresh Token không tồn tại");
            }

            // Kiểm tra refresh token cũ
            Users user = authService.refresh(refreshToken);

            // Tạo ACCESS TOKEN mới
            String newAccessToken = jwtService.generateAccessToken(user);

            // Lưu ACCESS JTI mới vào Redis whitelist
            String newAccessJti = jwtService.extractJti(newAccessToken);
            Instant newAccessExpiresAt = jwtService.extractExpiration(newAccessToken);
            Duration newAccessTtl = Duration.between(Instant.now(), newAccessExpiresAt);
            tokenRedisService.saveAccessToken(newAccessJti, user.getId(), newAccessTtl);

            ResponseCookie newAccessCookie = ResponseCookie
                    .from("accessToken", newAccessToken)
                    .httpOnly(true)
                    .secure(false)
                    .path("/")
                    .maxAge(jwtService.getAccessCookieMaxAgeInSeconds())
                    .sameSite("Lax")
                    .build();

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, newAccessCookie.toString())
                    .body(Map.of(
                            "accessToken", newAccessToken,
                            "message", "Access token refreshed"
                    ));

        } catch (Exception e) {
            ResponseCookie cleanAccessCookie = ResponseCookie.from("accessToken", "")
                    .httpOnly(true)
                    .path("/")
                    .maxAge(0)
                    .build();

            ResponseCookie cleanRefreshCookie = ResponseCookie.from("refreshToken", "")
                    .httpOnly(true)
                    .path("/")
                    .maxAge(0)
                    .build();

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .header(HttpHeaders.SET_COOKIE, cleanAccessCookie.toString())
                    .header(HttpHeaders.SET_COOKIE, cleanRefreshCookie.toString())
                    .body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * API KIỂM TRA THÔNG TIN USER ĐANG ĐĂNG NHẬP (/me)
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Chưa đăng nhập");
        }

        String username = authentication.getName();
        String role = authentication.getAuthorities().stream()
                .map(auth -> auth.getAuthority())
                .filter(authority -> authority.startsWith("ROLE_"))
                .map(authority -> authority.replace("ROLE_", ""))
                .findFirst()
                .orElse("GUEST");

        Map<String, String> userInfo = new HashMap<>();
        userInfo.put("username", username);
        userInfo.put("role", role);

        return ResponseEntity.ok(userInfo);
    }

    /**
     * API ĐĂNG XUẤT
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            Authentication authentication,
            HttpServletRequest request
    ) {
        String accessToken = null;
        String refreshToken = null;

        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("accessToken".equals(cookie.getName())) {
                    accessToken = cookie.getValue();
                }
                if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                }
            }
        }

        if (accessToken == null || accessToken.isBlank()) {
            String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                accessToken = authHeader.substring(7);
            }
        }

        if (accessToken != null && !accessToken.isBlank()) {
            try {
                String accessJti = jwtService.extractJti(accessToken);
                if (accessJti != null) {
                    tokenRedisService.deleteAccessToken(accessJti);
                }
            } catch (Exception ignored) {}
        }

        if (refreshToken != null && !refreshToken.isBlank()) {
            try {
                String refreshJti = jwtService.extractJti(refreshToken);
                if (refreshJti != null) {
                    tokenRedisService.deleteRefreshToken(refreshJti);
                }
            } catch (Exception ignored) {}
        }

        ResponseCookie cleanAccessCookie = ResponseCookie.from("accessToken", "")
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .build();

        ResponseCookie cleanRefreshCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cleanAccessCookie.toString())
                .header(HttpHeaders.SET_COOKIE, cleanRefreshCookie.toString())
                .body(Map.of("message", "Đăng xuất thành công"));
    }
}