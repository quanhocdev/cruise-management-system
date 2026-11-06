package com.project.chat.service;

import com.project.chat.client.InternalUserClient;
import com.project.chat.dto.response.ManagerInfoResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class ManagerService {

    private static final Set<String> ALLOWED_ROLES = Set.of(
            "ONBOARD",
            "CONVENIENCE",
            "FINANCE",
            "SHORE",
            "SCHEDULER");

    private final InternalUserClient internalUserClient;

    public ManagerService(InternalUserClient internalUserClient) {
        this.internalUserClient = internalUserClient;
    }

    public List<ManagerInfoResponse> getManagersByRoleName(String roleName) {

        String normalizedRole = roleName.toUpperCase();

        if (!ALLOWED_ROLES.contains(normalizedRole)) {
            throw new IllegalArgumentException("Invalid manager role: " + roleName);
        }

        return internalUserClient.getManagersByRoleName(normalizedRole);
    }
}