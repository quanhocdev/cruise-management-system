package com.project.auth.dto;

import com.project.auth.model.enums.UserStatus;

import jakarta.validation.constraints.NotNull;

public record UpdateStaffStatusRequest(

        @NotNull(message = "Status không được để trống") UserStatus status

) {
}