
package com.project.tour.service.passenger;

import com.project.tour.dto.passenger.tourpackage.PassengerTourPackageDTO;
import com.project.tour.mapper.passenger.TourPackageMapper;
import com.project.tour.model.operation.PackageBenefit;
import com.project.tour.model.operation.TourPackage;
import com.project.tour.repository.scheduler.TourRepository;
import com.project.tour.repository.tour.PackageBenefitRepository;
import com.project.tour.repository.tour.TourPackageRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class PassengerTourPackageService {

    private final TourRepository tourRepository;
    private final TourPackageRepository tourPackageRepository;
    private final PackageBenefitRepository packageBenefitRepository;
    private final TourPackageMapper tourPackageMapper;

    public PassengerTourPackageService(
            TourRepository tourRepository,
            TourPackageRepository tourPackageRepository,
            PackageBenefitRepository packageBenefitRepository,
            TourPackageMapper tourPackageMapper) {
        this.tourRepository = tourRepository;
        this.tourPackageRepository = tourPackageRepository;
        this.packageBenefitRepository = packageBenefitRepository;
        this.tourPackageMapper = tourPackageMapper;
    }

    public List<PassengerTourPackageDTO> getPackages(UUID tourId) {

        if (!tourRepository.existsById(tourId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Không tìm thấy tour");
        }

        List<TourPackage> packages = tourPackageRepository.findAllByTourIdWithRoomType(tourId);

        return packages.stream()
                .map(tourPackage -> {
                    List<PackageBenefit> benefits = packageBenefitRepository.findAllByTourPackageId(
                            tourPackage.getId());

                    return tourPackageMapper.toDTO(
                            tourPackage,
                            benefits,
                            Map.<UUID, String>of());
                })
                .toList();
    }
}
