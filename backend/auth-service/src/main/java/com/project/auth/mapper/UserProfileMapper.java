package com.project.auth.mapper;

import com.project.auth.dto.UserProfileRequest;
import com.project.auth.dto.UserProfileResponse;
import com.project.auth.model.UserProfile;

import java.util.UUID;

public final class UserProfileMapper {

    private UserProfileMapper() {
    }

    /**
     * Cập nhật thông tin cá nhân.
     * Không thay đổi user, id, avatar, createdAt và updatedAt.
     */
    public static void updateEntity(
            UserProfile profile,
            UserProfileRequest request) {

        profile.setFullName(request.fullName());
        profile.setDateOfBirth(request.dateOfBirth());
        profile.setPhone(request.phone());
        profile.setAddress(request.address());
        profile.setGender(request.gender());
        profile.setNationality(request.nationality());
        profile.setIdentityNumber(request.identityNumber());
    }

    /**
     * Chuyển entity thành response.
     */
    public static UserProfileResponse toResponse(UserProfile profile) {

        UUID userId = profile.getUser().getId();

        return new UserProfileResponse(
                profile.getId(),
                userId,
                profile.getFullName(),
                profile.getDateOfBirth(),
                profile.getPhone(),
                profile.getAddress(),
                profile.getGender(),
                profile.getNationality(),
                profile.getIdentityNumber(),
                profile.getAvatarPublicId(),
                profile.getCreatedAt(),
                profile.getUpdatedAt());
    }
}