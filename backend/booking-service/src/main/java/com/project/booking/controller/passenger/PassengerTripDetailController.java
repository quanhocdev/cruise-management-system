
package com.project.booking.controller.passenger;

import com.project.booking.dto.passenger.benefit.PassengerBenefitDTO;
import com.project.booking.dto.passenger.bill.PassengerBillDTO;
import com.project.booking.dto.passenger.booking.PassengerBookingDTO;
import com.project.booking.dto.passenger.passenger.PassengerBookingPassengerDTO;
import com.project.booking.dto.passenger.usage.PassengerActivityCruiseUsageDTO;
import com.project.booking.dto.passenger.usage.PassengerActivityVisitUsageDTO;
import com.project.booking.dto.passenger.usage.PassengerProductUsageDTO;
import com.project.booking.dto.passenger.usage.PassengerServiceUsageDTO;

import com.project.booking.service.passenger.PassengerTripDetailService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/passengers/bookings")
public class PassengerTripDetailController {

    private final PassengerTripDetailService passengerTripDetailService;

    public PassengerTripDetailController(
            PassengerTripDetailService passengerTripDetailService) {
        this.passengerTripDetailService = passengerTripDetailService;
    }

    private UUID extractUserId(Principal principal) {

        if (principal instanceof JwtAuthenticationToken jwtAuth) {
            Object userIdClaim = jwtAuth.getTokenAttributes().get("userId");

            if (userIdClaim != null) {
                try {
                    return UUID.fromString(userIdClaim.toString());
                } catch (IllegalArgumentException e) {
                    throw new RuntimeException(
                            "userId trong token không đúng định dạng UUID.");
                }
            }
        }

        try {
            return UUID.fromString(principal.getName());
        } catch (Exception e) {
            throw new RuntimeException(
                    "Không thể xác định userId dạng UUID từ token xác thực.");
        }
    }

    /**
     * GET /api/passengers/bookings/{bookingId}/trip-detail
     */
    @GetMapping("/{bookingId}/trip-detail")
    public ResponseEntity<PassengerBookingDTO> getBookingDetail(
            @PathVariable Long bookingId,
            Principal principal) {

        UUID userId = extractUserId(principal);

        return ResponseEntity.ok(
                passengerTripDetailService.getBookingDetail(
                        bookingId,
                        userId));
    }

    /**
     * GET /api/passengers/bookings/{bookingId}/passengers
     */
    @GetMapping("/{bookingId}/passengers")
    public ResponseEntity<List<PassengerBookingPassengerDTO>> getPassengers(
            @PathVariable Long bookingId,
            Principal principal) {

        UUID userId = extractUserId(principal);

        return ResponseEntity.ok(
                passengerTripDetailService.getPassengers(
                        bookingId,
                        userId));
    }

    /**
     * GET /api/passengers/bookings/{bookingId}/usages/products
     */
    @GetMapping("/{bookingId}/usages/products")
    public ResponseEntity<List<PassengerProductUsageDTO>> getProductUsages(
            @PathVariable Long bookingId,
            Principal principal) {

        UUID userId = extractUserId(principal);

        return ResponseEntity.ok(
                passengerTripDetailService.getProductUsages(
                        bookingId,
                        userId));
    }

    /**
     * GET /api/passengers/bookings/{bookingId}/usages/services
     */
    @GetMapping("/{bookingId}/usages/services")
    public ResponseEntity<List<PassengerServiceUsageDTO>> getServiceUsages(
            @PathVariable Long bookingId,
            Principal principal) {

        UUID userId = extractUserId(principal);

        return ResponseEntity.ok(
                passengerTripDetailService.getServiceUsages(
                        bookingId,
                        userId));
    }

    /**
     * GET /api/passengers/bookings/{bookingId}/usages/activities-cruise
     */
    @GetMapping("/{bookingId}/usages/activities-cruise")
    public ResponseEntity<List<PassengerActivityCruiseUsageDTO>> getActivityCruiseUsages(
            @PathVariable Long bookingId,
            Principal principal) {

        UUID userId = extractUserId(principal);

        return ResponseEntity.ok(
                passengerTripDetailService.getActivityCruiseUsages(
                        bookingId,
                        userId));
    }

    /**
     * GET /api/passengers/bookings/{bookingId}/usages/visits
     */
    @GetMapping("/{bookingId}/usages/visits")
    public ResponseEntity<List<PassengerActivityVisitUsageDTO>> getActivityVisitUsages(
            @PathVariable Long bookingId,
            Principal principal) {

        UUID userId = extractUserId(principal);

        return ResponseEntity.ok(
                passengerTripDetailService.getActivityVisitUsages(
                        bookingId,
                        userId));
    }

    /**
     * GET /api/passengers/bookings/{bookingId}/benefits
     */
    @GetMapping("/{bookingId}/benefits")
    public ResponseEntity<PassengerBenefitDTO> getBenefits(
            @PathVariable Long bookingId,
            Principal principal) {

        UUID userId = extractUserId(principal);

        return ResponseEntity.ok(
                passengerTripDetailService.getBenefits(
                        bookingId,
                        userId));
    }

    /**
     * GET /api/passengers/bookings/{bookingId}/bills
     */
    @GetMapping("/{bookingId}/bills")
    public ResponseEntity<List<PassengerBillDTO>> getBills(
            @PathVariable Long bookingId,
            Principal principal) {

        UUID userId = extractUserId(principal);

        return ResponseEntity.ok(
                passengerTripDetailService.getBills(
                        bookingId,
                        userId));
    }
}
