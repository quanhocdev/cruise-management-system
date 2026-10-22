
package com.project.auth.service;

import com.project.auth.dto.CreateStaffRequest;
import com.project.auth.dto.StaffResponse;
import com.project.auth.dto.UpdateStaffRequest;
import com.project.auth.dto.UpdateStaffStatusRequest;

import java.util.List;
import java.util.UUID;

public interface AdminStaffService {

        StaffResponse createStaff(CreateStaffRequest request);

        List<StaffResponse> getAllStaff();

        StaffResponse getStaffById(UUID id);

        StaffResponse updateStaff(
                        UUID id,
                        UpdateStaffRequest request);

        StaffResponse updateStaffStatus(
                        UUID id,
                        UpdateStaffStatusRequest request);
}
