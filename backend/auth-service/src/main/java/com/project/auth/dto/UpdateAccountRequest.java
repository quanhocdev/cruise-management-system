package com.project.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateAccountRequest(

        @NotBlank(message = "Username không được để trống") @Size(min = 3, max = 50, message = "Username phải từ 3 đến 50 ký tự") String username,

        @NotBlank(message = "Email không được để trống") @Email(message = "Email không hợp lệ") String email

) {
}