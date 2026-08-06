package com.project.cruise.controller.auth;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import com.project.cruise.dto.request.LoginRequest;
import com.project.cruise.dto.request.RegisterRequestDTO;
import com.project.cruise.dto.response.JwtResponse;
import com.project.cruise.dto.response.RegisterResponseDTO;
import com.project.cruise.model.Users;
import com.project.cruise.service.AuthService;
import com.project.cruise.service.JwtService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
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
     * API ĐĂNG NHẬP (Trả về Token qua Cookie cho Web & JSON Body cho Android)
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            // 1. Xác thực thông tin người dùng
            Users user = authService.login(request);

            // 2. Tạo cặp Access Token & Refresh Token
            String accessToken = jwtService.generateAccessToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);

            // 3. Set Cookie cho Web Client (React/Next.js)
            ResponseCookie accessCookie = ResponseCookie.from("accessToken", accessToken)
                    .httpOnly(true)
                    .secure(false) // Đổi thành true khi chạy HTTPS thực tế
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

            // 4. Trả JSON Body cho Android App hoặc React State
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
            // Nếu Refresh thất bại -> Xóa Cookie trên Browser để yêu cầu login lại
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
        if (authentication == null || !authentication.isAuthenticated() || !(authentication instanceof JwtAuthenticationToken jwtAuth)) {
            return ResponseEntity.ok(Map.of(
                    "isLoggedIn", false,
                    "message", "Chưa đăng nhập hoặc token đã hết hạn"
            ));
        }

        String username = jwtAuth.getName();
        String role = jwtAuth.getAuthorities().stream()
                .map(auth -> auth.getAuthority())
                .filter(authority -> authority.startsWith("ROLE_"))
                .map(authority -> authority.replace("ROLE_", ""))
                .findFirst()
                .orElse("USER");

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("isLoggedIn", true);
        userInfo.put("username", username);
        userInfo.put("role", role);

        return ResponseEntity.ok(userInfo);
    }
}