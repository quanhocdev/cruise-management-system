package com.project.tour.service.operation;

import com.project.common.event.TourPackageSyncedEvent;
import com.project.tour.dto.admin.roomtype.RoomTypeResponse;
import com.project.tour.dto.internal.BookingPackageInfo;
import com.project.tour.dto.operation.packages.TourPackageRequest;
import com.project.tour.dto.operation.packages.TourPackageResponse;
import com.project.tour.mapper.operation.PackageBenefitMapper;
import com.project.tour.mapper.operation.RoomTypeMapper;
import com.project.tour.mapper.operation.TourPackageMapper;
import com.project.tour.model.admin.RoomType;
import com.project.tour.model.operation.PackageBenefit;
import com.project.tour.model.operation.TourPackage;
import com.project.tour.model.scheduler.Tour;
import com.project.tour.repository.room.RoomTypeRepository;
import com.project.tour.repository.tour.PackageBenefitRepository;
import com.project.tour.repository.tour.TourPackageRepository;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
@Transactional
public class TourPackageServiceImpl implements TourPackageService {

    private static final String DELETED_STATUS = "DELETED";

    private final TourPackageRepository tourPackageRepository;
    private final PackageBenefitRepository packageBenefitRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final TourPackageValidator tourPackageValidator;
    private final PackageRoomAvailabilityService packageRoomAvailabilityService;
    private final ApplicationEventPublisher eventPublisher;

    public TourPackageServiceImpl(
            TourPackageRepository tourPackageRepository,
            PackageBenefitRepository packageBenefitRepository,
            RoomTypeRepository roomTypeRepository,
            TourPackageValidator tourPackageValidator,
            PackageRoomAvailabilityService packageRoomAvailabilityService,
            ApplicationEventPublisher eventPublisher) {
        this.tourPackageRepository = tourPackageRepository;
        this.packageBenefitRepository = packageBenefitRepository;
        this.roomTypeRepository = roomTypeRepository;
        this.tourPackageValidator = tourPackageValidator;
        this.packageRoomAvailabilityService = packageRoomAvailabilityService;
        this.eventPublisher = eventPublisher;
    }

    // =========================================================
    // TẠO MỚI GÓI TOUR
    // =========================================================
    @Override
    public TourPackageResponse createPackage(TourPackageRequest request) {

        tourPackageValidator.validateUniqueName(request.tourId(), request.name(), null);
        Tour tour = tourPackageValidator.getTourWithCruise(request.tourId());
        RoomType roomType = tourPackageValidator.getRoomTypeOrThrow(request.roomTypeId());

        TourPackage tourPackage = new TourPackage();
        tourPackage.setTourId(request.tourId());
        tourPackage.setRoomTypeId(request.roomTypeId());
        tourPackage.setName(request.name());
        tourPackage.setDescription(request.description());
        tourPackage.setPrice(request.price());
        tourPackage.setStatus(request.status());

        TourPackage savedPackage = tourPackageRepository.save(tourPackage);
        List<PackageBenefit> savedBenefits = saveBenefits(savedPackage.getId(), request);

        int initialRooms = packageRoomAvailabilityService.countActiveRooms(
                tour.getCruise().getId(),
                request.roomTypeId());

        // Redis + Kafka chỉ chạy sau khi DB commit (xem TourPackageEventListener)
        eventPublisher.publishEvent(TourPackageChangedEvent.upserted(
                savedPackage.getId(),
                toSyncedEvent(savedPackage, roomType.getCapacity(), savedPackage.getStatus().name()),
                initialRooms));

        return TourPackageMapper.toResponse(savedPackage, savedBenefits, roomType.getCapacity());
    }

    // =========================================================
    // CẬP NHẬT GÓI TOUR (PATCH)
    // =========================================================
    @Override
    public TourPackageResponse patchPackage(UUID packageId, TourPackageRequest request) {

        TourPackage tourPackage = tourPackageValidator.getPackageOrThrow(packageId);

        if (request.roomTypeId() != null) {
            tourPackage.setRoomTypeId(request.roomTypeId());
        }
        if (request.name() != null) {
            tourPackageValidator.validateUniqueName(
                    tourPackage.getTourId(), request.name(), tourPackage.getName());
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

        RoomType roomType = tourPackageValidator.getRoomTypeOrThrow(updatedPackage.getRoomTypeId());

        // Chỉ đếm lại số phòng khi đổi hạng phòng
        Integer availableRooms = null;
        if (request.roomTypeId() != null) {
            Tour tour = tourPackageValidator.getTourOrThrow(updatedPackage.getTourId());
            if (tour.getCruise() != null) {
                availableRooms = packageRoomAvailabilityService.countActiveRooms(
                        tour.getCruise().getId(),
                        updatedPackage.getRoomTypeId());
            }
        }

        List<PackageBenefit> savedBenefits = packageBenefitRepository
                .findAllByTourPackageId(updatedPackage.getId());

        if (request.benefits() != null) {
            packageBenefitRepository.deleteAllByTourPackageId(updatedPackage.getId());
            savedBenefits = saveBenefits(updatedPackage.getId(), request);
        }

        eventPublisher.publishEvent(TourPackageChangedEvent.upserted(
                updatedPackage.getId(),
                toSyncedEvent(updatedPackage, roomType.getCapacity(), updatedPackage.getStatus().name()),
                availableRooms));

        return TourPackageMapper.toResponse(updatedPackage, savedBenefits, roomType.getCapacity());
    }

    // =========================================================
    // XÓA GÓI TOUR
    // =========================================================
    @Override
    public void deletePackage(UUID packageId) {

        TourPackage pkg = tourPackageValidator.getPackageOrThrow(packageId);

        // Capacity lấy đúng từ DB (giống create/patch), không có giá trị mặc định
        RoomType roomType = tourPackageValidator.getRoomTypeOrThrow(pkg.getRoomTypeId());
        Integer capacity = roomType.getCapacity();

        packageBenefitRepository.deleteAllByTourPackageId(pkg.getId());
        tourPackageRepository.delete(pkg);

        // Event DELETED + xóa key Redis chỉ chạy sau khi DB xóa thành công
        eventPublisher.publishEvent(TourPackageChangedEvent.deleted(
                pkg.getId(),
                toSyncedEvent(pkg, capacity, DELETED_STATUS)));
    }

    // =========================================================
    // ĐỌC DỮ LIỆU
    // =========================================================
    @Override
    @Transactional(readOnly = true)
    public List<TourPackageResponse> getPackagesByTourId(UUID tourId) {
        List<TourPackage> packages = tourPackageRepository.findAllByTourId(tourId);

        // Lấy capacity từ DB một lần cho tất cả room type (tránh N+1)
        List<UUID> roomTypeIds = packages.stream()
                .map(TourPackage::getRoomTypeId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        Map<UUID, Integer> capacityByRoomTypeId = new HashMap<>();
        roomTypeRepository.findAllById(roomTypeIds)
                .forEach(roomType -> capacityByRoomTypeId.put(roomType.getId(), roomType.getCapacity()));

        return packages.stream()
                .map(pkg -> TourPackageMapper.toResponse(
                        pkg,
                        packageBenefitRepository.findAllByTourPackageId(pkg.getId()),
                        capacityByRoomTypeId.get(pkg.getRoomTypeId())))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomTypeResponse> getRoomTypesByTourId(UUID tourId) {
        Tour tour = tourPackageValidator.getTourWithCruise(tourId);

        return roomTypeRepository.findRoomTypesByCruiseId(tour.getCruise().getId()).stream()
                .map(RoomTypeMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public BookingPackageInfo getBookingPackageInfo(UUID tourPackageId) {

        TourPackage tourPackage = tourPackageValidator.getPackageOrThrow(tourPackageId);
        tourPackageValidator.validateHasRoomType(tourPackage);

        RoomType roomType = tourPackageValidator.getRoomTypeOrThrow(tourPackage.getRoomTypeId());
        tourPackageValidator.validateCapacity(roomType);

        return new BookingPackageInfo(
                tourPackage.getId(),
                tourPackage.getPrice(),
                roomType.getCapacity());
    }

    // =========================================================
    // PRIVATE HELPERS
    // =========================================================
    private List<PackageBenefit> saveBenefits(UUID tourPackageId, TourPackageRequest request) {
        if (request.benefits() == null || request.benefits().isEmpty()) {
            return List.of();
        }
        return packageBenefitRepository.saveAll(
                PackageBenefitMapper.toEntities(tourPackageId, request.benefits()));
    }

    private TourPackageSyncedEvent toSyncedEvent(TourPackage pkg, Integer capacity, String status) {
        return new TourPackageSyncedEvent(
                pkg.getId(),
                pkg.getTourId(),
                pkg.getName(),
                pkg.getPrice(),
                capacity,
                status);
    }
}