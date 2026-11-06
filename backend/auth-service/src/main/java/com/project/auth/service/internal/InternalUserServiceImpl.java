package com.project.auth.service.internal;

import com.project.auth.dto.response.ManagerInfoResponse;
import com.project.auth.model.enums.UserStatus;
import com.project.auth.repository.UserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;

@Service
public class InternalUserServiceImpl implements InternalUserService {

    private static final Set<String> ALLOWED_MANAGER_ROLES = Set.of(
            "ONBOARD",
            "CONVENIENCE",
            "FINANCE",
            "SHORE",
            "SCHEDULER");

    private final UserRepository userRepository;

    public InternalUserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<ManagerInfoResponse> getManagersByRoleName(String roleName) {

        if (roleName == null || !ALLOWED_MANAGER_ROLES.contains(roleName)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid manager role name");
        }

        return userRepository.findManagerInfoByRoleName(
                roleName,
                UserStatus.ACTIVE);
    }
}