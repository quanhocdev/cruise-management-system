package com.project.tour.controller.cruise;

import com.project.tour.dto.cruise.CreateCruiseRequest;
import com.project.tour.dto.cruise.CruiseResponse;
import com.project.tour.dto.cruise.UpdateCruiseRequest;
import com.project.tour.service.cruise.CruiseService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/cruises")
public class CruiseController {

    private final CruiseService cruiseService;

    public CruiseController(CruiseService cruiseService) {
        this.cruiseService = cruiseService;
    }

    @PostMapping
    public ResponseEntity<CruiseResponse> createCruise(
            @Valid @RequestBody CreateCruiseRequest request) {
        CruiseResponse response = cruiseService.createCruise(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CruiseResponse> getCruiseById(
            @PathVariable UUID id) {
        CruiseResponse response = cruiseService.getCruiseById(id);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<CruiseResponse> getCruiseByCode(
            @PathVariable String code) {
        CruiseResponse response = cruiseService.getCruiseByCode(code);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<CruiseResponse>> getCruises(
            @RequestParam(defaultValue = "false") boolean activeOnly) {
        List<CruiseResponse> response;

        if (activeOnly) {
            response = cruiseService.getActiveCruises();
        } else {
            response = cruiseService.getAllCruises();
        }

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CruiseResponse> updateCruise(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateCruiseRequest request) {
        CruiseResponse response = cruiseService.updateCruise(id, request);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<CruiseResponse> deactivateCruise(
            @PathVariable UUID id) {
        CruiseResponse response = cruiseService.deactivateCruise(id);

        return ResponseEntity.ok(response);
    }
}