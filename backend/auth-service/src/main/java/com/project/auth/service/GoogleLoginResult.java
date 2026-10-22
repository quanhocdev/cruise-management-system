
package com.project.auth.service;

import java.util.UUID;

import com.project.auth.model.Users;

public record GoogleLoginResult(
        boolean otpRequired,
        UUID userId,
        String message,
        Users user) {

    public static GoogleLoginResult otpRequired(
            UUID userId,
            String message) {
        return new GoogleLoginResult(
                true,
                userId,
                message,
                null);
    }

    public static GoogleLoginResult authenticated(Users user) {
        return new GoogleLoginResult(
                false,
                user.getId(),
                "Đăng nhập Google thành công",
                user);
    }
}
