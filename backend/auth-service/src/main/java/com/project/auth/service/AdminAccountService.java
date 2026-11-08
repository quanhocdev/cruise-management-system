package com.project.auth.service;

import java.util.List;
import java.util.UUID;

import com.project.auth.dto.StaffResponse;
import com.project.auth.dto.UpdateAccountRequest;
import com.project.auth.dto.UpdateStaffStatusRequest;

public interface AdminAccountService {

    List<StaffResponse> getAllAccounts();

    StaffResponse getAccountById(UUID id);

    StaffResponse updateAccount(UUID id, UpdateAccountRequest request);

    StaffResponse updateAccountStatus(
            UUID id,
            UpdateStaffStatusRequest request);
}