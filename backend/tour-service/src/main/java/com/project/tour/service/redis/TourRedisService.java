package com.project.tour.service.redis;

import java.util.UUID;

public interface TourRedisService {
    void savePackageAvailableRooms(UUID tourPackageId, Integer totalRooms);

    Long getPackageAvailableRooms(UUID tourPackageId);

    void deletePackageAvailableRooms(UUID tourPackageId);

    Long reservePackageRooms(UUID tourPackageId, int requestedRooms);
}