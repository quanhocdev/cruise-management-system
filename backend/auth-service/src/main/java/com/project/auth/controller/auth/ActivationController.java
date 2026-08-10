package com.project.auth.controller.auth;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.project.auth.dto.ActivateTokenRequest;
import com.project.auth.dto.SetPasswordRequest;
import com.project.auth.service.StaffService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth/activate")
public class ActivationController {

    private final StaffService staffService;

    public ActivationController(StaffService staffService) {
        this.staffService = staffService;
    }

    /**
     * Kiểm tra activation token
     */
    @PostMapping("/verify")
    public ResponseEntity<Map<String, String>> verify(
            @Valid @RequestBody ActivateTokenRequest request) {

        String username = staffService.verifyActivationToken(request);

        return ResponseEntity.ok(
                Map.of(
                        "message", "Activation token hợp lệ",
                        "username", username));
    }

    /**
     * Thiết lập mật khẩu mới và kích hoạt tài khoản
     */
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