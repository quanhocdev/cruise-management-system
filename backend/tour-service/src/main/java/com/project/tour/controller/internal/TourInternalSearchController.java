
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
     * Tìm tour theo giá khởi điểm tối thiểu.
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
     * Tìm tour theo giá khởi điểm tối đa.
     */
    @GetMapping("/search-by-max-price")
    public List<TourPriceSearchResponse> searchReadyToursByMaximumPrice(
            @RequestParam @DecimalMin(value = "0.01", message = "Giá tìm kiếm phải lớn hơn 0") BigDecimal maxPrice,

            @RequestParam(defaultValue = "3") @Min(value = 1, message = "Limit tối thiểu là 1") @Max(value = 10, message = "Limit tối đa là 10") int limit) {

        return tourInternalSearchService.searchReadyToursByMaximumPrice(
                maxPrice,
                limit);
    }

    /**
     * Tìm tour theo khoảng giá khởi điểm.
     */
    @GetMapping("/search-by-price-range")
    public List<TourPriceSearchResponse> searchReadyToursByPriceRange(
            @RequestParam @DecimalMin(value = "0.01", message = "Giá thấp nhất phải lớn hơn 0") BigDecimal minPrice,

            @RequestParam @DecimalMin(value = "0.01", message = "Giá cao nhất phải lớn hơn 0") BigDecimal maxPrice,

            @RequestParam(defaultValue = "3") @Min(value = 1, message = "Limit tối thiểu là 1") @Max(value = 10, message = "Limit tối đa là 10") int limit) {

        return tourInternalSearchService.searchReadyToursByPriceRange(
                minPrice,
                maxPrice,
                limit);
    }
}
