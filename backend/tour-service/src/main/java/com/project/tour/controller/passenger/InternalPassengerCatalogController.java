package com.project.tour.controller.passenger;

import com.project.tour.dto.passenger.PassengerRoomCatalogResponse;
import com.project.tour.dto.passenger.PassengerVoyageBookingContext;
import com.project.tour.service.passenger.PassengerCatalogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/internal/voyages")
public class InternalPassengerCatalogController {
    private final PassengerCatalogService service;

    public InternalPassengerCatalogController(PassengerCatalogService service) {
        this.service = service;
    }

    @GetMapping("/{voyageId}/booking-context")
    public ResponseEntity<PassengerVoyageBookingContext> getBookingContext(@PathVariable UUID voyageId) {
        return ResponseEntity.ok(service.getBookingContext(voyageId));
    }

    @GetMapping("/{voyageId}/rooms")
    public ResponseEntity<List<PassengerRoomCatalogResponse>> getRooms(@PathVariable UUID voyageId) {
        return ResponseEntity.ok(service.getRoomCatalog(voyageId));
    }
}
