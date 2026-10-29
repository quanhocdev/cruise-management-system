package com.project.tour.controller.shore;

import com.project.tour.dto.shore.AssignShoreStaffRequest;
import com.project.tour.dto.shore.AssignShoreStaffResult;
import com.project.tour.dto.shore.ShoreStaffAssignmentResponse;
import com.project.tour.service.shore.ShoreAssignStaffService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/shore/visit-tours/{visitTourId}/assignments")
public class ShoreAssignStaffController {

    private final ShoreAssignStaffService shoreAssignStaffService;

    public ShoreAssignStaffController(
            ShoreAssignStaffService shoreAssignStaffService) {
        this.shoreAssignStaffService = shoreAssignStaffService;
    }

    /**
     * Phân công nhiều nhân viên SHORE_STAFF cho một VisitTour.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AssignShoreStaffResult assignStaff(
            @PathVariable UUID visitTourId,
            @Valid @RequestBody AssignShoreStaffRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        UUID assignedBy = extractUserId(jwt);

        return shoreAssignStaffService.assignStaff(
                visitTourId,
                request,
                assignedBy);
    }

    /**
     * Lấy danh sách nhân viên đã được phân công cho một VisitTour.
     */
    @GetMapping
    public List<ShoreStaffAssignmentResponse> getAssignments(
            @PathVariable UUID visitTourId) {
        return shoreAssignStaffService.getAssignments(visitTourId);
    }

    private UUID extractUserId(Jwt jwt) {
        if (jwt == null) {
            throw new com.project.tour.exception.AppException(
                    "Không tìm thấy thông tin xác thực",
                    HttpStatus.UNAUTHORIZED);
        }

        String userId = jwt.getClaimAsString("userId");

        if (userId == null || userId.isBlank()) {
            throw new com.project.tour.exception.AppException(
                    "JWT không chứa userId",
                    HttpStatus.UNAUTHORIZED);
        }

        try {
            return UUID.fromString(userId);
        } catch (IllegalArgumentException exception) {
            throw new com.project.tour.exception.AppException(
                    "userId trong JWT không hợp lệ",
                    HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * Hủy phân công nhân viên SHORE_STAFF.
     */
    @DeleteMapping("/{assignmentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelAssignment(
            @PathVariable UUID visitTourId,
            @PathVariable UUID assignmentId) {

        shoreAssignStaffService.cancelAssignment(
                visitTourId,
                assignmentId);
    }
}