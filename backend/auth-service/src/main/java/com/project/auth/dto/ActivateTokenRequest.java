package com.project.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record ActivateTokenRequest(

        @NotBlank(message = "Activation token không được để trống") String token

) {
}