package com.project.tour.dto.staff;

import com.project.tour.model.StatusAssignStaff;
import jakarta.validation.constraints.NotNull;

public record RespondAssignStaffRequest(
        @NotNull(message = "Trạng thái phản hồi không được để trống") StatusAssignStaff status,

        String rejectionReason) {
}