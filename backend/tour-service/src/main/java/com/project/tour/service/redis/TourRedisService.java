package com.project.tour.service.redis;

import java.util.UUID;

public interface TourRedisService {
    void saveRemainingSeats(UUID tourId, Integer maxPassengers);

    Long getRemainingSeats(UUID tourId);

    void deleteRemainingSeats(UUID tourId);

    boolean tryReserveSeats(UUID tourId, int requestedSeats);

}