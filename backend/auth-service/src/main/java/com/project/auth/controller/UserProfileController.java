
package com.project.auth.controller;

import com.project.auth.dto.UserProfileRequest;
import com.project.auth.dto.UserProfileResponse;
import com.project.auth.service.UserProfileService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<UserProfileResponse> getMyProfile() {
        UUID userId = getAuthenticatedUserId();

        return ResponseEntity.ok(
                userProfileService.getMyProfile(userId));
    }

    /**
     * Cập nhật một phần hồ sơ của người đang đăng nhập.
     */
    @PatchMapping("/me")
    public ResponseEntity<UserProfileResponse> updateMyProfile(
            @Valid @RequestBody UserProfileRequest request) {
        UUID userId = getAuthenticatedUserId();

        return ResponseEntity.ok(
                userProfileService.updateMyProfile(userId, request));
    }

    /**
     * Lấy UUID từ danh tính đã xác thực.
     */
    private UUID getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication.getPrincipal() == null
                || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new IllegalStateException(
                    "Người dùng chưa được xác thực");
        }

        try {
            return UUID.fromString(authentication.getName());
        } catch (IllegalArgumentException exception) {
            throw new IllegalStateException(
                    "Authentication.getName() không chứa UUID. "
                            + "Cần lấy userId từ principal hoặc JWT hiện tại.",
                    exception);
        }
    }
}
