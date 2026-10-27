
package com.project.auth.service;

import com.project.auth.dto.UserProfileRequest;
import com.project.auth.dto.UserProfileResponse;
import com.project.auth.mapper.UserProfileMapper;
import com.project.auth.model.UserProfile;
import com.project.auth.repository.UserProfileRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;

    public UserProfileService(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getMyProfile(UUID userId) {
        UserProfile profile = userProfileRepository
                .findByUser_Id(userId)
                .orElseThrow(() -> new RuntimeException(
                        "Không tìm thấy hồ sơ của người dùng: " + userId));

        return UserProfileMapper.toResponse(profile);
    }

    @Transactional
    public UserProfileResponse updateMyProfile(
            UUID userId,
            UserProfileRequest request) {

        UserProfile profile = userProfileRepository
                .findByUser_Id(userId)
                .orElseThrow(() -> new RuntimeException(
                        "Không tìm thấy hồ sơ của người dùng: " + userId));

        UserProfileMapper.updateEntity(profile, request);

        UserProfile savedProfile = userProfileRepository.save(profile);

        return UserProfileMapper.toResponse(savedProfile);
    }
}
