
package com.project.auth.controller;

import com.project.auth.dto.UserProfileRequest;
import com.project.auth.dto.UserProfileResponse;
import com.project.auth.security.JwtUserUtil;
import com.project.auth.service.UserProfileService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/api/profile")
public class UserProfileController {

    private final UserProfileService userProfileService;

    public UserProfileController(
            UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    /**
     * Lấy hồ sơ của người đang đăng nhập.
     */
    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getMyProfile(Principal principal) {
        UUID userId = JwtUserUtil.extractUserId(principal);
        return ResponseEntity.ok(userProfileService.getMyProfile(userId));
    }

    @PatchMapping("/me")
    public ResponseEntity<UserProfileResponse> updateMyProfile(
            Principal principal,
            @Valid @RequestBody UserProfileRequest request) {
        UUID userId = JwtUserUtil.extractUserId(principal);
        return ResponseEntity.ok(userProfileService.updateMyProfile(userId, request));
    }
}
