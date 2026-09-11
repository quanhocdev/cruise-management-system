package com.project.tour.service.tour;

import com.project.common.event.TourPackageSyncedEvent;
import com.project.tour.dto.tour.packages.TourPackageRequest;
import com.project.tour.dto.tour.packages.TourPackageResponse;
import com.project.tour.exception.AppException;
import com.project.tour.mapper.tour.TourPackageMapper;
import com.project.tour.model.PackageBenefit;
import com.project.tour.model.TourPackage;
import com.project.tour.model.Tour;
import com.project.tour.model.RoomType;
import com.project.tour.dto.roomtype.RoomTypeResponse;
import com.project.tour.repository.tour.PackageBenefitRepository;
import com.project.tour.repository.tour.TourPackageRepository;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.project.tour.repository.tour.TourRepository;
import com.project.tour.repository.room.RoomTypeRepository;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class TourPackageService {

    private final TourPackageRepository tourPackageRepository;
    private final PackageBenefitRepository packageBenefitRepository;
    private final TourRepository tourRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public TourPackageService(
            TourPackageRepository tourPackageRepository,
            PackageBenefitRepository packageBenefitRepository,
            TourRepository tourRepository,
            RoomTypeRepository roomTypeRepository,
            KafkaTemplate<String, Object> kafkaTemplate) {
        this.tourPackageRepository = tourPackageRepository;
        this.packageBenefitRepository = packageBenefitRepository;
        this.tourRepository = tourRepository;
        this.roomTypeRepository = roomTypeRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    // =========================================================
    // TẠO MỚI GÓI TOUR
    // =========================================================
    public TourPackageResponse createPackage(TourPackageRequest request) {
        boolean exists = tourPackageRepository.existsByTourIdAndName(request.tourId(), request.name());
        if (exists) {
            throw new AppException("A package with this name already exists for this tour", HttpStatus.BAD_REQUEST);
        }

        TourPackage tourPackage = new TourPackage();
        tourPackage.setTourId(request.tourId());
        tourPackage.setRoomTypeId(request.roomTypeId());
        tourPackage.setName(request.name());
        tourPackage.setDescription(request.description());
        tourPackage.setPrice(request.price());
        tourPackage.setMaxPassengers(request.maxPassengers());
        tourPackage.setStatus(request.status());

        TourPackage savedPackage = tourPackageRepository.save(tourPackage);

        List<PackageBenefit> savedBenefits = List.of();
        if (request.benefits() != null && !request.benefits().isEmpty()) {
            List<PackageBenefit> benefits = request.benefits().stream().map(dto -> {
                PackageBenefit benefit = new PackageBenefit();
                benefit.setTourPackageId(savedPackage.getId());
                benefit.setType(dto.type());
                benefit.setReferenceId(dto.referenceId());
                benefit.setQuantity(dto.quantity());
                benefit.setDiscountPercent(dto.discountPercent());
                return benefit;
            }).toList();

            savedBenefits = packageBenefitRepository.saveAll(benefits);
        }

        // 1. BẮN KAFKA EVENT KHI TẠO MỚI THÀNH CÔNG
        TourPackageSyncedEvent event = new TourPackageSyncedEvent(
                savedPackage.getId(),
                savedPackage.getTourId(),
                savedPackage.getName(),
                savedPackage.getPrice(),
                savedPackage.getMaxPassengers(),
                savedPackage.getStatus().name());
        kafkaTemplate.send("tour-package-sync-topic", savedPackage.getId().toString(), event);

        return TourPackageMapper.toResponse(savedPackage, savedBenefits);
    }

    // =========================================================
    // CẬP NHẬT GÓI TOUR (PATCH)
    // =========================================================
    public TourPackageResponse patchPackage(UUID packageId, TourPackageRequest request) {
        TourPackage tourPackage = tourPackageRepository.findById(packageId)
                .orElseThrow(() -> new AppException("Tour package not found", HttpStatus.NOT_FOUND));

        if (request.roomTypeId() != null) {
            tourPackage.setRoomTypeId(request.roomTypeId());
        }
        if (request.name() != null) {
            boolean exists = tourPackageRepository.existsByTourIdAndName(tourPackage.getTourId(), request.name());
            if (exists && !tourPackage.getName().equals(request.name())) {
                throw new AppException("A package with this name already exists for this tour", HttpStatus.BAD_REQUEST);
            }
            tourPackage.setName(request.name());
        }
        if (request.description() != null) {
            tourPackage.setDescription(request.description());
        }
        if (request.price() != null) {
            tourPackage.setPrice(request.price());
        }
        if (request.maxPassengers() != null) {
            tourPackage.setMaxPassengers(request.maxPassengers());
        }
        if (request.status() != null) {
            tourPackage.setStatus(request.status());
        }

        TourPackage updatedPackage = tourPackageRepository.save(tourPackage);

        List<PackageBenefit> savedBenefits = packageBenefitRepository.findAllByTourPackageId(updatedPackage.getId());
        if (request.benefits() != null) {
            packageBenefitRepository.deleteAllByTourPackageId(updatedPackage.getId());

            List<PackageBenefit> newBenefits = request.benefits().stream().map(dto -> {
                PackageBenefit benefit = new PackageBenefit();
                benefit.setTourPackageId(updatedPackage.getId());
                benefit.setType(dto.type());
                benefit.setReferenceId(dto.referenceId());
                benefit.setQuantity(dto.quantity());
                benefit.setDiscountPercent(dto.discountPercent());
                return benefit;
            }).toList();

            savedBenefits = packageBenefitRepository.saveAll(newBenefits);
        }

        // 2. BẮN KAFKA EVENT KHI CẬP NHẬT THÀNH CÔNG
        TourPackageSyncedEvent event = new TourPackageSyncedEvent(
                updatedPackage.getId(),
                updatedPackage.getTourId(),
                updatedPackage.getName(),
                updatedPackage.getPrice(),
                updatedPackage.getMaxPassengers(),
                updatedPackage.getStatus().name());
        kafkaTemplate.send("tour-package-sync-topic", updatedPackage.getId().toString(), event);

        return TourPackageMapper.toResponse(updatedPackage, savedBenefits);
    }

    // =========================================================
    // LẤY DANH SÁCH GÓI TOUR THEO TOUR ID
    // =========================================================
    @Transactional(readOnly = true)
    public List<TourPackageResponse> getPackagesByTourId(UUID tourId) {
        List<TourPackage> packages = tourPackageRepository.findAllByTourId(tourId);

        return packages.stream().map(pkg -> {
            List<PackageBenefit> benefits = packageBenefitRepository.findAllByTourPackageId(pkg.getId());
            return TourPackageMapper.toResponse(pkg, benefits);
        }).toList();
    }

    // =========================================================
    // XÓA GÓI TOUR
    // =========================================================
    public void deletePackage(UUID packageId) {
        TourPackage pkg = tourPackageRepository.findById(packageId)
                .orElseThrow(() -> new AppException("Tour package not found", HttpStatus.NOT_FOUND));

        // 3. BẮN KAFKA EVENT BÁO XÓA TRƯỚC KHI THỰC HIỆN XÓA
        TourPackageSyncedEvent event = new TourPackageSyncedEvent(
                pkg.getId(),
                pkg.getTourId(),
                pkg.getName(),
                pkg.getPrice(),
                pkg.getMaxPassengers(),
                "DELETED");
        kafkaTemplate.send("tour-package-sync-topic", pkg.getId().toString(), event);

        packageBenefitRepository.deleteAllByTourPackageId(pkg.getId());
        tourPackageRepository.delete(pkg);
    }

    @Transactional(readOnly = true)
    public List<RoomTypeResponse> getRoomTypesByTourId(UUID tourId) {
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new AppException("Tour not found", HttpStatus.NOT_FOUND));

        if (tour.getCruise() == null) {
            throw new AppException("This tour does not have an assigned cruise yet", HttpStatus.BAD_REQUEST);
        }

        UUID cruiseId = tour.getCruise().getId();
        List<RoomType> roomTypes = roomTypeRepository.findRoomTypesByCruiseId(cruiseId);

        return roomTypes.stream().map(rt -> {
            com.project.tour.dto.roomtype.RoomTypeResponse dto = new com.project.tour.dto.roomtype.RoomTypeResponse();
            dto.setId(rt.getId());
            dto.setName(rt.getName());
            dto.setDescription(rt.getDescription());
            return dto;
        }).toList();
    }
}