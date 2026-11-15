
package com.project.auth.controller;

import com.project.auth.dto.UserProfileRequest;
import com.project.auth.dto.UserProfileResponse;
import com.project.auth.service.admin.AdminProfileService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/profiles")
public class AdminProfileController {

    private final AdminProfileService adminProfileService;

    public AdminProfileController(
            AdminProfileService adminProfileService) {

        this.adminProfileService = adminProfileService;
    }

    @PostMapping("/{userId}")
    public ResponseEntity<UserProfileResponse> createProfile(
            @PathVariable UUID userId,
            @Valid @RequestBody UserProfileRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(adminProfileService.createProfile(userId, request));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserProfileResponse> getProfile(
            @PathVariable UUID userId) {

        return ResponseEntity.ok(
                adminProfileService.getProfile(userId));
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<UserProfileResponse> updateProfile(
            @PathVariable UUID userId,
            @Valid @RequestBody UserProfileRequest request) {

        return ResponseEntity.ok(
                adminProfileService.updateProfile(userId, request));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteProfile(
            @PathVariable UUID userId) {

        adminProfileService.deleteProfile(userId);

        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/{userId}/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserProfileResponse> uploadAvatar(
            @PathVariable UUID userId,
            @RequestParam("avatar") MultipartFile avatar) {

        return ResponseEntity.ok(
                adminProfileService.uploadAvatar(userId, avatar));
    }

    @DeleteMapping("/{userId}/avatar")
    public ResponseEntity<UserProfileResponse> deleteAvatar(
            @PathVariable UUID userId) {

        return ResponseEntity.ok(
                adminProfileService.deleteAvatar(userId));
    }
}
