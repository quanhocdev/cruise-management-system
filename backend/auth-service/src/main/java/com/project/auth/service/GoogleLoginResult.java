
package com.project.auth.service;

import com.project.auth.model.Users;

public record GoogleLoginResult(
        boolean otpRequired,
        Long userId,
        String message,
        Users user) {

    public static GoogleLoginResult otpRequired(
            Long userId,
            String message) {
        return new GoogleLoginResult(true, userId, message, null);
    }

    public static GoogleLoginResult authenticated(Users user) {
        return new GoogleLoginResult(
                false,
                user.getId(),
                "Đăng nhập Google thành công",
                user);
    }
}
