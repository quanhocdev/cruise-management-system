
package com.project.tour.controller.internal;

import com.project.tour.dto.internal.TourPriceSearchResponse;
import com.project.tour.service.internal.TourInternalSearchService;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/internal/tours")
@Validated
public class TourInternalSearchController {

    private final TourInternalSearchService tourInternalSearchService;

    public TourInternalSearchController(
            TourInternalSearchService tourInternalSearchService) {
        this.tourInternalSearchService = tourInternalSearchService;
    }

    /**
     * API cũ: Tìm tour READY theo giá tối thiểu.
     * Giữ lại để không ảnh hưởng Chat Service hiện tại.
     */
    @GetMapping("/search-by-min-price")
    public List<TourPriceSearchResponse> searchReadyToursByMinimumPrice(
            @RequestParam @DecimalMin(value = "0.01", message = "Giá tìm kiếm phải lớn hơn 0") BigDecimal minPrice,

            @RequestParam(defaultValue = "3") @Min(value = 1, message = "Limit tối thiểu là 1") @Max(value = 10, message = "Limit tối đa là 10") int limit) {

        return tourInternalSearchService.searchReadyToursByMinimumPrice(
                minPrice,
                limit);
    }

    /**
     * API mới: Tìm tour theo nhiều tiêu chí.
     */
    @GetMapping("/search")
    public List<TourPriceSearchResponse> searchReadyToursByCriteria(

            @RequestParam(required = false) @DecimalMin(value = "0.01", message = "Giá thấp nhất phải lớn hơn 0") BigDecimal minPrice,

            @RequestParam(required = false) @DecimalMin(value = "0.01", message = "Giá cao nhất phải lớn hơn 0") BigDecimal maxPrice,

            @RequestParam(required = false) String departurePoint,

            @RequestParam(required = false) String destination,

            @RequestParam(required = false) @Min(1) @Max(12) Integer month,

            @RequestParam(required = false) @Min(1900) @Max(9999) Integer year,

            @RequestParam(defaultValue = "3") @Min(1) @Max(10) int limit) {

        return tourInternalSearchService.searchReadyToursByCriteria(
                minPrice,
                maxPrice,
                departurePoint,
                destination,
                month,
                year,
                limit);
    }
}
