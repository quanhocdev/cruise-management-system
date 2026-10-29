package com.project.tour.controller.convenience;

import com.project.tour.dto.convenience.ConvenienceStaffAssignmentResponse;
import com.project.tour.dto.staff.RespondAssignStaffRequest;
import com.project.tour.exception.AppException;
import com.project.tour.service.convenience.ConvenienceStaffAssignmentService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/convenience-staff/assignments")
public class ConvenienceStaffAssignmentController {

    private final ConvenienceStaffAssignmentService staffAssignmentService;

    public ConvenienceStaffAssignmentController(
            ConvenienceStaffAssignmentService staffAssignmentService) {
        this.staffAssignmentService = staffAssignmentService;
    }

    /**
     * Nhân viên CONVENIENCE_STAFF xem các phân công của chính mình.
     */
    @GetMapping
    public List<ConvenienceStaffAssignmentResponse> getMyAssignments(
            @AuthenticationPrincipal Jwt jwt) {

        UUID userId = extractUserId(jwt);

        return staffAssignmentService.getMyAssignments(userId);
    }

    /**
     * Nhân viên CONVENIENCE_STAFF chấp nhận hoặc từ chối phân công.
     */
    @PatchMapping("/{assignmentId}/respond")
    public ConvenienceStaffAssignmentResponse respondToAssignment(
            @PathVariable UUID assignmentId,
            @Valid @RequestBody RespondAssignStaffRequest request,
            @AuthenticationPrincipal Jwt jwt) {

        UUID userId = extractUserId(jwt);

        return staffAssignmentService.respondToAssignment(
                assignmentId,
                userId,
                request);
    }

    private UUID extractUserId(Jwt jwt) {

        if (jwt == null) {
            throw new AppException(
                    "Không tìm thấy thông tin xác thực",
                    HttpStatus.UNAUTHORIZED);
        }

        String userId = jwt.getClaimAsString("userId");

        if (userId == null || userId.isBlank()) {
            throw new AppException(
                    "JWT không chứa userId",
                    HttpStatus.UNAUTHORIZED);
        }

        try {
            return UUID.fromString(userId);
        } catch (IllegalArgumentException exception) {
            throw new AppException(
                    "userId trong JWT không hợp lệ",
                    HttpStatus.UNAUTHORIZED);
        }
    }
}