package com.project.tour.controller;

import com.project.tour.dto.port.CreatePortRequest;
import com.project.tour.dto.port.PortResponse;
import com.project.tour.dto.port.UpdatePortRequest;
import com.project.tour.service.PortService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/ports")
public class PortController {

    private final PortService portService;

    public PortController(PortService portService) {
        this.portService = portService;
    }

    @PostMapping
    public ResponseEntity<PortResponse> createPort(
        @Valid @RequestBody CreatePortRequest request
    ) {
        PortResponse response = portService.createPort(request);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PortResponse> getPortById(
        @PathVariable UUID id
    ) {
        PortResponse response = portService.getPortById(id);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<PortResponse>> getPorts(
        @RequestParam(defaultValue = "false") boolean activeOnly
    ) {
        List<PortResponse> response;

        if (activeOnly) {
            response = portService.getActivePorts();
        } else {
            response = portService.getAllPorts();
        }

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PortResponse> updatePort(
        @PathVariable UUID id,
        @Valid @RequestBody UpdatePortRequest request
    ) {
        PortResponse response = portService.updatePort(id, request);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<PortResponse> deactivatePort(
        @PathVariable UUID id
    ) {
        PortResponse response = portService.deactivatePort(id);

        return ResponseEntity.ok(response);
    }
}