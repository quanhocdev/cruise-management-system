package com.project.booking.controller.finance;

import com.project.booking.service.command.IdempotentCommandService;
import com.project.booking.service.finance.checkout.PassengerDepartureService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/finance/bookings/{bookingId}/departures")
public class PassengerDepartureController {
    private final PassengerDepartureService service;
    private final IdempotentCommandService commands;
    public PassengerDepartureController(PassengerDepartureService service, IdempotentCommandService commands) {
        this.service=service; this.commands=commands;
    }
    public record Request(@NotNull UUID clientRequestId, @AssertTrue boolean roomReturned,
                          @AssertTrue boolean wristbandReturned, @NotBlank @Size(max=500) String reason) {}
    @GetMapping
    public PassengerDepartureService.Manifest list(@PathVariable long bookingId) { return service.list(bookingId); }
    @PostMapping("/{passengerId}")
    public ResponseEntity<PassengerDepartureService.Departure> confirm(@PathVariable long bookingId,
            @PathVariable long passengerId, @Valid @RequestBody Request request, @AuthenticationPrincipal Jwt jwt) {
        long actor;
        try { actor=Long.parseLong(String.valueOf(jwt.getClaimAsString("userId"))); }
        catch (RuntimeException e) { throw new ResponseStatusException(HttpStatus.UNAUTHORIZED); }
        return commands.execute("PASSENGER_DEPARTURE",request.clientRequestId(),
            Map.of("bookingId",bookingId,"passengerId",passengerId,"roomReturned",request.roomReturned(),
                "wristbandReturned",request.wristbandReturned(),"reason",request.reason()),
            PassengerDepartureService.Departure.class,
            () -> service.confirm(bookingId,passengerId,actor,request.roomReturned(),request.wristbandReturned(),request.reason()));
    }
}
