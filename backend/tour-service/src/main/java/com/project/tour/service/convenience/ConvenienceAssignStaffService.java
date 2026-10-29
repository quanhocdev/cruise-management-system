
package com.project.tour.service.convenience;

import com.project.tour.client.auth.AuthConvenienceStaffClient;
import com.project.tour.client.auth.ShoreStaffResponse;
import com.project.tour.dto.convenience.AssignConvenienceStaffRequest;
import com.project.tour.dto.convenience.AssignConvenienceStaffResult;
import com.project.tour.dto.convenience.ConvenienceStaffAssignmentResponse;
import com.project.tour.exception.AppException;
import com.project.tour.mapper.convenience.ConvenienceAssignStaffMapper;
import com.project.tour.model.ConvenienceAssignStaff;
import com.project.tour.model.StatusAssignStaff;
import com.project.tour.model.TypeConvenienceAssign;
import com.project.tour.model.convenience.product.ProductTour;
import com.project.tour.model.convenience.service.ServiceTour;
import com.project.tour.repository.convenience.ConvenienceAssignStaffRepository;
import com.project.tour.repository.convenience.ProductTourRepository;
import com.project.tour.repository.convenience.ServiceTourRepository;

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

@Service
public class ConvenienceAssignStaffService {

        private final ConvenienceAssignStaffRepository assignmentRepository;
        private final ProductTourRepository productTourRepository;
        private final ServiceTourRepository serviceTourRepository;
        private final AuthConvenienceStaffClient authConvenienceStaffClient;

        public ConvenienceAssignStaffService(
                        ConvenienceAssignStaffRepository assignmentRepository,
                        ProductTourRepository productTourRepository,
                        ServiceTourRepository serviceTourRepository,
                        AuthConvenienceStaffClient authConvenienceStaffClient) {

                this.assignmentRepository = assignmentRepository;
                this.productTourRepository = productTourRepository;
                this.serviceTourRepository = serviceTourRepository;
                this.authConvenienceStaffClient = authConvenienceStaffClient;
        }

        @Transactional
        public AssignConvenienceStaffResult assignStaff(
                        AssignConvenienceStaffRequest request,
                        UUID assignedBy) {

                if (assignedBy == null) {
                        throw new AppException(
                                        "Không xác định được người thực hiện phân công",
                                        HttpStatus.UNAUTHORIZED);
                }

                validateTarget(request);

                Set<UUID> requestedIds = new LinkedHashSet<>(request.userIds());

                List<ShoreStaffResponse> eligibleStaff = authConvenienceStaffClient.getConvenienceStaffByIds(
                                new ArrayList<>(requestedIds));

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

                List<ConvenienceAssignStaff> existingAssignments = eligibleIds.isEmpty()
                                ? List.of()
                                : assignmentRepository
                                                .findAllByTourIdAndAssignmentTypeAndTargetIdAndUserIdIn(
                                                                request.tourId(),
                                                                request.assignmentType(),
                                                                request.targetId(),
                                                                eligibleIds);

                Set<UUID> existingUserIds = existingAssignments.stream()
                                .map(ConvenienceAssignStaff::getUserId)
                                .collect(Collectors.toSet());

                List<UUID> alreadyAssigned = requestedIds.stream()
                                .filter(staffById::containsKey)
                                .filter(existingUserIds::contains)
                                .toList();

                List<ConvenienceAssignStaff> newAssignments = requestedIds.stream()
                                .filter(staffById::containsKey)
                                .filter(userId -> !existingUserIds.contains(userId))
                                .map(userId -> {
                                        ConvenienceAssignStaff assignment = new ConvenienceAssignStaff();

                                        assignment.setTourId(request.tourId());
                                        assignment.setAssignmentType(request.assignmentType());
                                        assignment.setTargetId(request.targetId());
                                        assignment.setUserId(userId);
                                        assignment.setAssignedBy(assignedBy);
                                        assignment.setStatus(StatusAssignStaff.PENDING);
                                        assignment.setRespondedAt(null);
                                        assignment.setRejectionReason(null);

                                        return assignment;
                                })
                                .toList();

                List<ConvenienceAssignStaff> savedAssignments = assignmentRepository.saveAll(newAssignments);

                List<ConvenienceStaffAssignmentResponse> assigned = savedAssignments.stream()
                                .map(assignment -> ConvenienceAssignStaffMapper.toResponse(
                                                assignment,
                                                staffById.get(assignment.getUserId())))
                                .toList();

                return new AssignConvenienceStaffResult(
                                assigned,
                                alreadyAssigned,
                                invalidUserIds);
        }

        @Transactional(readOnly = true)
        public List<ConvenienceStaffAssignmentResponse> getAssignments(
                        UUID tourId,
                        TypeConvenienceAssign assignmentType,
                        UUID targetId) {

                validateTarget(tourId, assignmentType, targetId);

                List<ConvenienceAssignStaff> assignments = assignmentRepository
                                .findAllByTourIdAndAssignmentTypeAndTargetIdOrderByAssignedAtDesc(
                                                tourId,
                                                assignmentType,
                                                targetId);

                if (assignments.isEmpty()) {
                        return List.of();
                }

                List<ShoreStaffResponse> staffList = authConvenienceStaffClient.getAllConvenienceStaff();

                Map<UUID, ShoreStaffResponse> staffById = staffList.stream()
                                .collect(Collectors.toMap(
                                                ShoreStaffResponse::userId,
                                                Function.identity(),
                                                (first, second) -> first));

                return assignments.stream()
                                .map(assignment -> ConvenienceAssignStaffMapper.toResponse(
                                                assignment,
                                                staffById.get(assignment.getUserId())))
                                .toList();
        }

        private void validateTarget(AssignConvenienceStaffRequest request) {
                validateTarget(
                                request.tourId(),
                                request.assignmentType(),
                                request.targetId());
        }

        private void validateTarget(
                        UUID tourId,
                        TypeConvenienceAssign assignmentType,
                        UUID targetId) {

                if (tourId == null || assignmentType == null || targetId == null) {
                        throw new AppException(
                                        "Tour ID, loại phân công và target ID không được để trống",
                                        HttpStatus.BAD_REQUEST);
                }

                UUID actualTourId;

                if (assignmentType == TypeConvenienceAssign.PRODUCT) {
                        ProductTour productTour = productTourRepository.findById(targetId)
                                        .orElseThrow(() -> new AppException(
                                                        "Không tìm thấy ProductTour cần phân công",
                                                        HttpStatus.NOT_FOUND));

                        actualTourId = productTour.getTourId();

                } else if (assignmentType == TypeConvenienceAssign.SERVICE) {
                        ServiceTour serviceTour = serviceTourRepository.findById(targetId)
                                        .orElseThrow(() -> new AppException(
                                                        "Không tìm thấy ServiceTour cần phân công",
                                                        HttpStatus.NOT_FOUND));

                        actualTourId = serviceTour.getTourId();

                } else {
                        throw new AppException(
                                        "Loại phân công không hợp lệ",
                                        HttpStatus.BAD_REQUEST);
                }

                if (!tourId.equals(actualTourId)) {
                        throw new AppException(
                                        "Đối tượng phân công không thuộc tour được cung cấp",
                                        HttpStatus.BAD_REQUEST);
                }
        }
}
