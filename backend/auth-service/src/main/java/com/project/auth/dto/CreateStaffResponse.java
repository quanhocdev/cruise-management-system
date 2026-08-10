package com.project.auth.dto;

public record CreateStaffResponse(
        Long id,
        String username,
        String email,
        String role,
        String status,
        String message) {
}