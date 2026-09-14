package com.project.booking.controller;

import com.project.booking.dto.checkin.CheckInLookupResponse;
import com.project.booking.dto.checkin.ConfirmCheckInRequest;
import com.project.booking.service.CheckInService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/finance/pos/check-in")
public class PosCheckInController {

    private final CheckInService checkInService;

    public PosCheckInController(CheckInService checkInService) {
        this.checkInService = checkInService;
    }

    @GetMapping("/lookup")
    public ResponseEntity<CheckInLookupResponse> lookupBooking(@RequestParam String code) {
        return ResponseEntity.ok(checkInService.lookupByCode(code));
    }

    @PostMapping("/confirm")
    public ResponseEntity<?> confirmCheckIn(@Valid @RequestBody ConfirmCheckInRequest request) {
        checkInService.confirmCheckIn(request);
        return ResponseEntity.ok(Map.of("message", "Check-in và gán phòng thành công"));
    }
}