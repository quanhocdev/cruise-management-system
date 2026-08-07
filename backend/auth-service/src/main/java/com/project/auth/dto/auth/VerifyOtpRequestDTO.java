package com.project.auth.dto.auth;

public class VerifyOtpRequestDTO {

    private Long userId;
    private String otp;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}