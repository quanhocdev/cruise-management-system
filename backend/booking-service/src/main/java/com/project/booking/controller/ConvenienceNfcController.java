package com.project.booking.controller;

import com.project.booking.dto.convenience.NfcResolveRequest;
import com.project.booking.dto.convenience.NfcResolveResponse;
import com.project.booking.service.ConvenienceNfcService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/convenience/nfc")
public class ConvenienceNfcController {

    private final ConvenienceNfcService convenienceNfcService;

    public ConvenienceNfcController(
            ConvenienceNfcService convenienceNfcService) {

        this.convenienceNfcService = convenienceNfcService;
    }

    /**
     * Resolve NFC card -> BookingPassenger -> Booking -> Tour.
     *
     * Dùng cho Convenience POS trước khi chọn
     * ProductTour hoặc ServiceTour.
     */
    @PostMapping("/resolve")
    @PreAuthorize("hasRole('CONVENIENCE')")
    public ResponseEntity<NfcResolveResponse> resolve(
            @Valid @RequestBody NfcResolveRequest request) {

        return ResponseEntity.ok(
                convenienceNfcService.resolve(request));
    }
}