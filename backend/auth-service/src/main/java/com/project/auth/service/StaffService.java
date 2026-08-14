package com.project.auth.service;

import java.util.List;

import com.project.auth.dto.ActivateTokenRequest;
import com.project.auth.dto.CreateStaffRequest;
import com.project.auth.dto.SetPasswordRequest;
import com.project.auth.dto.StaffResponse;
import com.project.auth.dto.UpdateStaffRequest;
import com.project.auth.dto.UpdateStaffStatusRequest;

public interface StaffService {

    StaffResponse createStaff(CreateStaffRequest request);

    List<StaffResponse> getAllStaff();

    StaffResponse getStaffById(Long id);

    StaffResponse updateStaff(
            Long id,
            UpdateStaffRequest request);

    StaffResponse updateStaffStatus(
            Long id,
            UpdateStaffStatusRequest request);

    String verifyActivationToken(
            ActivateTokenRequest request);

    void setPassword(
            SetPasswordRequest request);
}