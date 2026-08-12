package com.project.tour.controller.port;

import com.project.tour.dto.port.CreatePortRequest;
import com.project.tour.dto.port.PortResponse;
import com.project.tour.dto.port.UpdatePortRequest;
import com.project.tour.service.port.PortService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin/ports")
public class PortController {

    private final PortService portService;

    public PortController(PortService portService) {
        this.portService = portService;
    }

    // CREATE
    @PostMapping
    public ResponseEntity<PortResponse> createPort(
            @Valid @RequestBody CreatePortRequest request) {

        PortResponse response = portService.createPort(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // GET ALL
    @GetMapping
    public ResponseEntity<List<PortResponse>> getPorts(
            @RequestParam(defaultValue = "false") boolean activeOnly) {

        List<PortResponse> response = activeOnly
                ? portService.getActivePorts()
                : portService.getAllPorts();

        return ResponseEntity.ok(response);
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<PortResponse> getPortById(
            @PathVariable UUID id) {

        return ResponseEntity.ok(
                portService.getPortById(id));
    }

    // UPDATE
    @PatchMapping("/{id}")
    public ResponseEntity<PortResponse> updatePort(
            @PathVariable UUID id,
            @Valid @RequestBody UpdatePortRequest request) {

        return ResponseEntity.ok(
                portService.updatePort(id, request));
    }

    // DELETE / SOFT DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePort(
            @PathVariable UUID id) {

        portService.deactivatePort(id);

        return ResponseEntity.noContent().build();
    }
}