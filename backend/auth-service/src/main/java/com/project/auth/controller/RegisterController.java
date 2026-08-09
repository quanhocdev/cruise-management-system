package com.project.auth.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.project.auth.dto.RegisterRequest;
import com.project.auth.dto.RegisterResponse;
import com.project.auth.dto.VerifyOtpRequest;
import com.project.auth.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class RegisterController {

    private final AuthService authService;

    public RegisterController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * API ĐĂNG KÝ TÀI KHOẢN
     */
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        RegisterResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    /**
     * API XÁC THỰC EMAIL
     */
    @PostMapping("/verify-email")
    public ResponseEntity<Map<String, String>> verifyEmail(@Valid @RequestBody VerifyOtpRequest request) {
        authService.verifyEmail(request);
        return ResponseEntity.ok(Map.of("message", "Xác thực email thành công"));
    }
}