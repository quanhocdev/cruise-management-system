package com.project.booking.controller;

import com.project.booking.dto.PosIdentityDtos.*;
import com.project.booking.service.PosIdentityService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
public class PosIdentityController {
    private final PosIdentityService service;
    public PosIdentityController(PosIdentityService service) { this.service = service; }

    @GetMapping("/api/admin/pos-terminals")
    public List<TerminalSummary> terminals() { return service.listTerminals(); }

    @GetMapping("/api/admin/pos-terminals/voyages/{voyageId}/passengers")
    public List<PassengerSummary> passengers(@PathVariable UUID voyageId) {
        return service.listPassengers(voyageId);
    }

    @GetMapping("/api/admin/pos-terminals/credentials")
    public List<CredentialSummary> credentials(@RequestParam UUID voyageId) {
        return service.listCredentials(voyageId);
    }

    @PutMapping("/api/admin/pos-terminals/{code}/voyage")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void assign(@PathVariable String code, @Valid @RequestBody AssignVoyage request) {
        service.assignVoyage(code, request.voyageId());
    }

    @PostMapping("/api/admin/pos-terminals/credentials")
    @ResponseStatus(HttpStatus.CREATED)
    public CredentialIssued issue(@Valid @RequestBody IssueCredential request) { return service.issue(request); }

    @PatchMapping("/api/admin/pos-terminals/credentials/{id}/revoke")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void revoke(@PathVariable Long id) { service.revoke(id); }

    @PostMapping("/api/v1/pos/identify")
    public Identity identify(@RequestHeader(value = "X-Terminal-Code", required = false) String code,
            @RequestHeader(value = "X-POS-Key", required = false) String key, @Valid @RequestBody Lookup request) {
        if (code == null || code.isBlank() || key == null || key.isBlank())
            throw new com.project.booking.exception.BookingException(HttpStatus.UNAUTHORIZED, "POS credentials are required");
        return service.identify(code, key, request);
    }
}
