package com.project.auth.mapper;

import com.project.auth.dto.response.ManagerInfoResponse;
import com.project.auth.model.Users;
import com.project.auth.model.UserProfile;

public class ManagerInfoMapper {

    private ManagerInfoMapper() {
    }

    public static ManagerInfoResponse toResponse(
            Users user,
            UserProfile profile) {
        return new ManagerInfoResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                profile != null ? profile.getFullName() : null,
                profile != null ? profile.getPhone() : null,
                profile != null ? profile.getAvatarPublicId() : null);
    }
}