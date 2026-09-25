package com.project.auth.service;

import com.project.auth.dto.CreateStaffRequest;
import com.project.auth.dto.StaffResponse;
import com.project.auth.dto.UpdateStaffRequest;
import com.project.auth.dto.UpdateStaffStatusRequest;
import java.util.List;

public interface AdminStaffService {
    StaffResponse createStaff(CreateStaffRequest request);

    List<StaffResponse> getAllStaff();

    StaffResponse getStaffById(Long id);

    StaffResponse updateStaff(
            Long id,
            UpdateStaffRequest request);

    StaffResponse updateStaffStatus(
            Long id,
            UpdateStaffStatusRequest request);
}
