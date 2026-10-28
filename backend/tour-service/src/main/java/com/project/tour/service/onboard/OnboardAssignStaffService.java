
package com.project.tour.service.onboard;

import com.project.tour.client.auth.AuthOnboardStaffClient;
import com.project.tour.client.auth.ShoreStaffResponse;
import com.project.tour.dto.onboard.AssignOnboardStaffRequest;
import com.project.tour.dto.onboard.AssignOnboardStaffResult;
import com.project.tour.dto.onboard.OnboardStaffAssignmentResponse;
import com.project.tour.exception.AppException;
import com.project.tour.mapper.onboard.OnboardAssignStaffMapper;
import com.project.tour.model.OnboardAssignStaff;
import com.project.tour.model.StatusAssignStaff;
import com.project.tour.model.TypeOnboardStaff;
import com.project.tour.model.admin.CruiseDeck;
import com.project.tour.model.enums.cruise.CruiseDeckStatus;
import com.project.tour.model.onboard.ActivityCruiseTour;
import com.project.tour.model.scheduler.Tour;
import com.project.tour.repository.cruise.CruiseDeckRepository;
import com.project.tour.repository.onboard.ActivityCruiseTourRepository;
import com.project.tour.repository.onboard.OnboardAssignStaffRepository;
import com.project.tour.repository.scheduler.TourRepository;

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
public class OnboardAssignStaffService {

        private final OnboardAssignStaffRepository assignmentRepository;
        private final TourRepository tourRepository;
        private final CruiseDeckRepository cruiseDeckRepository;
        private final ActivityCruiseTourRepository activityCruiseTourRepository;
        private final AuthOnboardStaffClient authOnboardStaffClient;

        public OnboardAssignStaffService(
                        OnboardAssignStaffRepository assignmentRepository,
                        TourRepository tourRepository,
                        CruiseDeckRepository cruiseDeckRepository,
                        ActivityCruiseTourRepository activityCruiseTourRepository,
                        AuthOnboardStaffClient authOnboardStaffClient) {

                this.assignmentRepository = assignmentRepository;
                this.tourRepository = tourRepository;
                this.cruiseDeckRepository = cruiseDeckRepository;
                this.activityCruiseTourRepository = activityCruiseTourRepository;
                this.authOnboardStaffClient = authOnboardStaffClient;
        }

        @Transactional
        public AssignOnboardStaffResult assignStaff(
                        AssignOnboardStaffRequest request,
                        UUID assignedBy) {

                if (assignedBy == null) {
                        throw new AppException(
                                        "Không xác định được người thực hiện phân công",
                                        HttpStatus.UNAUTHORIZED);
                }

                validateTarget(
                                request.tourId(),
                                request.assignmentType(),
                                request.cruiseDeckId(),
                                request.activityCruiseTourId());

                Set<UUID> requestedIds = new LinkedHashSet<>(request.userIds());

                List<ShoreStaffResponse> eligibleStaff = authOnboardStaffClient.getOnboardStaffByIds(
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

                List<OnboardAssignStaff> existingAssignments;

                if (eligibleIds.isEmpty()) {
                        existingAssignments = List.of();

                } else if (request.assignmentType() == TypeOnboardStaff.PATROL) {
                        existingAssignments = assignmentRepository
                                        .findAllByTourIdAndAssignmentTypeAndCruiseDeckIdAndUserIdIn(
                                                        request.tourId(),
                                                        request.assignmentType(),
                                                        request.cruiseDeckId(),
                                                        eligibleIds);

                } else {
                        existingAssignments = assignmentRepository
                                        .findAllByTourIdAndAssignmentTypeAndActivityCruiseTourIdAndUserIdIn(
                                                        request.tourId(),
                                                        request.assignmentType(),
                                                        request.activityCruiseTourId(),
                                                        eligibleIds);
                }

                Set<UUID> existingUserIds = existingAssignments.stream()
                                .map(OnboardAssignStaff::getUserId)
                                .collect(Collectors.toSet());

                List<UUID> alreadyAssigned = requestedIds.stream()
                                .filter(staffById::containsKey)
                                .filter(existingUserIds::contains)
                                .toList();

                List<OnboardAssignStaff> newAssignments = requestedIds.stream()
                                .filter(staffById::containsKey)
                                .filter(userId -> !existingUserIds.contains(userId))
                                .map(userId -> {
                                        OnboardAssignStaff assignment = new OnboardAssignStaff();

                                        assignment.setTourId(request.tourId());
                                        assignment.setAssignmentType(request.assignmentType());
                                        assignment.setCruiseDeckId(request.cruiseDeckId());
                                        assignment.setActivityCruiseTourId(
                                                        request.activityCruiseTourId());
                                        assignment.setUserId(userId);
                                        assignment.setAssignedBy(assignedBy);
                                        assignment.setStatus(StatusAssignStaff.PENDING);
                                        assignment.setRespondedAt(null);
                                        assignment.setRejectionReason(null);

                                        return assignment;
                                })
                                .toList();

                List<OnboardAssignStaff> savedAssignments = assignmentRepository.saveAll(newAssignments);

                List<OnboardStaffAssignmentResponse> assigned = savedAssignments.stream()
                                .map(assignment -> OnboardAssignStaffMapper.toResponse(
                                                assignment,
                                                staffById.get(assignment.getUserId())))
                                .toList();

                return new AssignOnboardStaffResult(
                                assigned,
                                alreadyAssigned,
                                invalidUserIds);
        }

        @Transactional(readOnly = true)
        public List<OnboardStaffAssignmentResponse> getAssignments(
                        UUID tourId,
                        TypeOnboardStaff assignmentType,
                        UUID cruiseDeckId,
                        UUID activityCruiseTourId) {

                validateTarget(
                                tourId,
                                assignmentType,
                                cruiseDeckId,
                                activityCruiseTourId);

                List<OnboardAssignStaff> assignments;

                if (assignmentType == TypeOnboardStaff.PATROL) {
                        assignments = assignmentRepository
                                        .findAllByTourIdAndAssignmentTypeAndCruiseDeckIdOrderByAssignedAtDesc(
                                                        tourId,
                                                        assignmentType,
                                                        cruiseDeckId);
                } else {
                        assignments = assignmentRepository
                                        .findAllByTourIdAndAssignmentTypeAndActivityCruiseTourIdOrderByAssignedAtDesc(
                                                        tourId,
                                                        assignmentType,
                                                        activityCruiseTourId);
                }

                if (assignments.isEmpty()) {
                        return List.of();
                }

                List<ShoreStaffResponse> staffList = authOnboardStaffClient.getAllOnboardStaff();

                Map<UUID, ShoreStaffResponse> staffById = staffList.stream()
                                .collect(Collectors.toMap(
                                                ShoreStaffResponse::userId,
                                                Function.identity(),
                                                (first, second) -> first));

                return assignments.stream()
                                .map(assignment -> OnboardAssignStaffMapper.toResponse(
                                                assignment,
                                                staffById.get(assignment.getUserId())))
                                .toList();
        }

        private void validateTarget(
                        UUID tourId,
                        TypeOnboardStaff assignmentType,
                        UUID cruiseDeckId,
                        UUID activityCruiseTourId) {

                if (tourId == null || assignmentType == null) {
                        throw new AppException(
                                        "Tour ID và loại phân công không được để trống",
                                        HttpStatus.BAD_REQUEST);
                }

                Tour tour = tourRepository.findById(tourId)
                                .orElseThrow(() -> new AppException(
                                                "Không tìm thấy tour",
                                                HttpStatus.NOT_FOUND));

                if (assignmentType == TypeOnboardStaff.PATROL) {

                        if (cruiseDeckId == null || activityCruiseTourId != null) {
                                throw new AppException(
                                                "PATROL yêu cầu cruiseDeckId và không được gửi activityCruiseTourId",
                                                HttpStatus.BAD_REQUEST);
                        }

                        if (tour.getCruise() == null) {
                                throw new AppException(
                                                "Tour chưa được phân công tàu",
                                                HttpStatus.CONFLICT);
                        }

                        CruiseDeck deck = cruiseDeckRepository
                                        .findByIdAndCruise_Id(
                                                        cruiseDeckId,
                                                        tour.getCruise().getId())
                                        .orElseThrow(() -> new AppException(
                                                        "Không tìm thấy deck thuộc tàu của tour",
                                                        HttpStatus.NOT_FOUND));

                        if (deck.getStatus() != CruiseDeckStatus.ACTIVE) {
                                throw new AppException(
                                                "Không thể phân công nhân viên vào deck không hoạt động",
                                                HttpStatus.CONFLICT);
                        }

                } else if (assignmentType == TypeOnboardStaff.ACTIVITY_CRUISE) {

                        if (activityCruiseTourId == null || cruiseDeckId != null) {
                                throw new AppException(
                                                "ACTIVITY_CRUISE yêu cầu activityCruiseTourId và không được gửi cruiseDeckId",
                                                HttpStatus.BAD_REQUEST);
                        }

                        ActivityCruiseTour activityTour = activityCruiseTourRepository
                                        .findById(activityCruiseTourId)
                                        .orElseThrow(() -> new AppException(
                                                        "Không tìm thấy ActivityCruiseTour cần phân công",
                                                        HttpStatus.NOT_FOUND));

                        if (!tourId.equals(activityTour.getTourId())) {
                                throw new AppException(
                                                "ActivityCruiseTour không thuộc tour được cung cấp",
                                                HttpStatus.BAD_REQUEST);
                        }

                } else {
                        throw new AppException(
                                        "Loại phân công không hợp lệ",
                                        HttpStatus.BAD_REQUEST);
                }
        }
}
