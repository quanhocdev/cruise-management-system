
package com.project.auth.dto;

import java.util.UUID;

public record CreateStaffResponse(
                UUID id,
                String username,
                String email,
                String role,
                String status,
                String message) {
}
