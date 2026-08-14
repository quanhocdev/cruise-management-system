package com.project.tour.controller.cruise;

import com.project.tour.dto.cruise.area.CreateCruiseAreaRequest;
import com.project.tour.dto.cruise.area.CruiseAreaResponse;
import com.project.tour.dto.cruise.area.UpdateCruiseAreaRequest;
import com.project.tour.service.cruise.CruiseAreaService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/decks/{deckId}/areas")
public class CruiseAreaController {

        private final CruiseAreaService cruiseAreaService;

        public CruiseAreaController(
                        CruiseAreaService cruiseAreaService) {

                this.cruiseAreaService = cruiseAreaService;
        }

        // =====================================================
        // CREATE AREA
        // =====================================================

        @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        public ResponseEntity<CruiseAreaResponse> createArea(
                        @PathVariable UUID deckId,
                        @Valid @ModelAttribute CreateCruiseAreaRequest request) {

                CruiseAreaResponse response = cruiseAreaService.create(deckId, request);

                return ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body(response);
        }

        // =====================================================
        // GET ALL AREAS
        // =====================================================

        @GetMapping
        public ResponseEntity<List<CruiseAreaResponse>> getAreas(
                        @PathVariable UUID deckId,
                        @RequestParam(defaultValue = "false") boolean activeOnly) {

                List<CruiseAreaResponse> response = activeOnly
                                ? cruiseAreaService.getActive(deckId)
                                : cruiseAreaService.getAll(deckId);

                return ResponseEntity.ok(response);
        }

        // =====================================================
        // GET AREA BY ID
        // =====================================================

        @GetMapping("/{areaId}")
        public ResponseEntity<CruiseAreaResponse> getAreaById(
                        @PathVariable UUID deckId,
                        @PathVariable UUID areaId) {

                return ResponseEntity.ok(
                                cruiseAreaService.getById(
                                                deckId,
                                                areaId));
        }

        // =====================================================
        // UPDATE AREA
        // =====================================================

        @PatchMapping(value = "/{areaId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        public ResponseEntity<CruiseAreaResponse> updateArea(
                        @PathVariable UUID deckId,
                        @PathVariable UUID areaId,
                        @Valid @ModelAttribute UpdateCruiseAreaRequest request) {

                return ResponseEntity.ok(
                                cruiseAreaService.update(
                                                deckId,
                                                areaId,
                                                request));
        }

        // =====================================================
        // DEACTIVATE
        // =====================================================

        @PatchMapping("/{areaId}/deactivate")
        public ResponseEntity<CruiseAreaResponse> deactivateArea(
                        @PathVariable UUID deckId,
                        @PathVariable UUID areaId) {

                return ResponseEntity.ok(
                                cruiseAreaService.deactivate(
                                                deckId,
                                                areaId));
        }

        // =====================================================
        // DELETE
        // =====================================================

        @DeleteMapping("/{areaId}")
        public ResponseEntity<Void> deleteArea(
                        @PathVariable UUID deckId,
                        @PathVariable UUID areaId) {

                cruiseAreaService.delete(
                                deckId,
                                areaId);

                return ResponseEntity.noContent().build();
        }
}