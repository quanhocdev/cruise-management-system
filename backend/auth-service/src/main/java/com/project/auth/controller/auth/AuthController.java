package com.project.auth.controller.auth;

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

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    public AuthController(AuthService authService, JwtService jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;
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
                    .sameSite("Strict")
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

            Users user = authService.refresh(refreshToken);

            String newAccessToken = jwtService.generateAccessToken(user);
            String newRefreshToken = jwtService.generateRefreshToken(user);

            ResponseCookie newAccessCookie = ResponseCookie.from("accessToken", newAccessToken)
                    .httpOnly(true)
                    .secure(false)
                    .path("/")
                    .maxAge(jwtService.getAccessCookieMaxAgeInSeconds())
                    .sameSite("Lax")
                    .build();

            ResponseCookie newRefreshCookie = ResponseCookie.from("refreshToken", newRefreshToken)
                    .httpOnly(true)
                    .secure(false)
                    .path("/")
                    .maxAge(jwtService.getRefreshCookieMaxAgeInSeconds())
                    .sameSite("Strict")
                    .build();

            JwtResponse responseBody = new JwtResponse(
                    newAccessToken,
                    newRefreshToken,
                    user.getUsername(),
                    user.getRole().name()
            );

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, newAccessCookie.toString())
                    .header(HttpHeaders.SET_COOKIE, newRefreshCookie.toString())
                    .body(responseBody);

        } catch (RuntimeException e) {
            ResponseCookie cleanAccessCookie = ResponseCookie.from("accessToken", "").path("/").maxAge(0).build();
            ResponseCookie cleanRefreshCookie = ResponseCookie.from("refreshToken", "").path("/").maxAge(0).build();

            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .header(HttpHeaders.SET_COOKIE, cleanAccessCookie.toString())
                    .header(HttpHeaders.SET_COOKIE, cleanRefreshCookie.toString())
                    .body(errorResponse);
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
     * API ĐĂNG XUẤT (Xóa sạch Cookie trên Browser)
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
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
public ResponseEntity<?> verifyEmail(
        @Valid @RequestBody VerifyOtpRequestDTO request
) {
    try {
        authService.verifyEmail(request);

        return ResponseEntity.ok(
                Map.of("message", "Xác thực email thành công")
        );

    } catch (RuntimeException e) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "message",
                        e.getMessage()
                ));
    }
}
}