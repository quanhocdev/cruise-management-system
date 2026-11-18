
package com.project.booking.mapper.passenger;

import com.project.booking.dto.passenger.booking.PassengerBookingDTO;
import com.project.booking.model.Booking;

public class BookingMapper {

    public PassengerBookingDTO toDTO(Booking booking) {
        if (booking == null) {
            return null;
        }

        return new PassengerBookingDTO(
                booking.getId(),
                booking.getCreatedByUserId(),
                booking.getTourId(),
                booking.getTourPackageId(),
                booking.getBookingCode(),
                booking.getNumberPassengers(),
                booking.getPrimaryContactName(),
                booking.getPrimaryContactEmail(),
                booking.getPrimaryContactPhone(),
                booking.getNumberOfRooms(),
                booking.getTotalAmount(),
                booking.getStatus(),
                booking.getCreatedAt(),
                booking.getUpdatedAt());
    }
}
