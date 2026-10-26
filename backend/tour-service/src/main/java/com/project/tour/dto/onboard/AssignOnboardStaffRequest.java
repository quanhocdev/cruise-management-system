
package com.project.tour.dto.onboard;

import com.project.tour.model.TypeOnboardStaff;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record AssignOnboardStaffRequest(

        @NotNull(message = "Tour ID không được để trống") UUID tourId,

        @NotNull(message = "Loại phân công không được để trống") TypeOnboardStaff assignmentType,

        UUID cruiseDeckId,

        UUID activityCruiseTourId,

        @NotEmpty(message = "Danh sách nhân viên không được để trống") List<@NotNull UUID> userIds) {
}
