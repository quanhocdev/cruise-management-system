package com.project.tour.service.tour;

import com.project.tour.dto.tour.CreateTourRequest;
import com.project.tour.dto.tour.TourResponse;
import com.project.tour.dto.tour.UpdateTourRequest;
import com.project.tour.exception.AppException;
import com.project.tour.mapper.tour.TourMapper;
import com.project.tour.model.Tour;
import com.project.tour.repository.tour.TourRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class TourService {

        private final TourRepository tourRepository;

        public TourService(
                        TourRepository tourRepository) {

                this.tourRepository = tourRepository;
        }

        // =====================================================
        // CREATE
        // =====================================================

        public TourResponse createTour(
                        CreateTourRequest request) {

                validateDays(
                                request.dayStart(),
                                request.dayEnd());

                validateCodeNotExists(
                                request.code());

                Tour tour = TourMapper.toEntity(request);

                Tour savedTour = tourRepository.save(tour);

                return TourMapper.toResponse(savedTour);
        }

        // =====================================================
        // GET BY ID
        // =====================================================

        @Transactional(readOnly = true)
        public TourResponse getTourById(
                        UUID id) {

                Tour tour = findById(id);

                return TourMapper.toResponse(tour);
        }

        // =====================================================
        // GET BY CODE
        // =====================================================

        @Transactional(readOnly = true)
        public TourResponse getTourByCode(
                        String code) {

                Tour tour = tourRepository
                                .findByCodeIgnoreCase(code)
                                .orElseThrow(() -> new AppException(
                                                "Tour not found",
                                                HttpStatus.NOT_FOUND));

                return TourMapper.toResponse(tour);
        }

        // =====================================================
        // GET ALL
        // =====================================================

        @Transactional(readOnly = true)
        public List<TourResponse> getAllTours() {

                return tourRepository
                                .findAllByOrderByNameAsc()
                                .stream()
                                .map(TourMapper::toResponse)
                                .toList();
        }

        // =====================================================
        // GET BY CRUISE
        // =====================================================

        @Transactional(readOnly = true)
        public List<TourResponse> getToursByCruise(
                        UUID cruiseId) {

                return tourRepository
                                .findAllByCruise_IdOrderByNameAsc(cruiseId)
                                .stream()
                                .map(TourMapper::toResponse)
                                .toList();
        }

        // =====================================================
        // UPDATE
        // =====================================================

        public TourResponse updateTour(
                        UUID id,
                        UpdateTourRequest request) {

                validateDays(
                                request.dayStart(),
                                request.dayEnd());

                Tour tour = findById(id);

                if (tourRepository.existsByCodeIgnoreCaseAndIdNot(
                                request.code(),
                                id)) {

                        throw new AppException(
                                        "Tour code already exists",
                                        HttpStatus.CONFLICT);
                }

                TourMapper.updateEntity(
                                tour,
                                request);

                Tour updatedTour = tourRepository.save(tour);

                return TourMapper.toResponse(updatedTour);
        }

        // =====================================================
        // DELETE
        // =====================================================

        public void deleteTour(
                        UUID id) {

                Tour tour = findById(id);

                tourRepository.delete(tour);
        }

        // =====================================================
        // FIND
        // =====================================================

        private Tour findById(
                        UUID id) {

                return tourRepository
                                .findById(id)
                                .orElseThrow(() -> new AppException(
                                                "Tour not found",
                                                HttpStatus.NOT_FOUND));
        }

        // =====================================================
        // VALIDATION
        // =====================================================

        private void validateDays(
                        Integer dayStart,
                        Integer dayEnd) {

                if (dayEnd < dayStart) {

                        throw new AppException(
                                        "Day end must be greater than or equal to day start",
                                        HttpStatus.BAD_REQUEST);
                }
        }

        private void validateCodeNotExists(
                        String code) {

                if (tourRepository.existsByCodeIgnoreCase(code)) {

                        throw new AppException(
                                        "Tour code already exists",
                                        HttpStatus.CONFLICT);
                }
        }
}