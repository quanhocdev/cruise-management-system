
package com.project.auth.dto;

import java.util.UUID;

public class VerifyOtpRequest {

    private UUID userId;
    private String otp;

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
