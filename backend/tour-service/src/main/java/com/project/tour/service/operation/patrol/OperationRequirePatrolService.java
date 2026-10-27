
package com.project.tour.service.operation.patrol;

import com.project.tour.dto.operation.patrol.PatrolRequirementItemRequest;
import com.project.tour.dto.operation.patrol.PatrolRequirementRequest;
import com.project.tour.dto.operation.patrol.PatrolRequirementResponse;
import com.project.tour.exception.AppException;
import com.project.tour.model.OperationRequirePatrol;
import com.project.tour.model.admin.CruiseDeck;
import com.project.tour.model.enums.cruise.CruiseDeckStatus;
import com.project.tour.model.scheduler.Tour;
import com.project.tour.repository.cruise.CruiseDeckRepository;
import com.project.tour.repository.operation.OperationRequirePatrolRepository;
import com.project.tour.repository.tour.TourRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OperationRequirePatrolService {

    private final OperationRequirePatrolRepository patrolRepository;
    private final TourRepository tourRepository;
    private final CruiseDeckRepository cruiseDeckRepository;

    public OperationRequirePatrolService(
            OperationRequirePatrolRepository patrolRepository,
            TourRepository tourRepository,
            CruiseDeckRepository cruiseDeckRepository) {
        this.patrolRepository = patrolRepository;
        this.tourRepository = tourRepository;
        this.cruiseDeckRepository = cruiseDeckRepository;
    }

    /**
     * Lấy toàn bộ tầng ACTIVE của tàu thuộc Tour,
     * bao gồm cả tầng chưa được cấu hình yêu cầu tuần tra.
     */
    @Transactional(readOnly = true)
    public List<PatrolRequirementResponse> getRequirements(UUID tourId) {

        Tour tour = getTourWithCruise(tourId);

        UUID cruiseId = tour.getCruise().getId();

        List<CruiseDeck> activeDecks = cruiseDeckRepository.findAllByCruise_IdAndStatusOrderByDeckNumberAsc(
                cruiseId,
                CruiseDeckStatus.ACTIVE);

        Map<UUID, Integer> requiredCountByDeck = patrolRepository.findAllByTourId(tourId)
                .stream()
                .collect(Collectors.toMap(
                        item -> item.getCruiseDeck().getId(),
                        OperationRequirePatrol::getRequiredCount));

        return activeDecks.stream()
                .map(deck -> new PatrolRequirementResponse(
                        deck.getId(),
                        deck.getDeckNumber(),
                        requiredCountByDeck.getOrDefault(deck.getId(), 0)))
                .toList();
    }

    /**
     * Lưu danh sách yêu cầu tuần tra của Tour.
     *
     * requiredCount = 0: xóa yêu cầu tuần tra của tầng đó.
     * requiredCount > 0: tạo mới hoặc cập nhật yêu cầu.
     *
     * Danh sách gửi lên là danh sách đầy đủ các tầng cần cấu hình.
     */
    @Transactional
    public List<PatrolRequirementResponse> saveRequirements(
            UUID tourId,
            PatrolRequirementRequest request,
            UUID createdBy) {

        if (request == null || request.requirements() == null) {
            throw new AppException(
                    "Patrol requirements must not be null",
                    HttpStatus.BAD_REQUEST);
        }

        if (createdBy == null) {
            throw new AppException(
                    "Cannot identify current user",
                    HttpStatus.UNAUTHORIZED);
        }

        Tour tour = getTourWithCruise(tourId);
        UUID cruiseId = tour.getCruise().getId();

        List<CruiseDeck> activeDecks = cruiseDeckRepository.findAllByCruise_IdAndStatusOrderByDeckNumberAsc(
                cruiseId,
                CruiseDeckStatus.ACTIVE);

        Map<UUID, CruiseDeck> activeDeckMap = activeDecks.stream()
                .collect(Collectors.toMap(
                        CruiseDeck::getId,
                        deck -> deck));

        Map<UUID, Integer> requestedCounts = new HashMap<>();

        for (PatrolRequirementItemRequest item : request.requirements()) {

            if (item == null || item.cruiseDeckId() == null) {
                throw new AppException(
                        "Cruise deck ID must not be null",
                        HttpStatus.BAD_REQUEST);
            }

            if (item.requiredCount() == null || item.requiredCount() < 0) {
                throw new AppException(
                        "Required count must be greater than or equal to zero",
                        HttpStatus.BAD_REQUEST);
            }

            if (!activeDeckMap.containsKey(item.cruiseDeckId())) {
                throw new AppException(
                        "Cruise deck does not belong to the assigned cruise or is inactive",
                        HttpStatus.BAD_REQUEST);
            }

            if (requestedCounts.putIfAbsent(
                    item.cruiseDeckId(),
                    item.requiredCount()) != null) {
                throw new AppException(
                        "Duplicate cruise deck in patrol requirements",
                        HttpStatus.BAD_REQUEST);
            }
        }

        List<OperationRequirePatrol> existingRequirements = patrolRepository.findAllByTourId(tourId);

        Map<UUID, OperationRequirePatrol> existingByDeck = existingRequirements.stream()
                .collect(Collectors.toMap(
                        item -> item.getCruiseDeck().getId(),
                        item -> item));

        List<OperationRequirePatrol> toSave = new ArrayList<>();
        List<OperationRequirePatrol> toDelete = new ArrayList<>();

        for (Map.Entry<UUID, Integer> entry : requestedCounts.entrySet()) {

            UUID deckId = entry.getKey();
            Integer requiredCount = entry.getValue();

            OperationRequirePatrol existing = existingByDeck.get(deckId);

            if (requiredCount == 0) {
                if (existing != null) {
                    toDelete.add(existing);
                }
                continue;
            }

            if (existing != null) {
                existing.setRequiredCount(requiredCount);
                toSave.add(existing);
            } else {
                OperationRequirePatrol requirement = new OperationRequirePatrol();

                requirement.setTourId(tourId);
                requirement.setCruiseDeck(activeDeckMap.get(deckId));
                requirement.setRequiredCount(requiredCount);
                requirement.setCreatedBy(createdBy);

                toSave.add(requirement);
            }
        }

        if (!toDelete.isEmpty()) {
            patrolRepository.deleteAll(toDelete);
        }

        if (!toSave.isEmpty()) {
            patrolRepository.saveAll(toSave);
        }

        return getRequirements(tourId);
    }

    private Tour getTourWithCruise(UUID tourId) {

        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new AppException(
                        "Tour not found",
                        HttpStatus.NOT_FOUND));

        if (tour.getCruise() == null) {
            throw new AppException(
                    "Tour has no cruise assigned",
                    HttpStatus.BAD_REQUEST);
        }

        return tour;
    }
}
