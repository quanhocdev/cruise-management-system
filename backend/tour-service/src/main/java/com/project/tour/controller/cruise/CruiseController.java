package com.project.tour.controller.cruise;

import com.project.tour.dto.cruise.CreateCruiseRequest;
import com.project.tour.dto.cruise.CruiseResponse;
import com.project.tour.dto.cruise.UpdateCruiseRequest;
import com.project.tour.service.cruise.CruiseService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

    // =========================================================
    // CREATE - MULTIPART/FORM-DATA
    // =========================================================
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CruiseResponse> createCruise(
            @Valid @ModelAttribute CreateCruiseRequest request) {

        CruiseResponse response = cruiseService.createCruise(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // =========================================================
    // GET BY ID
    // =========================================================
    @GetMapping("/{id}")
    public ResponseEntity<CruiseResponse> getCruiseById(
            @PathVariable UUID id) {

        return ResponseEntity.ok(
                cruiseService.getCruiseById(id));
    }

    // =========================================================
    // GET BY CODE
    // =========================================================
    @GetMapping("/code/{code}")
    public ResponseEntity<CruiseResponse> getCruiseByCode(
            @PathVariable String code) {

        return ResponseEntity.ok(
                cruiseService.getCruiseByCode(code));
    }

    // =========================================================
    // GET ALL
    // =========================================================
    @GetMapping
    public ResponseEntity<List<CruiseResponse>> getCruises(
            @RequestParam(defaultValue = "false") boolean activeOnly) {

        List<CruiseResponse> response = activeOnly
                ? cruiseService.getActiveCruises()
                : cruiseService.getAllCruises();

        return ResponseEntity.ok(response);
    }

    // =========================================================
    // UPDATE - MULTIPART/FORM-DATA
    // =========================================================
    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CruiseResponse> updateCruise(
            @PathVariable UUID id,
            @Valid @ModelAttribute UpdateCruiseRequest request) {

        return ResponseEntity.ok(
                cruiseService.updateCruise(id, request));
    }

    // =========================================================
    // DELETE
    // =========================================================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCruise(
            @PathVariable UUID id) {

        cruiseService.deleteCruise(id);

        return ResponseEntity.noContent().build();
    }
}