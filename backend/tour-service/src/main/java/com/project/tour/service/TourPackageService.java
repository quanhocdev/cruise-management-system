package com.project.tour.service;

import com.project.tour.dto.tourpackage.*;
import com.project.tour.exception.DuplicateResourceException;
import com.project.tour.exception.ResourceNotFoundException;
import com.project.tour.model.TourPackage;
import com.project.tour.model.enums.TourPackageStatus;
import com.project.tour.repository.TourPackageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class TourPackageService {

    private final TourPackageRepository repository;

    public TourPackageService(TourPackageRepository repository) {
        this.repository = repository;
    }

    public TourPackageResponse create(CreateTourPackageRequest request) {
        String name = normalizeName(request.name());
        validateDuration(request.numberOfDays(), request.numberOfNights());
        if (repository.existsByNameIgnoreCase(name)) {
            throw new DuplicateResourceException("Tour package name already exists: " + name);
        }

        TourPackage tourPackage = new TourPackage();
        tourPackage.setName(name);
        tourPackage.setNumberOfDays(request.numberOfDays());
        tourPackage.setNumberOfNights(request.numberOfNights());
        tourPackage.setDescription(trimToNull(request.description()));
        tourPackage.setStatus(TourPackageStatus.ACTIVE);
        return toResponse(repository.save(tourPackage));
    }

    @Transactional(readOnly = true)
    public TourPackageResponse get(UUID id) {
        return toResponse(find(id));
    }

    @Transactional(readOnly = true)
    public List<TourPackageResponse> getAll(boolean activeOnly) {
        List<TourPackage> packages = activeOnly
            ? repository.findAllByStatusOrderByNameAsc(TourPackageStatus.ACTIVE)
            : repository.findAllByOrderByNameAsc();
        return packages.stream().map(this::toResponse).toList();
    }

    public TourPackageResponse update(UUID id, UpdateTourPackageRequest request) {
        TourPackage tourPackage = find(id);
        String name = normalizeName(request.name());
        validateDuration(request.numberOfDays(), request.numberOfNights());
        if (repository.existsByNameIgnoreCaseAndIdNot(name, id)) {
            throw new DuplicateResourceException("Tour package name already exists: " + name);
        }

        tourPackage.setName(name);
        tourPackage.setNumberOfDays(request.numberOfDays());
        tourPackage.setNumberOfNights(request.numberOfNights());
        tourPackage.setDescription(trimToNull(request.description()));
        tourPackage.setStatus(request.status());
        return toResponse(repository.save(tourPackage));
    }

    public TourPackageResponse deactivate(UUID id) {
        TourPackage tourPackage = find(id);
        tourPackage.setStatus(TourPackageStatus.INACTIVE);
        return toResponse(repository.save(tourPackage));
    }

    private TourPackage find(UUID id) {
        return repository.findById(id).orElseThrow(() ->
            new ResourceNotFoundException("Tour package not found with id: " + id));
    }

    private void validateDuration(Integer days, Integer nights) {
        if (nights > days) {
            throw new IllegalArgumentException(
                "Number of nights must not exceed number of days"
            );
        }
    }

    private String normalizeName(String name) {
        return name.trim().replaceAll("\\s+", " ");
    }

    private String trimToNull(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private TourPackageResponse toResponse(TourPackage tourPackage) {
        return new TourPackageResponse(
            tourPackage.getId(),
            tourPackage.getName(),
            tourPackage.getNumberOfDays(),
            tourPackage.getNumberOfNights(),
            tourPackage.getDescription(),
            tourPackage.getStatus()
        );
    }
}
