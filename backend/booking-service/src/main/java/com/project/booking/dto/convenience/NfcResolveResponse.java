package com.project.booking.dto.convenience;

import java.util.UUID;

public record NfcResolveResponse(

        Long bookingPassengerId,

        String passengerName,

        Long bookingId,

        UUID tourId,

        UUID tourPackageId

) {
}