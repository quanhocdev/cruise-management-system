package com.project.tour.controller.shore;

import com.project.tour.dto.shore.ShoreStaffAssignmentResponse;
import com.project.tour.dto.staff.RespondAssignStaffRequest;
import com.project.tour.exception.AppException;
import com.project.tour.service.shore.ShoreStaffAssignmentService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/shore-staff/assignments")
public class ShoreStaffAssignmentController {

    private final ShoreStaffAssignmentService staffAssignmentService;

    public ShoreStaffAssignmentController(
            ShoreStaffAssignmentService staffAssignmentService) {
        this.staffAssignmentService = staffAssignmentService;
    }

    /**
     * Nhân viên SHORE_STAFF xem các phân công của chính mình.
     */
    @GetMapping
    public List<ShoreStaffAssignmentResponse> getMyAssignments(
            @AuthenticationPrincipal Jwt jwt) {

        UUID userId = extractUserId(jwt);

        return staffAssignmentService.getMyAssignments(userId);
    }

    /**
     * Nhân viên SHORE_STAFF chấp nhận hoặc từ chối phân công.
     */
    @PatchMapping("/{assignmentId}/respond")
    public ShoreStaffAssignmentResponse respondToAssignment(
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