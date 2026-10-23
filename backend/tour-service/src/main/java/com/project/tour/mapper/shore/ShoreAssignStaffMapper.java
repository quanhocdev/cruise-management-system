package com.project.tour.mapper.shore;

import com.project.tour.client.auth.ShoreStaffResponse;
import com.project.tour.dto.shore.ShoreStaffAssignmentResponse;
import com.project.tour.model.ShoreAssignStaff;

public final class ShoreAssignStaffMapper {

    private ShoreAssignStaffMapper() {
    }

    public static ShoreStaffAssignmentResponse toResponse(
            ShoreAssignStaff assignment,
            ShoreStaffResponse staff) {

        return new ShoreStaffAssignmentResponse(
                assignment.getId(),
                assignment.getTourId(),
                assignment.getVisitTourId(),
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