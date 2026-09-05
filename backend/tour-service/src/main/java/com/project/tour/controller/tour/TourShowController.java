// package com.project.tour.controller.tour;

// import com.project.tour.dto.tour.TourResponse;
// import com.project.tour.service.tour.TourService;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// import java.util.List;

// @RestController
// @RequestMapping("/api/public/tours")
// public class TourShowController {

// private final TourService tourService;

// public TourShowController(TourService tourService) {
// this.tourService = tourService;
// }

// @GetMapping
// public ResponseEntity<List<TourResponse>> getTours() {
// return ResponseEntity.ok(
// tourService.getAllTours());
// }
// }