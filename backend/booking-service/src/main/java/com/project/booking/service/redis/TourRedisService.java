package com.project.booking.service.redis;

import java.util.UUID;

public interface TourRedisService {

    boolean tryReserveSeats(UUID tourId, int requestedSeats);

}