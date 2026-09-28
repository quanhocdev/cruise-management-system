package com.project.booking.controller.finance;

import com.project.booking.dto.finance.PassengerCheckInRequest;
import com.project.booking.service.finance.CheckPassengerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/finance/check-passenger")
public class CheckPassengerController {

    private final CheckPassengerService checkPassengerService;

    public CheckPassengerController(CheckPassengerService checkPassengerService) {
        this.checkPassengerService = checkPassengerService;
    }

    @GetMapping("/available-wristbands")
    public java.util.List<?> availableWristbands() {
        return checkPassengerService.getAvailableWristbands();
    }

    @PostMapping("/{bookingId}/check-in-passenger")
    public ResponseEntity<?> checkInSinglePassenger(
            @PathVariable Long bookingId,
            @RequestBody PassengerCheckInRequest request) {

        checkPassengerService.processSinglePassengerCheckIn(bookingId, request);
        return ResponseEntity.ok(Map.of("success", true, "message", "Check-in thành công cho hành khách!"));
    }
}
