
package com.project.tour.mapper.onboard;

import com.project.tour.client.auth.ShoreStaffResponse;
import com.project.tour.dto.onboard.OnboardStaffAssignmentResponse;
import com.project.tour.model.OnboardAssignStaff;

public final class OnboardAssignStaffMapper {

    private OnboardAssignStaffMapper() {
    }

    public static OnboardStaffAssignmentResponse toResponse(
            OnboardAssignStaff assignment,
            ShoreStaffResponse staff) {

        return new OnboardStaffAssignmentResponse(
                assignment.getId(),
                assignment.getTourId(),
                assignment.getAssignmentType(),
                assignment.getCruiseDeckId(),
                assignment.getActivityCruiseTourId(),
                assignment.getUserId(),
                staff == null ? null : staff.username(),
                staff == null ? null : staff.email(),
                staff == null ? null : staff.fullName(),
                staff == null ? null : staff.phone(),
                staff == null ? null : staff.avatarUrl(),
                assignment.getStatus(),
                assignment.getRejectionReason(),
                assignment.getAssignedBy(),
                assignment.getAssignedAt(),
                assignment.getRespondedAt());
    }
}
