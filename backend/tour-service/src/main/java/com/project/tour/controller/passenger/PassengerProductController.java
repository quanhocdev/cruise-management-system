
package com.project.tour.controller.passenger;

import com.project.tour.dto.passenger.product.PassengerProductDTO;
import com.project.tour.service.passenger.PassengerProductService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/passenger/tours")
public class PassengerProductController {

    private final PassengerProductService service;

    public PassengerProductController(PassengerProductService service) {
        this.service = service;
    }

    @GetMapping("/{tourId}/products")
    public ResponseEntity<List<PassengerProductDTO>> getProducts(
            @PathVariable UUID tourId) {
        return ResponseEntity.ok(service.getProducts(tourId));
    }
}
