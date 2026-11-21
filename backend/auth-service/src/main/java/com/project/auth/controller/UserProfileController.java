
package com.project.auth.controller;

import com.project.auth.dto.UserProfileResponse;
import com.project.auth.security.JwtUserUtil;
import com.project.auth.service.UserProfileService;

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
     * Mọi role đã đăng nhập đều được xem profile của chính mình.
     */
    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getMyProfile(
            Principal principal) {

        UUID userId = JwtUserUtil.extractUserId(principal);

        return ResponseEntity.ok(
                userProfileService.getMyProfile(userId));
    }
}
