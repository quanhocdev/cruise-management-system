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
import com.project.tour.service.redis.TourRedisService;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.project.tour.repository.tour.TourRepository;
import com.project.tour.repository.room.RoomRepository;
import com.project.tour.repository.room.RoomTypeRepository;
import java.util.List;
import java.util.UUID;
import com.project.tour.model.enums.RoomStatus;

@Service
@Transactional
public class TourPackageService {

    private final TourPackageRepository tourPackageRepository;
    private final PackageBenefitRepository packageBenefitRepository;
    private final TourRepository tourRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final RoomRepository roomRepository;
    private final TourRedisService tourRedisService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public TourPackageService(
            TourPackageRepository tourPackageRepository,
            PackageBenefitRepository packageBenefitRepository,
            TourRepository tourRepository,
            RoomTypeRepository roomTypeRepository,
            RoomRepository roomRepository,
            TourRedisService tourRedisService,
            KafkaTemplate<String, Object> kafkaTemplate) {
        this.tourPackageRepository = tourPackageRepository;
        this.packageBenefitRepository = packageBenefitRepository;
        this.tourRepository = tourRepository;
        this.roomTypeRepository = roomTypeRepository;
        this.roomRepository = roomRepository;
        this.tourRedisService = tourRedisService;
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

        Tour tour = tourRepository.findById(request.tourId())
                .orElseThrow(() -> new AppException("Tour not found", HttpStatus.NOT_FOUND));

        if (tour.getCruise() == null) {
            throw new AppException("This tour does not have an assigned cruise yet", HttpStatus.BAD_REQUEST);
        }

        // Lấy RoomType để kiểm tra và lấy capacity
        RoomType roomType = roomTypeRepository.findById(request.roomTypeId())
                .orElseThrow(() -> new AppException("Room type not found", HttpStatus.NOT_FOUND));

        TourPackage tourPackage = new TourPackage();
        tourPackage.setTourId(request.tourId());
        tourPackage.setRoomTypeId(request.roomTypeId());
        tourPackage.setName(request.name());
        tourPackage.setDescription(request.description());
        tourPackage.setPrice(request.price());
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

        // =========================================================
        // ĐẾM VÀ LƯU SỐ LƯỢNG PHÒNG THỰC TẾ LÊN REDIS KHI TẠO GÓI THÀNH CÔNG
        // =========================================================
        UUID cruiseId = tour.getCruise().getId();
        int initialRooms = (int) roomRepository.countByCruiseDeck_CruiseIdAndRoomTypeIdAndStatus(
                cruiseId,
                request.roomTypeId(),
                RoomStatus.ACTIVE);

        tourRedisService.savePackageAvailableRooms(savedPackage.getId(), initialRooms);

        // 1. BẮN KAFKA EVENT (Dùng capacity của RoomType thay vì maxPassengers cũ)
        TourPackageSyncedEvent event = new TourPackageSyncedEvent(
                savedPackage.getId(),
                savedPackage.getTourId(),
                savedPackage.getName(),
                savedPackage.getPrice(),
                roomType.getCapacity(),
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
        if (request.status() != null) {
            tourPackage.setStatus(request.status());
        }

        TourPackage updatedPackage = tourPackageRepository.save(tourPackage);

        // Lấy RoomType hiện tại của package để lấy capacity bắn event
        RoomType roomType = roomTypeRepository.findById(updatedPackage.getRoomTypeId())
                .orElseThrow(() -> new AppException("Room type not found", HttpStatus.NOT_FOUND));

        // =========================================================
        // CẬP NHẬT LẠI SỐ LƯỢNG PHÒNG TRÊN REDIS NẾU THAY ĐỔI HẠNG PHÒNG
        // =========================================================
        if (request.roomTypeId() != null) {
            Tour tour = tourRepository.findById(updatedPackage.getTourId())
                    .orElseThrow(() -> new AppException("Tour not found", HttpStatus.NOT_FOUND));
            if (tour.getCruise() != null) {
                int updatedRooms = (int) roomRepository.countByCruiseDeck_CruiseIdAndRoomTypeIdAndStatus(
                        tour.getCruise().getId(),
                        updatedPackage.getRoomTypeId(),
                        RoomStatus.ACTIVE);
                tourRedisService.savePackageAvailableRooms(updatedPackage.getId(), updatedRooms);
            }
        }

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
                roomType.getCapacity(),
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

        RoomType roomType = roomTypeRepository.findById(pkg.getRoomTypeId()).orElse(null);
        int capacity = roomType != null && roomType.getCapacity() != null ? roomType.getCapacity() : 2;

        // 3. BẮN KAFKA EVENT BÁO XÓA TRƯỚC KHI THỰC HIỆN XÓA
        TourPackageSyncedEvent event = new TourPackageSyncedEvent(
                pkg.getId(),
                pkg.getTourId(),
                pkg.getName(),
                pkg.getPrice(),
                capacity,
                "DELETED");
        kafkaTemplate.send("tour-package-sync-topic", pkg.getId().toString(), event);

        // =========================================================
        // DỌN DẸP KEY REDIS TƯƠNG ỨNG KHI XÓA GÓI
        // =========================================================
        tourRedisService.deletePackageAvailableRooms(pkg.getId());

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