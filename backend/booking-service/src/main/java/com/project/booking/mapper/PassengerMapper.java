package com.project.booking.mapper;

import com.project.booking.dto.passenger.PassengerRequest;
import com.project.booking.dto.passenger.PassengerResponse;
import com.project.booking.model.Passenger;
import org.springframework.stereotype.Component;

@Component
public class PassengerMapper {

    public Passenger toEntity(PassengerRequest request) {
        if (request == null) {
            return null;
        }
        Passenger passenger = new Passenger();
        passenger.setFullName(request.getFullName() != null ? request.getFullName().trim() : null);
        passenger.setDateOfBirth(request.getDateOfBirth());
        passenger.setGender(request.getGender() != null ? request.getGender().trim() : null);
        passenger.setPhoneNumber(request.getPhoneNumber());
        passenger.setEmail(request.getEmail());
        passenger.setIdCardType(request.getIdCardType());
        passenger.setIdentificationNumber(
                request.getIdentificationNumber() != null ? request.getIdentificationNumber().trim() : null);
        passenger.setDocumentNote(request.getDocumentNote());
        return passenger;
    }

    public PassengerResponse toResponse(Passenger passenger) {
        if (passenger == null) {
            return null;
        }

        // Lấy bookingId từ quan hệ Booking nếu cần hiển thị ra DTO (hoặc truyền null
        // nếu DTO của bạn không có trường này)
        Long bookingId = passenger.getBooking() != null ? passenger.getBooking().getId() : null;

        return new PassengerResponse(
                passenger.getId(),
                bookingId, // Thay thế cho passenger.getUserId() cũ
                passenger.getFullName(),
                passenger.getDateOfBirth(),
                passenger.getGender(),
                passenger.getPhoneNumber(),
                passenger.getEmail(),
                passenger.getIdCardType(),
                passenger.getIdentificationNumber(),
                passenger.getDocumentNote(),
                passenger.getIdCardImageUrl(),
                passenger.getCreatedAt(),
                passenger.getUpdatedAt());
    }
}