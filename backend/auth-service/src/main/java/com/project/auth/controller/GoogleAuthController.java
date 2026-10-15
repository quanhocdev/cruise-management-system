
package com.project.auth.controller;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.project.auth.dto.GoogleLoginRequest;
import com.project.auth.dto.GoogleVerifyOtpRequest;
import com.project.auth.dto.JwtResponse;
import com.project.auth.model.Users;
import com.project.auth.service.GoogleAuthService;
import com.project.auth.service.GoogleLoginResult;
import com.project.auth.service.JwtService;
import com.project.auth.service.redis.TokenRedisService;
import com.project.auth.util.CookieUtil;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth/google")
public class GoogleAuthController {

    private final GoogleAuthService googleAuthService;
    private final JwtService jwtService;
    private final TokenRedisService tokenRedisService;

    public GoogleAuthController(
            GoogleAuthService googleAuthService,
            JwtService jwtService,
            TokenRedisService tokenRedisService) {
        this.googleAuthService = googleAuthService;
        this.jwtService = jwtService;
        this.tokenRedisService = tokenRedisService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody GoogleLoginRequest request,
            HttpServletResponse response) {

        GoogleLoginResult result = googleAuthService.login(request.getIdToken());

        if (result.otpRequired()) {
            return ResponseEntity.ok(Map.of(
                    "status", "OTP_REQUIRED",
                    "userId", result.userId(),
                    "message", result.message()));
        }

        JwtResponse jwtResponse = issueTokens(result.user(), response);

        return ResponseEntity.ok(jwtResponse);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<JwtResponse> verifyOtp(
            @Valid @RequestBody GoogleVerifyOtpRequest request,
            HttpServletResponse response) {

        Users user = googleAuthService.verifyOtp(
                request.getUserId(),
                request.getOtp());

        JwtResponse jwtResponse = issueTokens(user, response);

        return ResponseEntity.ok(jwtResponse);
    }

    private JwtResponse issueTokens(
            Users user,
            HttpServletResponse response) {

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        String accessJti = jwtService.extractJti(accessToken);
        String refreshJti = jwtService.extractJti(refreshToken);

        Instant accessExpiration = jwtService.extractExpiration(accessToken);

        Instant refreshExpiration = jwtService.extractExpiration(refreshToken);

        Duration accessTtl = Duration.between(Instant.now(), accessExpiration);

        Duration refreshTtl = Duration.between(Instant.now(), refreshExpiration);

        if (accessTtl.isZero() || accessTtl.isNegative()
                || refreshTtl.isZero() || refreshTtl.isNegative()) {
            throw new IllegalStateException(
                    "Token expiration phải nằm trong tương lai");
        }

        tokenRedisService.saveAccessToken(
                accessJti, user.getId(), accessTtl);

        tokenRedisService.saveRefreshToken(
                refreshJti, user.getId(), refreshTtl);

        CookieUtil.addCookie(
                response,
                CookieUtil.ACCESS_TOKEN_COOKIE_NAME,
                accessToken,
                accessTtl.getSeconds());

        CookieUtil.addCookie(
                response,
                CookieUtil.REFRESH_TOKEN_COOKIE_NAME,
                refreshToken,
                refreshTtl.getSeconds());

        return new JwtResponse(
                accessToken,
                refreshToken,
                user.getUsername(),
                user.getRole().getName());
    }
}
