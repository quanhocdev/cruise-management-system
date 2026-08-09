package com.project.auth.controller.auth;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.project.auth.dto.auth.RegisterRequestDTO;
import com.project.auth.dto.auth.RegisterResponseDTO;
import com.project.auth.dto.auth.VerifyOtpRequestDTO;
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
    public ResponseEntity<RegisterResponseDTO> register(@Valid @RequestBody RegisterRequestDTO request) {
        RegisterResponseDTO response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    /**
     * API XÁC THỰC EMAIL
     */
    @PostMapping("/verify-email")
    public ResponseEntity<Map<String, String>> verifyEmail(@Valid @RequestBody VerifyOtpRequestDTO request) {
        authService.verifyEmail(request);
        return ResponseEntity.ok(Map.of("message", "Xác thực email thành công"));
    }
}