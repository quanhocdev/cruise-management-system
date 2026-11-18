
package com.project.booking.mapper.passenger;

import com.project.booking.dto.passenger.passenger.PassengerProfileDTO;
import com.project.booking.model.Passenger;

public class PassengerMapper {

    public PassengerProfileDTO toDTO(Passenger passenger) {
        if (passenger == null) {
            return null;
        }

        return new PassengerProfileDTO(
                passenger.getId(),
                passenger.getFullName(),
                passenger.getDateOfBirth(),
                passenger.getGender(),
                passenger.getPhoneNumber(),
                passenger.getEmail(),
                passenger.getIdCardType(),
                passenger.getIdentificationNumber(),
                passenger.getDocumentNote(),
                passenger.getIdCardImageUrl(),
                passenger.getIdCardImagePublicId(),
                passenger.getCreatedAt(),
                passenger.getUpdatedAt());
    }
}
