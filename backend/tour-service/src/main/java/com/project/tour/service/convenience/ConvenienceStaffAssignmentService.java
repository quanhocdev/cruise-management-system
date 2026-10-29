package com.project.tour.service.convenience;

import com.project.tour.client.auth.AuthConvenienceStaffClient;
import com.project.tour.client.auth.ShoreStaffResponse;
import com.project.tour.dto.convenience.ConvenienceStaffAssignmentResponse;
import com.project.tour.dto.staff.RespondAssignStaffRequest;
import com.project.tour.exception.AppException;
import com.project.tour.mapper.convenience.ConvenienceAssignStaffMapper;
import com.project.tour.model.ConvenienceAssignStaff;
import com.project.tour.model.StatusAssignStaff;
import com.project.tour.repository.convenience.ConvenienceAssignStaffRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ConvenienceStaffAssignmentService {

    private final ConvenienceAssignStaffRepository assignmentRepository;
    private final AuthConvenienceStaffClient authConvenienceStaffClient;

    public ConvenienceStaffAssignmentService(
            ConvenienceAssignStaffRepository assignmentRepository,
            AuthConvenienceStaffClient authConvenienceStaffClient) {
        this.assignmentRepository = assignmentRepository;
        this.authConvenienceStaffClient = authConvenienceStaffClient;
    }

    /**
     * Lấy tất cả phân công của chính nhân viên CONVENIENCE_STAFF.
     */
    @Transactional(readOnly = true)
    public List<ConvenienceStaffAssignmentResponse> getMyAssignments(UUID userId) {

        if (userId == null) {
            throw new AppException(
                    "Không xác định được nhân viên đăng nhập",
                    HttpStatus.UNAUTHORIZED);
        }

        List<ConvenienceAssignStaff> assignments = assignmentRepository
                .findAllByUserIdOrderByAssignedAtDesc(userId);

        if (assignments.isEmpty()) {
            return List.of();
        }

        List<ShoreStaffResponse> staffList = authConvenienceStaffClient
                .getConvenienceStaffByIds(List.of(userId));

        ShoreStaffResponse staff = staffList.isEmpty()
                ? null
                : staffList.get(0);

        return assignments.stream()
                .map(assignment -> ConvenienceAssignStaffMapper.toResponse(
                        assignment,
                        staff))
                .toList();
    }

    /**
     * Nhân viên CONVENIENCE_STAFF chấp nhận hoặc từ chối phân công.
     */
    @Transactional
    public ConvenienceStaffAssignmentResponse respondToAssignment(
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

        ConvenienceAssignStaff assignment = assignmentRepository
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

        ConvenienceAssignStaff savedAssignment = assignmentRepository.save(assignment);

        List<ShoreStaffResponse> staffList = authConvenienceStaffClient
                .getConvenienceStaffByIds(List.of(userId));

        ShoreStaffResponse staff = staffList.isEmpty()
                ? null
                : staffList.get(0);

        return ConvenienceAssignStaffMapper.toResponse(
                savedAssignment,
                staff);
    }
}