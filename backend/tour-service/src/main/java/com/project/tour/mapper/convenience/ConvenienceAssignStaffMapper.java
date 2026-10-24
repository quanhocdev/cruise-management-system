
package com.project.tour.mapper.convenience;

import com.project.tour.client.auth.ShoreStaffResponse;
import com.project.tour.dto.convenience.ConvenienceStaffAssignmentResponse;
import com.project.tour.model.ConvenienceAssignStaff;

public final class ConvenienceAssignStaffMapper {

    private ConvenienceAssignStaffMapper() {
    }

    public static ConvenienceStaffAssignmentResponse toResponse(
            ConvenienceAssignStaff assignment,
            ShoreStaffResponse staff) {

        return new ConvenienceStaffAssignmentResponse(
                assignment.getId(),
                assignment.getTourId(),
                assignment.getAssignmentType(),
                assignment.getTargetId(),
                assignment.getUserId(),
                staff == null ? null : staff.username(),
                staff == null ? null : staff.email(),
                staff == null ? null : staff.fullName(),
                staff == null ? null : staff.phone(),
                staff == null ? null : staff.avatarUrl(),
                assignment.getStatus(),
                assignment.getAssignedBy(),
                assignment.getAssignedAt(),
                assignment.getRespondedAt());
    }
}
