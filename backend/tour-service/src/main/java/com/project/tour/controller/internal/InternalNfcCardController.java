
package com.project.tour.controller.internal;

import com.project.tour.dto.admin.nfc.NfcCardResponse;
import com.project.tour.service.NfcCardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/nfc-cards")
public class InternalNfcCardController {

    private final NfcCardService nfcCardService;

    public InternalNfcCardController(NfcCardService nfcCardService) {
        this.nfcCardService = nfcCardService;
    }

    @PutMapping("/{cardUid}/assign")
    public ResponseEntity<NfcCardResponse> assignCard(
            @PathVariable String cardUid) {
        return ResponseEntity.ok(nfcCardService.assignCard(cardUid));
    }
}
