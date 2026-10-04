package com.project.booking.controller;

import com.project.booking.dto.nfc.NfcResolveRequest;
import com.project.booking.dto.nfc.NfcResolveResponse;
import com.project.booking.service.ConvenienceNfcService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/onboard/nfc")
public class OnboardNfcController {

    private final ConvenienceNfcService convenienceNfcService;

    public OnboardNfcController(
            ConvenienceNfcService convenienceNfcService) {

        this.convenienceNfcService = convenienceNfcService;
    }

    @PostMapping("/resolve")
    @PreAuthorize("hasRole('ONBOARD')")
    public ResponseEntity<NfcResolveResponse> resolve(
            @Valid @RequestBody NfcResolveRequest request) {

        return ResponseEntity.ok(
                convenienceNfcService.resolve(request));
    }
}