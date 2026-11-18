
package com.project.booking.mapper.passenger;

import com.project.booking.dto.passenger.passenger.PassengerBookingPassengerDTO;
import com.project.booking.model.BookingPassenger;

public class BookingPassengerMapper {

    private final PassengerMapper passengerMapper;

    public BookingPassengerMapper(PassengerMapper passengerMapper) {
        this.passengerMapper = passengerMapper;
    }

    public PassengerBookingPassengerDTO toDTO(
            BookingPassenger bookingPassenger) {
        if (bookingPassenger == null) {
            return null;
        }

        return new PassengerBookingPassengerDTO(
                bookingPassenger.getId(),
                passengerMapper.toDTO(bookingPassenger.getPassenger()),
                bookingPassenger.getRoomId(),
                bookingPassenger.getNfcCardUid(),
                bookingPassenger.getStatus(),
                bookingPassenger.getCheckedInAt(),
                bookingPassenger.getCheckedOutAt(),
                bookingPassenger.getCreatedAt(),
                bookingPassenger.getUpdatedAt());
    }
}
