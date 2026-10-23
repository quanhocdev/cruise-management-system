package com.project.auth.controller.internal;

import com.project.auth.dto.ShoreStaffResponse;
import com.project.auth.model.Users;
import com.project.auth.repository.UserRepository;
import com.project.auth.repository.UserProfileRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/internal/staff")
public class ShoreStaffInternalController {

        private static final String SHORE_STAFF_ROLE = "SHORE_STAFF";

        private final UserRepository userRepository;
        private final UserProfileRepository userProfileRepository;

        public ShoreStaffInternalController(
                        UserRepository userRepository,
                        UserProfileRepository userProfileRepository) {
                this.userRepository = userRepository;
                this.userProfileRepository = userProfileRepository;
        }

        @GetMapping("/shore")
        @Transactional(readOnly = true)
        public ResponseEntity<List<ShoreStaffResponse>> getShoreStaff() {

                List<ShoreStaffResponse> staff = userRepository
                                .findAllByRole_NameAndEnabledTrue(SHORE_STAFF_ROLE)
                                .stream()
                                .map(this::toResponse)
                                .toList();

                return ResponseEntity.ok(staff);
        }

        private ShoreStaffResponse toResponse(Users user) {

                var profile = userProfileRepository.findByUser_Id(user.getId());

                String fullName = profile
                                .map(p -> p.getFullName())
                                .orElse(null);

                String phone = profile
                                .map(p -> p.getPhone())
                                .orElse(null);

                String avatarUrl = profile
                                .map(p -> p.getAvatarUrl())
                                .orElse(null);

                return new ShoreStaffResponse(
                                user.getId(),
                                user.getUsername(),
                                user.getEmail(),
                                user.getRole().getName(),
                                fullName,
                                phone,
                                avatarUrl);
        }
}