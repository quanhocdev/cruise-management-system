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

    @PostMapping("/{bookingId}/check-in-passenger")
    public ResponseEntity<?> checkInSinglePassenger(
            @PathVariable Long bookingId,
            @RequestBody PassengerCheckInRequest request) {

        System.out.println("========================================");
        System.out.println("[FINANCE] POST Check-in single passenger");
        System.out.println("[FINANCE] bookingId = " + bookingId);
        System.out.println("[FINANCE] passengerId = " + request.getPassengerId());
        System.out.println("[FINANCE] roomId = " + request.getRoomId());
        System.out.println("[FINANCE] nfcCode = " + request.getNfcCode());
        System.out.println("========================================");

        checkPassengerService.processSinglePassengerCheckIn(bookingId, request);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Check-in thành công cho hành khách!"));
    }
}