
package com.project.auth.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class GoogleVerifyOtpRequest {

    @NotNull(message = "User ID không được để trống")
    private UUID userId;

    @NotBlank(message = "OTP không được để trống")
    @Pattern(regexp = "\\d{6}", message = "OTP phải gồm 6 chữ số")
    private String otp;

    public GoogleVerifyOtpRequest() {
    }

    public GoogleVerifyOtpRequest(UUID userId, String otp) {
        this.userId = userId;
        this.otp = otp;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}
