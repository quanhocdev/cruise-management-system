package com.project.tour.controller.convenience;

import com.project.tour.dto.convenience.AssignConvenienceStaffRequest;
import com.project.tour.dto.convenience.AssignConvenienceStaffResult;
import com.project.tour.dto.convenience.ConvenienceStaffAssignmentResponse;
import com.project.tour.model.TypeConvenienceAssign;
import com.project.tour.service.convenience.ConvenienceAssignStaffService;
import com.project.tour.exception.AppException;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/convenience/assignments")
public class ConvenienceAssignStaffController {

    private final ConvenienceAssignStaffService assignmentService;

    public ConvenienceAssignStaffController(
            ConvenienceAssignStaffService assignmentService) {
        this.assignmentService = assignmentService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AssignConvenienceStaffResult assignStaff(
            @Valid @RequestBody AssignConvenienceStaffRequest request,
            @AuthenticationPrincipal Jwt jwt) {

        UUID assignedBy = extractUserId(jwt);

        return assignmentService.assignStaff(request, assignedBy);
    }

    @GetMapping
    public List<ConvenienceStaffAssignmentResponse> getAssignments(
            @RequestParam UUID tourId,
            @RequestParam TypeConvenienceAssign assignmentType,
            @RequestParam UUID targetId) {

        return assignmentService.getAssignments(
                tourId,
                assignmentType,
                targetId);
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