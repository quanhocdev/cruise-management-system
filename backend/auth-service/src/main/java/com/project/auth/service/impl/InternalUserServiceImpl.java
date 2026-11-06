package com.project.auth.service.impl;

import com.project.auth.dto.ManagerInfoResponse;
import com.project.auth.model.UserProfile;
import com.project.auth.model.Users;
import com.project.auth.model.enums.UserStatus;
import com.project.auth.repository.UserRepository;
import com.project.auth.service.InternalUserService;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InternalUserServiceImpl implements InternalUserService {

    private final UserRepository userRepository;

    public InternalUserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<ManagerInfoResponse> getManagersByRoleName(String roleName) {

        List<Users> users = userRepository.findAllByRole_IdAndEnabledTrueAndStatus(
                roleId,
                UserStatus.ACTIVE);

        return users.stream()
                .map(user -> {
                    UserProfile profile = user.getUserProfile();

                    return new ManagerInfoResponse(
                            user.getId(),
                            user.getUsername(),
                            user.getEmail(),
                            profile != null ? profile.getFullName() : null,
                            profile != null ? profile.getPhone() : null,
                            profile != null ? profile.getAvatarUrl() : null);
                })
                .toList();
    }
}