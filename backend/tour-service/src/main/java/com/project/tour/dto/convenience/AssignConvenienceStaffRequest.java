
package com.project.tour.dto.convenience;

import com.project.tour.model.TypeConvenienceAssign;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record AssignConvenienceStaffRequest(
        @NotNull(message = "Tour ID không được để trống") UUID tourId,

        @NotNull(message = "Loại phân công không được để trống") TypeConvenienceAssign assignmentType,

        @NotNull(message = "Target ID không được để trống") UUID targetId,

        @NotEmpty(message = "Danh sách nhân viên không được để trống") List<@NotNull UUID> userIds) {
}
