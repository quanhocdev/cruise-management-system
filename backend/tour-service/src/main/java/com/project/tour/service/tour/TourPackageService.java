package com.project.tour.service.tour;

import com.project.tour.dto.admin.roomtype.RoomTypeResponse;
import com.project.tour.dto.internal.BookingPackageInfo;
import com.project.tour.dto.operation.packages.TourPackageRequest;
import com.project.tour.dto.operation.packages.TourPackageResponse;

import java.util.List;
import java.util.UUID;

public interface TourPackageService {

    TourPackageResponse createPackage(TourPackageRequest request);

    TourPackageResponse patchPackage(UUID packageId, TourPackageRequest request);

    void deletePackage(UUID packageId);

    List<TourPackageResponse> getPackagesByTourId(UUID tourId);

    List<RoomTypeResponse> getRoomTypesByTourId(UUID tourId);

    /** API nội bộ cho booking-service. */
    BookingPackageInfo getBookingPackageInfo(UUID tourPackageId);
}