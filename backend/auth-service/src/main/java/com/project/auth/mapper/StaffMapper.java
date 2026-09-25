package com.project.auth.mapper;

import org.springframework.stereotype.Component;

import com.project.auth.dto.StaffResponse;
import com.project.auth.model.Users;

@Component
public class StaffMapper {

    public StaffResponse toStaffResponse(Users user) {
        return new StaffResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().getId(),
                user.getRole().getName(),
                user.getProvider().name(),
                user.getStatus().name(),
                user.getEnabled(),
                user.getCreatedAt(),
                user.getUpdatedAt());
    }
}