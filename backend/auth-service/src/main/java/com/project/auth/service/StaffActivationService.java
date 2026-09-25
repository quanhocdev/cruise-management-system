package com.project.auth.service;

import com.project.auth.dto.ActivateTokenRequest;
import com.project.auth.dto.SetPasswordRequest;

public interface StaffActivationService {

        String verifyActivationToken(
                        ActivateTokenRequest request);

        void setPassword(
                        SetPasswordRequest request);
}