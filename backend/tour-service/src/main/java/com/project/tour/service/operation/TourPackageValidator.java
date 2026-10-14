package com.project.tour.service.operation;

import com.project.tour.exception.AppException;
import com.project.tour.model.admin.RoomType;
import com.project.tour.model.operation.TourPackage;
import com.project.tour.model.scheduler.Tour;
import com.project.tour.repository.room.RoomTypeRepository;
import com.project.tour.repository.tour.TourPackageRepository;
import com.project.tour.repository.tour.TourRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TourPackageValidator {

    private final TourPackageRepository tourPackageRepository;
    private final TourRepository tourRepository;
    private final RoomTypeRepository roomTypeRepository;

    public TourPackageValidator(
            TourPackageRepository tourPackageRepository,
            TourRepository tourRepository,
            RoomTypeRepository roomTypeRepository) {
        this.tourPackageRepository = tourPackageRepository;
        this.tourRepository = tourRepository;
        this.roomTypeRepository = roomTypeRepository;
    }

    /**
     * Tên package không được trùng trong cùng tour.
     * currentName = null khi tạo mới; khi patch truyền tên hiện tại để cho phép giữ
     * nguyên tên.
     */
    public void validateUniqueName(UUID tourId, String name, String currentName) {
        boolean exists = tourPackageRepository.existsByTourIdAndName(tourId, name);
        if (exists && (currentName == null || !currentName.equals(name))) {
            throw new AppException("A package with this name already exists for this tour", HttpStatus.BAD_REQUEST);
        }
    }

    public Tour getTourOrThrow(UUID tourId) {
        return tourRepository.findById(tourId)
                .orElseThrow(() -> new AppException("Tour not found", HttpStatus.NOT_FOUND));
    }

    /** Tour phải tồn tại và đã được gán cruise. */
    public Tour getTourWithCruise(UUID tourId) {
        Tour tour = getTourOrThrow(tourId);
        if (tour.getCruise() == null) {
            throw new AppException("This tour does not have an assigned cruise yet", HttpStatus.BAD_REQUEST);
        }
        return tour;
    }

    public RoomType getRoomTypeOrThrow(UUID roomTypeId) {
        return roomTypeRepository.findById(roomTypeId)
                .orElseThrow(() -> new AppException("Room type not found", HttpStatus.NOT_FOUND));
    }

    public TourPackage getPackageOrThrow(UUID packageId) {
        return tourPackageRepository.findById(packageId)
                .orElseThrow(() -> new AppException("Tour package not found", HttpStatus.NOT_FOUND));
    }

    public void validateHasRoomType(TourPackage tourPackage) {
        if (tourPackage.getRoomTypeId() == null) {
            throw new AppException("Tour package does not have a room type", HttpStatus.BAD_REQUEST);
        }
    }

    public void validateCapacity(RoomType roomType) {
        if (roomType.getCapacity() == null || roomType.getCapacity() <= 0) {
            throw new AppException("Room type capacity is invalid", HttpStatus.BAD_REQUEST);
        }
    }
}