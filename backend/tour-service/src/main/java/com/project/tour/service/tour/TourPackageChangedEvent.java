package com.project.tour.service.tour;

import com.project.common.event.TourPackageSyncedEvent;

import java.util.UUID;

/**
 * Event NỘI BỘ (Spring), publish trong transaction.
 * TourPackageEventListener chỉ xử lý sau khi DB commit thành công.
 *
 * @param availableRooms số phòng cần ghi lên Redis; null = không đổi Redis
 * @param deleted        true = xóa key Redis của package
 */
public record TourPackageChangedEvent(
        UUID packageId,
        TourPackageSyncedEvent syncedEvent,
        Integer availableRooms,
        boolean deleted) {

    public static TourPackageChangedEvent upserted(
            UUID packageId, TourPackageSyncedEvent syncedEvent, Integer availableRooms) {
        return new TourPackageChangedEvent(packageId, syncedEvent, availableRooms, false);
    }

    public static TourPackageChangedEvent deleted(UUID packageId, TourPackageSyncedEvent syncedEvent) {
        return new TourPackageChangedEvent(packageId, syncedEvent, null, true);
    }
}