package com.project.tour.service.onboard;

import com.project.tour.client.auth.AuthOnboardStaffClient;
import com.project.tour.client.auth.ShoreStaffResponse;
import com.project.tour.dto.onboard.OnboardStaffAssignmentResponse;
import com.project.tour.dto.staff.RespondAssignStaffRequest;
import com.project.tour.exception.AppException;
import com.project.tour.mapper.onboard.OnboardAssignStaffMapper;
import com.project.tour.model.OnboardAssignStaff;
import com.project.tour.model.StatusAssignStaff;
import com.project.tour.repository.onboard.OnboardAssignStaffRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class OnboardStaffAssignmentService {

    private final OnboardAssignStaffRepository assignmentRepository;
    private final AuthOnboardStaffClient authOnboardStaffClient;

    public OnboardStaffAssignmentService(
            OnboardAssignStaffRepository assignmentRepository,
            AuthOnboardStaffClient authOnboardStaffClient) {
        this.assignmentRepository = assignmentRepository;
        this.authOnboardStaffClient = authOnboardStaffClient;
    }

    /**
     * Lấy tất cả phân công PATROL và ACTIVITY_CRUISE
     * của chính nhân viên ONBOARD_STAFF.
     */
    @Transactional(readOnly = true)
    public List<OnboardStaffAssignmentResponse> getMyAssignments(UUID userId) {

        if (userId == null) {
            throw new AppException(
                    "Không xác định được nhân viên đăng nhập",
                    HttpStatus.UNAUTHORIZED);
        }

        List<OnboardAssignStaff> assignments = assignmentRepository
                .findAllByUserIdOrderByAssignedAtDesc(userId);

        if (assignments.isEmpty()) {
            return List.of();
        }

        List<ShoreStaffResponse> staffList = authOnboardStaffClient
                .getOnboardStaffByIds(List.of(userId));

        ShoreStaffResponse staff = staffList.isEmpty()
                ? null
                : staffList.get(0);

        return assignments.stream()
                .map(assignment -> OnboardAssignStaffMapper.toResponse(
                        assignment,
                        staff))
                .toList();
    }

    /**
     * Nhân viên ONBOARD_STAFF chấp nhận hoặc từ chối phân công.
     */
    @Transactional
    public OnboardStaffAssignmentResponse respondToAssignment(
            UUID assignmentId,
            UUID userId,
            RespondAssignStaffRequest request) {

        if (userId == null) {
            throw new AppException(
                    "Không xác định được nhân viên đăng nhập",
                    HttpStatus.UNAUTHORIZED);
        }

        if (request == null || request.status() == null) {
            throw new AppException(
                    "Trạng thái phản hồi không được để trống",
                    HttpStatus.BAD_REQUEST);
        }

        StatusAssignStaff newStatus = request.status();

        if (newStatus != StatusAssignStaff.ACCEPTED
                && newStatus != StatusAssignStaff.REJECTED) {
            throw new AppException(
                    "Nhân viên chỉ được chấp nhận hoặc từ chối phân công",
                    HttpStatus.BAD_REQUEST);
        }

        String rejectionReason = request.rejectionReason();

        if (newStatus == StatusAssignStaff.REJECTED
                && (rejectionReason == null || rejectionReason.isBlank())) {
            throw new AppException(
                    "Vui lòng cung cấp lý do khi từ chối phân công",
                    HttpStatus.BAD_REQUEST);
        }

        OnboardAssignStaff assignment = assignmentRepository
                .findByIdAndUserIdAndStatus(
                        assignmentId,
                        userId,
                        StatusAssignStaff.PENDING)
                .orElseThrow(() -> new AppException(
                        "Không tìm thấy phân công đang chờ phản hồi của bạn",
                        HttpStatus.NOT_FOUND));

        assignment.setStatus(newStatus);

        if (newStatus == StatusAssignStaff.REJECTED) {
            assignment.setRejectionReason(rejectionReason.trim());
        } else {
            assignment.setRejectionReason(null);
        }

        assignment.setRespondedAt(LocalDateTime.now());

        OnboardAssignStaff savedAssignment = assignmentRepository.save(assignment);

        List<ShoreStaffResponse> staffList = authOnboardStaffClient
                .getOnboardStaffByIds(List.of(userId));

        ShoreStaffResponse staff = staffList.isEmpty()
                ? null
                : staffList.get(0);

        return OnboardAssignStaffMapper.toResponse(
                savedAssignment,
                staff);
    }
}