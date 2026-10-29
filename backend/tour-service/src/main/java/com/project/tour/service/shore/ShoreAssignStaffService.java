package com.project.tour.service.shore;

import com.project.tour.client.auth.AuthShoreStaffClient;
import com.project.tour.client.auth.ShoreStaffResponse;
import com.project.tour.dto.shore.AssignShoreStaffRequest;
import com.project.tour.dto.shore.AssignShoreStaffResult;
import com.project.tour.dto.shore.ShoreStaffAssignmentResponse;
import com.project.tour.mapper.shore.ShoreAssignStaffMapper;
import com.project.tour.model.ShoreAssignStaff;
import com.project.tour.model.StatusAssignStaff;
import com.project.tour.model.shore.VisitTour;
import com.project.tour.repository.shore.ShoreAssignStaffRepository;
import com.project.tour.repository.shore.VisitTourRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.project.tour.exception.AppException;

@Service
public class ShoreAssignStaffService {

        private final ShoreAssignStaffRepository assignmentRepository;
        private final VisitTourRepository visitTourRepository;
        private final AuthShoreStaffClient authStaffClient;

        public ShoreAssignStaffService(
                        ShoreAssignStaffRepository assignmentRepository,
                        VisitTourRepository visitTourRepository,
                        AuthShoreStaffClient authStaffClient) {
                this.assignmentRepository = assignmentRepository;
                this.visitTourRepository = visitTourRepository;
                this.authStaffClient = authStaffClient;
        }

        /**
         * Phân công nhiều nhân viên SHORE_STAFF cho một VisitTour.
         * Nhân viên không hợp lệ và nhân viên đã được phân công sẽ không được lưu.
         */
        @Transactional
        public AssignShoreStaffResult assignStaff(
                        UUID visitTourId,
                        AssignShoreStaffRequest request,
                        UUID assignedBy) {
                VisitTour visitTour = visitTourRepository.findById(visitTourId)
                                .orElseThrow(() -> new AppException(
                                                "Không tìm thấy lịch tham quan cần phân công",
                                                HttpStatus.NOT_FOUND));

                if (assignedBy == null) {
                        throw new AppException(
                                        "Không xác định được người thực hiện phân công",
                                        HttpStatus.UNAUTHORIZED);
                }

                // Loại bỏ UUID trùng nhau trong chính request, đồng thời giữ nguyên thứ tự.
                Set<UUID> requestedIds = new LinkedHashSet<>(request.userIds());

                // AuthStaffClient chỉ trả về nhân viên SHORE_STAFF hợp lệ.
                List<ShoreStaffResponse> eligibleStaff = authStaffClient
                                .getShoreStaffByIds(new ArrayList<>(requestedIds));

                Map<UUID, ShoreStaffResponse> staffById = eligibleStaff.stream()
                                .collect(Collectors.toMap(
                                                ShoreStaffResponse::userId,
                                                Function.identity(),
                                                (first, second) -> first,
                                                LinkedHashMap::new));

                List<UUID> invalidUserIds = requestedIds.stream()
                                .filter(userId -> !staffById.containsKey(userId))
                                .toList();

                Collection<UUID> eligibleIds = staffById.keySet();

                List<ShoreAssignStaff> existingAssignments = eligibleIds.isEmpty()
                                ? List.of()
                                : assignmentRepository.findAllByVisitTourIdAndUserIdIn(
                                                visitTourId,
                                                eligibleIds);

                Set<UUID> existingUserIds = existingAssignments.stream()
                                .map(ShoreAssignStaff::getUserId)
                                .collect(Collectors.toSet());

                List<UUID> alreadyAssigned = requestedIds.stream()
                                .filter(staffById::containsKey)
                                .filter(existingUserIds::contains)
                                .toList();

                List<ShoreAssignStaff> newAssignments = requestedIds.stream()
                                .filter(staffById::containsKey)
                                .filter(userId -> !existingUserIds.contains(userId))
                                .map(userId -> {
                                        ShoreAssignStaff assignment = new ShoreAssignStaff();

                                        assignment.setTourId(visitTour.getTourId());
                                        assignment.setVisitTourId(visitTour.getId());
                                        assignment.setUserId(userId);
                                        assignment.setAssignedBy(assignedBy);
                                        assignment.setStatus(StatusAssignStaff.PENDING);
                                        assignment.setRespondedAt(null);
                                        assignment.setRejectionReason(null);

                                        return assignment;
                                })
                                .toList();

                List<ShoreAssignStaff> savedAssignments = assignmentRepository.saveAll(newAssignments);

                List<ShoreStaffAssignmentResponse> assigned = savedAssignments.stream()
                                .map(assignment -> ShoreAssignStaffMapper.toResponse(
                                                assignment,
                                                staffById.get(assignment.getUserId())))
                                .toList();

                return new AssignShoreStaffResult(
                                assigned,
                                alreadyAssigned,
                                invalidUserIds);
        }

        /**
         * Lấy các nhân viên đã được phân công cho một VisitTour.
         */
        @Transactional(readOnly = true)
        public List<ShoreStaffAssignmentResponse> getAssignments(UUID visitTourId) {
                if (!visitTourRepository.existsById(visitTourId)) {
                        throw new AppException(
                                        "Không tìm thấy lịch tham quan",
                                        HttpStatus.NOT_FOUND);
                }

                List<ShoreAssignStaff> assignments = assignmentRepository.findAllByVisitTourIdOrderByAssignedAtDesc(
                                visitTourId);

                if (assignments.isEmpty()) {
                        return List.of();
                }

                List<ShoreStaffResponse> staffList = authStaffClient.getAllShoreStaff();

                Map<UUID, ShoreStaffResponse> staffById = staffList.stream()
                                .collect(Collectors.toMap(
                                                ShoreStaffResponse::userId,
                                                Function.identity(),
                                                (first, second) -> first));

                return assignments.stream()
                                .map(assignment -> ShoreAssignStaffMapper.toResponse(
                                                assignment,
                                                staffById.get(assignment.getUserId())))
                                .toList();
        }

        /**
         * Hủy phân công nhân viên SHORE_STAFF.
         * Chỉ được xóa phân công đang chờ phản hồi.
         */
        @Transactional
        public void cancelAssignment(UUID visitTourId, UUID assignmentId) {
                ShoreAssignStaff assignment = assignmentRepository
                                .findByIdAndVisitTourIdAndStatus(
                                                assignmentId,
                                                visitTourId,
                                                StatusAssignStaff.PENDING)
                                .orElseThrow(() -> new AppException(
                                                "Không tìm thấy phân công đang chờ phản hồi để hủy",
                                                HttpStatus.NOT_FOUND));

                assignmentRepository.delete(assignment);
        }
}