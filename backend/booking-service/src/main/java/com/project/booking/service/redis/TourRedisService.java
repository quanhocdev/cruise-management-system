package com.project.booking.service.redis;

import java.util.UUID;

public interface TourRedisService {

    Long reservePackageRooms(UUID tourPackageId, int requestedRooms);

    Long releasePackageRooms(
            UUID tourPackageId,
            int releasedRooms,
            UUID refundRequestId);
}