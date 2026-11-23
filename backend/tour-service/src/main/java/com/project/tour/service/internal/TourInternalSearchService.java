
package com.project.tour.service.internal;

import com.project.tour.dto.internal.TourPriceSearchResponse;
import com.project.tour.model.enums.tour.TourStatusTrip;
import com.project.tour.repository.tour.TourPackageRepository;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
public class TourInternalSearchService {

    private final TourPackageRepository tourPackageRepository;

    public TourInternalSearchService(
            TourPackageRepository tourPackageRepository) {
        this.tourPackageRepository = tourPackageRepository;
    }

    /**
     * API cũ: Tìm tour READY có giá thấp nhất >= minPrice.
     */
    @Transactional(readOnly = true)
    public List<TourPriceSearchResponse> searchReadyToursByMinimumPrice(
            BigDecimal minPrice,
            int limit) {

        return tourPackageRepository.searchToursByStatusAndMinimumPrice(
                TourStatusTrip.READY,
                minPrice,
                PageRequest.of(0, limit));
    }

    /**
     * API mới: Tìm tour READY theo nhiều tiêu chí.
     *
     * Quy tắc thời gian:
     * - Chỉ có month: sử dụng năm hiện tại.
     * - Chỉ có year: tìm trong toàn bộ năm đó.
     * - Có cả month và year: tìm đúng tháng/năm.
     * - Không có month và year: không lọc thời gian.
     *
     * Thời gian được lọc dựa trên Tour.startDate.
     */
    @Transactional(readOnly = true)
    public List<TourPriceSearchResponse> searchReadyToursByCriteria(
            BigDecimal minPrice,
            BigDecimal maxPrice,
            String departurePoint,
            String destination,
            Integer month,
            Integer year,
            int limit) {

        validateSearchCriteria(minPrice, maxPrice, month, year, limit);

        LocalDate today = LocalDate.now();

        Integer effectiveYear = year;

        // Khách chỉ nói tháng thì mặc định năm hiện tại.
        if (month != null && effectiveYear == null) {
            effectiveYear = today.getYear();
        }

        LocalDate startDate = null;
        LocalDate endDate = null;

        // Có tháng: tìm trong tháng cụ thể.
        if (month != null) {
            YearMonth yearMonth = YearMonth.of(effectiveYear, month);

            startDate = yearMonth.atDay(1);
            endDate = yearMonth.atEndOfMonth();
        }
        // Chỉ có năm: tìm trong toàn bộ năm.
        else if (effectiveYear != null) {
            startDate = LocalDate.of(effectiveYear, 1, 1);
            endDate = LocalDate.of(effectiveYear, 12, 31);
        }

        String normalizedDeparture = normalizeText(departurePoint);
        String normalizedDestination = normalizeText(destination);

        return tourPackageRepository.searchToursByCriteria(
                TourStatusTrip.READY,
                minPrice,
                maxPrice,
                normalizedDeparture,
                normalizedDestination,
                startDate,
                endDate,
                PageRequest.of(0, limit));
    }

    private void validateSearchCriteria(
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Integer month,
            Integer year,
            int limit) {

        if (minPrice != null && minPrice.signum() < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Giá thấp nhất không được âm");
        }

        if (maxPrice != null && maxPrice.signum() < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Giá cao nhất không được âm");
        }

        if (minPrice != null
                && maxPrice != null
                && minPrice.compareTo(maxPrice) > 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Giá thấp nhất không được lớn hơn giá cao nhất");
        }

        if (month != null && (month < 1 || month > 12)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Tháng phải nằm trong khoảng từ 1 đến 12");
        }

        if (year != null && (year < 1900 || year > 9999)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Năm không hợp lệ");
        }

        if (limit < 1 || limit > 10) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Số lượng kết quả phải từ 1 đến 10");
        }
    }

    private String normalizeText(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }
}
