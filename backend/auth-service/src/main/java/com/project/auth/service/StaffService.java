package com.project.auth.service;

import com.project.auth.dto.ActivateTokenRequest;
import com.project.auth.dto.CreateStaffRequest;
import com.project.auth.dto.CreateStaffResponse;
import com.project.auth.dto.SetPasswordRequest;

public interface StaffService {

    CreateStaffResponse createStaff(CreateStaffRequest request);

    String verifyActivationToken(ActivateTokenRequest request);

    void setPassword(SetPasswordRequest request);

}