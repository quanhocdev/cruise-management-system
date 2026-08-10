package com.project.tour.service;

import com.project.tour.dto.cruise.CreateCruiseRequest;
import com.project.tour.dto.cruise.CruiseResponse;
import com.project.tour.dto.cruise.UpdateCruiseRequest;
import com.project.tour.exception.DuplicateResourceException;
import com.project.tour.exception.ResourceNotFoundException;
import com.project.tour.model.Cruise;
import com.project.tour.model.enums.CruiseStatus;
import com.project.tour.repository.CruiseRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@Transactional
public class CruiseService {

    private final CruiseRepository cruiseRepository;

    public CruiseService(CruiseRepository cruiseRepository) {
        this.cruiseRepository = cruiseRepository;
    }

    public CruiseResponse createCruise(
        CreateCruiseRequest request
    ) {
        String normalizedCode = normalizeCode(request.code());

        if (cruiseRepository.existsByCodeIgnoreCase(normalizedCode)) {
            throw new DuplicateResourceException(
                "Cruise code already exists: " + normalizedCode
            );
        }

        Cruise cruise = new Cruise();

        cruise.setName(request.name().trim());
        cruise.setCode(normalizedCode);
        cruise.setDescription(trimToNull(request.description()));
        cruise.setTotalDecks(request.totalDecks());
        cruise.setMaxPassengers(request.maxPassengers());
        cruise.setStatus(CruiseStatus.ACTIVE);

        Cruise savedCruise = cruiseRepository.save(cruise);

        return toResponse(savedCruise);
    }

    @Transactional(readOnly = true)
    public CruiseResponse getCruiseById(UUID id) {
        Cruise cruise = findCruiseById(id);

        return toResponse(cruise);
    }

    @Transactional(readOnly = true)
    public CruiseResponse getCruiseByCode(String code) {
        String normalizedCode = normalizeCode(code);

        Cruise cruise = cruiseRepository
            .findByCodeIgnoreCase(normalizedCode)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Cruise not found with code: " + normalizedCode
            ));

        return toResponse(cruise);
    }

    @Transactional(readOnly = true)
    public List<CruiseResponse> getAllCruises() {
        return cruiseRepository
            .findAll(Sort.by(Sort.Direction.ASC, "name"))
            .stream()
            .map(this::toResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<CruiseResponse> getActiveCruises() {
        return cruiseRepository
            .findAllByStatusOrderByNameAsc(CruiseStatus.ACTIVE)
            .stream()
            .map(this::toResponse)
            .toList();
    }

    public CruiseResponse updateCruise(
        UUID id,
        UpdateCruiseRequest request
    ) {
        Cruise cruise = findCruiseById(id);

        cruise.setName(request.name().trim());
        cruise.setDescription(trimToNull(request.description()));
        cruise.setTotalDecks(request.totalDecks());
        cruise.setMaxPassengers(request.maxPassengers());
        cruise.setStatus(request.status());

        Cruise updatedCruise = cruiseRepository.save(cruise);

        return toResponse(updatedCruise);
    }

    public CruiseResponse deactivateCruise(UUID id) {
        Cruise cruise = findCruiseById(id);

        cruise.setStatus(CruiseStatus.INACTIVE);

        Cruise updatedCruise = cruiseRepository.save(cruise);

        return toResponse(updatedCruise);
    }

    public CruiseResponse updateCruiseImage(
        UUID id,
        String imageUrl,
        String imagePublicId
    ) {
        Cruise cruise = findCruiseById(id);

        cruise.setImageUrl(trimToNull(imageUrl));
        cruise.setImagePublicId(trimToNull(imagePublicId));

        Cruise updatedCruise = cruiseRepository.save(cruise);

        return toResponse(updatedCruise);
    }

    private Cruise findCruiseById(UUID id) {
        return cruiseRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Cruise not found with id: " + id
            ));
    }

    private CruiseResponse toResponse(Cruise cruise) {
        return new CruiseResponse(
            cruise.getId(),
            cruise.getName(),
            cruise.getCode(),
            cruise.getDescription(),
            cruise.getTotalDecks(),
            cruise.getMaxPassengers(),
            cruise.getImageUrl(),
            cruise.getImagePublicId(),
            cruise.getStatus(),
            cruise.getCreatedAt(),
            cruise.getUpdatedAt()
        );
    }

    private String normalizeCode(String code) {
        return code
            .trim()
            .toUpperCase(Locale.ROOT);
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }

        String trimmedValue = value.trim();

        return trimmedValue.isEmpty() ? null : trimmedValue;
    }
}