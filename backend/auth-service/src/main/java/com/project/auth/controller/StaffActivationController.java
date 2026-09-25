package com.project.auth.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.project.auth.dto.ActivateTokenRequest;
import com.project.auth.dto.SetPasswordRequest;
import com.project.auth.service.StaffActivationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth/activate")
public class StaffActivationController {

    private final StaffActivationService staffService;

    public StaffActivationController(StaffActivationService staffService) {
        this.staffService = staffService;
    }

    // Xác thực token kích hoạt tài khoản nhân viên
    @PostMapping("/verify")
    public ResponseEntity<Map<String, String>> verify(
            @Valid @RequestBody ActivateTokenRequest request) {

        String username = staffService.verifyActivationToken(request);

        return ResponseEntity.ok(
                Map.of(
                        "message", "Activation token hợp lệ",
                        "username", username));
    }

    // Nhân viên đặt mật khẩu kích hoạt
    @PostMapping("/set-password")
    public ResponseEntity<Map<String, String>> setPassword(
            @Valid @RequestBody SetPasswordRequest request) {

        staffService.setPassword(request);

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "Kích hoạt tài khoản thành công"));
    }
}