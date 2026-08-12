package com.project.tour.controller.cruise;

import com.project.tour.dto.cruise.area.CreateCruiseAreaRequest;
import com.project.tour.dto.cruise.area.CruiseAreaResponse;
import com.project.tour.dto.cruise.area.UpdateCruiseAreaRequest;
import com.project.tour.service.cruise.CruiseAreaService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/decks/{deckId}/areas")
public class CruiseAreaController {

    private final CruiseAreaService areaService;

    public CruiseAreaController(CruiseAreaService areaService) {
        this.areaService = areaService;
    }

    @PostMapping
    public ResponseEntity<CruiseAreaResponse> createArea(
            @PathVariable UUID deckId,
            @Valid @RequestBody CreateCruiseAreaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(areaService.createArea(deckId, request));
    }

    @GetMapping
    public ResponseEntity<List<CruiseAreaResponse>> getAreas(
            @PathVariable UUID deckId,
            @RequestParam(defaultValue = "false") boolean activeOnly) {
        return ResponseEntity.ok(areaService.getAreas(deckId, activeOnly));
    }

    @GetMapping("/{areaId}")
    public ResponseEntity<CruiseAreaResponse> getArea(
            @PathVariable UUID deckId,
            @PathVariable UUID areaId) {
        return ResponseEntity.ok(areaService.getArea(deckId, areaId));
    }

    @PutMapping("/{areaId}")
    public ResponseEntity<CruiseAreaResponse> updateArea(
            @PathVariable UUID deckId,
            @PathVariable UUID areaId,
            @Valid @RequestBody UpdateCruiseAreaRequest request) {
        return ResponseEntity.ok(areaService.updateArea(deckId, areaId, request));
    }

    @PatchMapping("/{areaId}/deactivate")
    public ResponseEntity<CruiseAreaResponse> deactivateArea(
            @PathVariable UUID deckId,
            @PathVariable UUID areaId) {
        return ResponseEntity.ok(areaService.deactivateArea(deckId, areaId));
    }
}
