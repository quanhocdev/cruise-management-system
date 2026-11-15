
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

        if (request.fullName() != null) {
            profile.setFullName(request.fullName());
        }

        if (request.dateOfBirth() != null) {
            profile.setDateOfBirth(request.dateOfBirth());
        }

        if (request.phone() != null) {
            profile.setPhone(request.phone());
        }

        if (request.address() != null) {
            profile.setAddress(request.address());
        }

        if (request.gender() != null) {
            profile.setGender(request.gender());
        }

        if (request.nationality() != null) {
            profile.setNationality(request.nationality());
        }

        if (request.identityNumber() != null) {
            profile.setIdentityNumber(request.identityNumber());
        }
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
                profile.getAvatarUrl(),
                profile.getCreatedAt(),
                profile.getUpdatedAt());
    }
}
