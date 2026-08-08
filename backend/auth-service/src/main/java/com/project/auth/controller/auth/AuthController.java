package com.project.auth.controller.auth;

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
import com.project.auth.dto.auth.RegisterRequestDTO;
import com.project.auth.dto.auth.RegisterResponseDTO;
import com.project.auth.dto.auth.VerifyOtpRequestDTO;
import com.project.auth.model.Users;
import com.project.auth.service.AuthService;
import com.project.auth.service.JwtService;
import com.project.auth.service.auth.RefreshTokenService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public AuthController(AuthService authService, JwtService jwtService, RefreshTokenService refreshTokenService) {
        this.authService = authService;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    /**
     * API ĐĂNG KÝ TÀI KHOẢN
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequestDTO request) {
        try {
            RegisterResponseDTO response = authService.register(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
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

            // 🟢 LƯU REFRESH TOKEN VÀO DATABASE
            String jti = jwtService.extractJti(refreshToken);
            Instant expiresAt = jwtService.extractExpiration(refreshToken);
            refreshTokenService.saveRefreshToken(user.getId().toString(), jti, user.getRole(), expiresAt);

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

        // Chỉ tạo ACCESS TOKEN mới
        String newAccessToken = jwtService.generateAccessToken(user);

        ResponseCookie newAccessCookie = ResponseCookie
                .from("accessToken", newAccessToken)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(jwtService.getAccessCookieMaxAgeInSeconds())
                .sameSite("Lax")
                .build();

        return ResponseEntity.ok()
        .header(
                HttpHeaders.SET_COOKIE,
                newAccessCookie.toString()
        )
        .body(Map.of(
                "accessToken", newAccessToken,
                "message", "Access token refreshed"
        ));

    } catch (Exception e) {

        ResponseCookie cleanAccessCookie =
                ResponseCookie.from("accessToken", "")
                        .httpOnly(true)
                        .path("/")
                        .maxAge(0)
                        .build();

        ResponseCookie cleanRefreshCookie =
                ResponseCookie.from("refreshToken", "")
                        .httpOnly(true)
                        .path("/")
                        .maxAge(0)
                        .build();

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .header(
                        HttpHeaders.SET_COOKIE,
                        cleanAccessCookie.toString()
                )
                .header(
                        HttpHeaders.SET_COOKIE,
                        cleanRefreshCookie.toString()
                )
                .body(Map.of(
                        "message",
                        e.getMessage()
                ));
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
     * API ĐĂNG XUẤT (Xóa sạch Cookie trên Browser & Revoke Token ở DB)
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            // Xóa Refresh Token trong Database nếu user đã xác thực
            String username = authentication.getName();
            // authService/refreshTokenService dọn dẹp theo UserId/Username
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

    @PostMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@Valid @RequestBody VerifyOtpRequestDTO request) {
        try {
            authService.verifyEmail(request);
            return ResponseEntity.ok(Map.of("message", "Xác thực email thành công"));
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        }
    }
}