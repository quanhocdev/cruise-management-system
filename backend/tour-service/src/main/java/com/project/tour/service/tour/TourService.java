package com.project.tour.service.tour;

import com.project.tour.dto.tour.CreateTourRequest;
import com.project.tour.dto.tour.TourResponse;
import com.project.tour.dto.tour.UpdateTourRequest;
import com.project.tour.exception.AppException;
import com.project.tour.mapper.tour.TourMapper;
import com.project.tour.model.Cruise;
import com.project.tour.model.Tour;
import com.project.tour.repository.cruise.CruiseRepository;
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
    private final CruiseRepository cruiseRepository;

    public TourService(
            TourRepository tourRepository,
            CruiseRepository cruiseRepository) {

        this.tourRepository = tourRepository;
        this.cruiseRepository = cruiseRepository;
    }

    public TourResponse createTour(
            CreateTourRequest request) {

        if (request.getDayEnd() < request.getDayStart()) {
            throw new AppException(
                    "Day end must be greater than or equal to day start",
                    HttpStatus.BAD_REQUEST);
        }

        if (tourRepository.existsByCodeIgnoreCase(
                request.getCode())) {

            throw new AppException(
                    "Tour code already exists",
                    HttpStatus.CONFLICT);
        }

        Cruise cruise = findCruise(request.getCruiseId());

        Tour tour = TourMapper.toEntity(
                request,
                cruise);

        Tour savedTour = tourRepository.save(tour);

        return TourMapper.toResponse(savedTour);
    }

    @Transactional(readOnly = true)
    public TourResponse getTourById(
            UUID id) {

        Tour tour = findById(id);

        return TourMapper.toResponse(tour);
    }

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

    @Transactional(readOnly = true)
    public List<TourResponse> getAllTours() {

        return tourRepository
                .findAllByOrderByNameAsc()
                .stream()
                .map(TourMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TourResponse> getToursByCruise(
            UUID cruiseId) {

        if (!cruiseRepository.existsById(cruiseId)) {
            throw new AppException(
                    "Cruise not found",
                    HttpStatus.NOT_FOUND);
        }

        return tourRepository
                .findAllByCruise_IdOrderByNameAsc(cruiseId)
                .stream()
                .map(TourMapper::toResponse)
                .toList();
    }

    public TourResponse updateTour(
            UUID id,
            UpdateTourRequest request) {

        if (request.getDayEnd() < request.getDayStart()) {
            throw new AppException(
                    "Day end must be greater than or equal to day start",
                    HttpStatus.BAD_REQUEST);
        }

        Tour tour = findById(id);

        if (tourRepository.existsByCodeIgnoreCaseAndIdNot(
                request.getCode(),
                id)) {

            throw new AppException(
                    "Tour code already exists",
                    HttpStatus.CONFLICT);
        }

        Cruise cruise = findCruise(request.getCruiseId());

        TourMapper.updateEntity(
                tour,
                request,
                cruise);

        Tour updatedTour = tourRepository.save(tour);

        return TourMapper.toResponse(updatedTour);
    }

    public void deleteTour(
            UUID id) {

        Tour tour = findById(id);

        tourRepository.delete(tour);
    }

    private Tour findById(
            UUID id) {

        return tourRepository
                .findById(id)
                .orElseThrow(() -> new AppException(
                        "Tour not found",
                        HttpStatus.NOT_FOUND));
    }

    private Cruise findCruise(
            UUID cruiseId) {

        return cruiseRepository
                .findById(cruiseId)
                .orElseThrow(() -> new AppException(
                        "Cruise not found",
                        HttpStatus.NOT_FOUND));
    }
}