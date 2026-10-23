package com.project.auth.dto;

import com.project.auth.model.Role;

public record RoleResponse(
        Long id,
        String name) {
    public static RoleResponse fromEntity(Role role) {
        return new RoleResponse(
                role.getId(),
                role.getName());
    }
}