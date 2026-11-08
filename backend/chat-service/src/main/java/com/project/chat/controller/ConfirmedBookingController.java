package com.project.chat.controller;

import com.project.chat.dto.ConfirmedBookingResponse;
import com.project.chat.service.ConfirmedBookingService;
import com.project.chat.security.JwtUserUtil;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/message/bookings")
public class ConfirmedBookingController {

    private final ConfirmedBookingService confirmedBookingService;

    public ConfirmedBookingController(
            ConfirmedBookingService confirmedBookingService) {
        this.confirmedBookingService = confirmedBookingService;
    }

    @GetMapping
    public ResponseEntity<List<ConfirmedBookingResponse>> getMyConfirmedBookings(
            Principal principal) {
        UUID userId = JwtUserUtil.extractUserId(principal);

        return ResponseEntity.ok(
                confirmedBookingService.getMyConfirmedBookings(userId));
    }
}