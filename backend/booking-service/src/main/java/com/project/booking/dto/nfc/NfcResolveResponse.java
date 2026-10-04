package com.project.booking.dto.nfc;

import java.util.UUID;

public record NfcResolveResponse(

                Long bookingPassengerId,

                String passengerName,

                Long bookingId,

                UUID tourId,

                UUID tourPackageId

) {
}