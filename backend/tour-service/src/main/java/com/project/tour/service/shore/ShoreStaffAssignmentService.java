package com.project.tour.service.shore;

import com.project.tour.client.auth.AuthShoreStaffClient;
import com.project.tour.client.auth.ShoreStaffResponse;
import com.project.tour.dto.shore.ShoreStaffAssignmentResponse;
import com.project.tour.dto.staff.RespondAssignStaffRequest;
import com.project.tour.exception.AppException;
import com.project.tour.mapper.shore.ShoreAssignStaffMapper;
import com.project.tour.model.ShoreAssignStaff;
import com.project.tour.model.StatusAssignStaff;
import com.project.tour.repository.shore.ShoreAssignStaffRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ShoreStaffAssignmentService {

    private final ShoreAssignStaffRepository assignmentRepository;
    private final AuthShoreStaffClient authStaffClient;

    public ShoreStaffAssignmentService(
            ShoreAssignStaffRepository assignmentRepository,
            AuthShoreStaffClient authStaffClient) {
        this.assignmentRepository = assignmentRepository;
        this.authStaffClient = authStaffClient;
    }

    /**
     * Lấy danh sách phân công của chính nhân viên SHORE_STAFF.
     */
    @Transactional(readOnly = true)
    public List<ShoreStaffAssignmentResponse> getMyAssignments(UUID userId) {

        if (userId == null) {
            throw new AppException(
                    "Không xác định được nhân viên đăng nhập",
                    HttpStatus.UNAUTHORIZED);
        }

        List<ShoreAssignStaff> assignments = assignmentRepository
                .findAllByUserIdOrderByAssignedAtDesc(userId);

        if (assignments.isEmpty()) {
            return List.of();
        }

        List<ShoreStaffResponse> staffList = authStaffClient
                .getShoreStaffByIds(List.of(userId));

        Map<UUID, ShoreStaffResponse> staffById = staffList.stream()
                .collect(Collectors.toMap(
                        ShoreStaffResponse::userId,
                        staff -> staff,
                        (first, second) -> first));

        return assignments.stream()
                .map(assignment -> ShoreAssignStaffMapper.toResponse(
                        assignment,
                        staffById.get(assignment.getUserId())))
                .toList();
    }

    /**
     * Nhân viên SHORE_STAFF chấp nhận hoặc từ chối phân công.
     */
    @Transactional
    public ShoreStaffAssignmentResponse respondToAssignment(
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

        ShoreAssignStaff assignment = assignmentRepository
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

        ShoreAssignStaff savedAssignment = assignmentRepository.save(assignment);

        List<ShoreStaffResponse> staffList = authStaffClient
                .getShoreStaffByIds(List.of(userId));

        ShoreStaffResponse staff = staffList.isEmpty()
                ? null
                : staffList.get(0);

        return ShoreAssignStaffMapper.toResponse(savedAssignment, staff);
    }
}