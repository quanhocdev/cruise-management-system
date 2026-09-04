package com.project.booking.controller;

import com.project.booking.dto.*;
import com.project.booking.service.PosService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class PosController {
    private final PosService service;
    public PosController(PosService service) { this.service = service; }

    @PostMapping("/api/admin/pos-terminals")
    ResponseEntity<PosTerminalRegistrationResponse> register(
            @Valid @RequestBody RegisterPosTerminalRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.register(request));
    }

    @PostMapping("/api/v1/pos/transactions/sync")
    PosSyncResponse sync(
            @RequestHeader("X-Terminal-Code") String terminalCode,
            @RequestHeader("X-POS-Key") String posKey,
            @Valid @RequestBody PosSyncRequest request) {
        return service.sync(terminalCode, posKey, request);
    }
}
