
package com.project.auth.controller;

import com.project.auth.dto.UserProfileRequest;
import com.project.auth.dto.UserProfileResponse;
import com.project.auth.security.JwtUserUtil;
import com.project.auth.service.UserProfileService;

import jakarta.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/api/profile/me")
@PreAuthorize("hasRole('PASSENGER')")
public class PassengerProfileController {

    private final UserProfileService userProfileService;

    public PassengerProfileController(
            UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    /**
     * Tạo profile cá nhân.
     */
    @PostMapping
    public ResponseEntity<UserProfileResponse> createMyProfile(
            Principal principal,
            @Valid @RequestBody UserProfileRequest request) {

        UUID userId = JwtUserUtil.extractUserId(principal);

        return ResponseEntity.status(201)
                .body(userProfileService.createMyProfile(userId, request));
    }

    /**
     * Cập nhật thông tin cá nhân.
     */
    @PatchMapping
    public ResponseEntity<UserProfileResponse> updateMyProfile(
            Principal principal,
            @Valid @RequestBody UserProfileRequest request) {

        UUID userId = JwtUserUtil.extractUserId(principal);

        return ResponseEntity.ok(
                userProfileService.updateMyProfile(userId, request));
    }

    /**
     * Upload hoặc thay avatar.
     */
    @PostMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserProfileResponse> uploadMyAvatar(
            Principal principal,
            @RequestParam("avatar") MultipartFile avatar) {

        UUID userId = JwtUserUtil.extractUserId(principal);

        return ResponseEntity.ok(
                userProfileService.uploadMyAvatar(userId, avatar));
    }

    /**
     * Xóa avatar, không xóa profile.
     */
    @DeleteMapping("/avatar")
    public ResponseEntity<UserProfileResponse> deleteMyAvatar(
            Principal principal) {

        UUID userId = JwtUserUtil.extractUserId(principal);

        return ResponseEntity.ok(
                userProfileService.deleteMyAvatar(userId));
    }
}
