
package com.project.tour.controller.onboard;

import com.project.tour.dto.onboard.AssignOnboardStaffRequest;
import com.project.tour.dto.onboard.AssignOnboardStaffResult;
import com.project.tour.dto.onboard.OnboardStaffAssignmentResponse;
import com.project.tour.exception.AppException;
import com.project.tour.model.TypeOnboardStaff;
import com.project.tour.service.onboard.OnboardAssignStaffService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/onboard/assignments")
public class OnboardAssignStaffController {

    private final OnboardAssignStaffService assignmentService;

    public OnboardAssignStaffController(
            OnboardAssignStaffService assignmentService) {
        this.assignmentService = assignmentService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AssignOnboardStaffResult assignStaff(
            @Valid @RequestBody AssignOnboardStaffRequest request,
            @AuthenticationPrincipal Jwt jwt) {

        UUID assignedBy = extractUserId(jwt);

        return assignmentService.assignStaff(request, assignedBy);
    }

    @DeleteMapping("/{assignmentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelAssignment(
            @PathVariable UUID assignmentId,
            @RequestParam UUID tourId,
            @RequestParam TypeOnboardStaff assignmentType,
            @RequestParam(required = false) UUID cruiseDeckId,
            @RequestParam(required = false) UUID activityCruiseTourId) {

        assignmentService.cancelAssignment(
                tourId,
                assignmentType,
                cruiseDeckId,
                activityCruiseTourId,
                assignmentId);
    }

    @GetMapping
    public List<OnboardStaffAssignmentResponse> getAssignments(
            @RequestParam UUID tourId,
            @RequestParam TypeOnboardStaff assignmentType,
            @RequestParam(required = false) UUID cruiseDeckId,
            @RequestParam(required = false) UUID activityCruiseTourId) {

        return assignmentService.getAssignments(
                tourId,
                assignmentType,
                cruiseDeckId,
                activityCruiseTourId);
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
