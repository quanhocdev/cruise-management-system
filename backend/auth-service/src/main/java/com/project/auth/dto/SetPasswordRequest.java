package com.project.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SetPasswordRequest(

        @NotBlank(message = "Activation token không được để trống") String token,

        @NotBlank(message = "Mật khẩu mới không được để trống") @Size(min = 8, max = 100, message = "Mật khẩu phải từ 8 đến 100 ký tự") String password,

        @NotBlank(message = "Xác nhận mật khẩu không được để trống") String confirmPassword

) {
}