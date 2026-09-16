package com.project.tour.controller;

import com.project.tour.dto.NfcCardRequest;
import com.project.tour.dto.NfcCardResponse;
import com.project.tour.service.NfcCardService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/nfc-cards")
public class NfcCardController {

    private final NfcCardService nfcCardService;

    public NfcCardController(NfcCardService nfcCardService) {
        this.nfcCardService = nfcCardService;
    }

    @PostMapping
    public ResponseEntity<NfcCardResponse> createCard(@RequestBody NfcCardRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(nfcCardService.createCard(request));
    }

    @GetMapping
    public ResponseEntity<List<NfcCardResponse>> getAllCards() {
        return ResponseEntity.ok(nfcCardService.getAllCards());
    }

    @GetMapping("/{id}")
    public ResponseEntity<NfcCardResponse> getCardById(@PathVariable UUID id) {
        return ResponseEntity.ok(nfcCardService.getCardById(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<NfcCardResponse> updateCard(@PathVariable UUID id, @RequestBody NfcCardRequest request) {
        return ResponseEntity.ok(nfcCardService.updateCard(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCard(@PathVariable UUID id) {
        nfcCardService.deleteCard(id);
        return ResponseEntity.noContent().build();
    }
}