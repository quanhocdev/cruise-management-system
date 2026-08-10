package com.project.tour.service;

import com.project.tour.dto.cruisearea.CreateCruiseAreaRequest;
import com.project.tour.dto.cruisearea.CruiseAreaResponse;
import com.project.tour.dto.cruisearea.UpdateCruiseAreaRequest;
import com.project.tour.exception.DuplicateResourceException;
import com.project.tour.exception.ResourceNotFoundException;
import com.project.tour.model.CruiseArea;
import com.project.tour.model.CruiseDeck;
import com.project.tour.model.enums.CruiseAreaStatus;
import com.project.tour.repository.CruiseAreaRepository;
import com.project.tour.repository.CruiseDeckRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class CruiseAreaService {
    private final CruiseAreaRepository areaRepository;
    private final CruiseDeckRepository deckRepository;

    public CruiseAreaService(
        CruiseAreaRepository areaRepository,
        CruiseDeckRepository deckRepository
    ) {
        this.areaRepository = areaRepository;
        this.deckRepository = deckRepository;
    }

    public CruiseAreaResponse createArea(UUID deckId, CreateCruiseAreaRequest request) {
        CruiseDeck deck = findDeck(deckId);
        String name = request.name().trim();
        if (areaRepository.existsByCruiseDeck_IdAndNameIgnoreCase(deckId, name)) {
            throw new DuplicateResourceException("Cruise area name already exists: " + name);
        }

        CruiseArea area = new CruiseArea();
        area.setCruiseDeck(deck);
        area.setName(name);
        area.setDescription(trimToNull(request.description()));
        area.setStatus(CruiseAreaStatus.ACTIVE);
        return toResponse(areaRepository.save(area));
    }

    @Transactional(readOnly = true)
    public CruiseAreaResponse getArea(UUID deckId, UUID areaId) {
        return toResponse(findArea(deckId, areaId));
    }

    @Transactional(readOnly = true)
    public List<CruiseAreaResponse> getAreas(UUID deckId, boolean activeOnly) {
        if (!deckRepository.existsById(deckId)) {
            throw new ResourceNotFoundException("Cruise deck not found with id: " + deckId);
        }
        List<CruiseArea> areas = activeOnly
            ? areaRepository.findAllByCruiseDeck_IdAndStatusOrderByNameAsc(
                deckId,
                CruiseAreaStatus.ACTIVE
            )
            : areaRepository.findAllByCruiseDeck_IdOrderByNameAsc(deckId);
        return areas.stream().map(this::toResponse).toList();
    }

    public CruiseAreaResponse updateArea(
        UUID deckId,
        UUID areaId,
        UpdateCruiseAreaRequest request
    ) {
        CruiseArea area = findArea(deckId, areaId);
        String name = request.name().trim();
        if (areaRepository.existsByCruiseDeck_IdAndNameIgnoreCaseAndIdNot(
            deckId,
            name,
            areaId
        )) {
            throw new DuplicateResourceException("Cruise area name already exists: " + name);
        }
        area.setName(name);
        area.setDescription(trimToNull(request.description()));
        area.setStatus(request.status());
        return toResponse(areaRepository.save(area));
    }

    public CruiseAreaResponse deactivateArea(UUID deckId, UUID areaId) {
        CruiseArea area = findArea(deckId, areaId);
        area.setStatus(CruiseAreaStatus.INACTIVE);
        return toResponse(areaRepository.save(area));
    }

    public CruiseAreaResponse updateImage(
        UUID deckId,
        UUID areaId,
        String imageUrl,
        String imagePublicId
    ) {
        CruiseArea area = findArea(deckId, areaId);
        area.setImageUrl(trimToNull(imageUrl));
        area.setImagePublicId(trimToNull(imagePublicId));
        return toResponse(areaRepository.save(area));
    }

    private CruiseDeck findDeck(UUID deckId) {
        return deckRepository.findById(deckId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Cruise deck not found with id: " + deckId
            ));
    }

    private CruiseArea findArea(UUID deckId, UUID areaId) {
        return areaRepository.findByIdAndCruiseDeck_Id(areaId, deckId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Cruise area not found with id: " + areaId + " in deck: " + deckId
            ));
    }

    private CruiseAreaResponse toResponse(CruiseArea area) {
        return new CruiseAreaResponse(
            area.getId(),
            area.getCruiseDeck().getId(),
            area.getName(),
            area.getDescription(),
            area.getStatus(),
            area.getImageUrl(),
            area.getImagePublicId()
        );
    }

    private String trimToNull(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
