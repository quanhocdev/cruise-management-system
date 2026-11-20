package com.project.booking.dto.booking;

import com.project.booking.dto.passenger.PassengerRequest;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class BookingRequestSnapshot {

    private BookingRequestSnapshot() {
    }

    public static CreateBookingRequest copy(
            CreateBookingRequest source) throws IOException {

        if (source == null) {
            throw new IllegalArgumentException(
                    "CreateBookingRequest không được null.");
        }

        CreateBookingRequest copy = new CreateBookingRequest();

        copy.setTourId(source.getTourId());
        copy.setTourPackageId(source.getTourPackageId());
        copy.setNumberOfRooms(source.getNumberOfRooms());
        copy.setPrimaryContactName(source.getPrimaryContactName());
        copy.setPrimaryContactPhone(source.getPrimaryContactPhone());

        List<PassengerRequest> passengers = new ArrayList<>();

        if (source.getPassengers() != null) {
            for (PassengerRequest original : source.getPassengers()) {
                passengers.add(copyPassenger(original));
            }
        }

        copy.setPassengers(passengers);

        return copy;
    }

    private static PassengerRequest copyPassenger(
            PassengerRequest source) throws IOException {

        PassengerRequest copy = new PassengerRequest();

        copy.setFullName(source.getFullName());
        copy.setDateOfBirth(source.getDateOfBirth());
        copy.setGender(source.getGender());
        copy.setPhoneNumber(source.getPhoneNumber());
        copy.setEmail(source.getEmail());
        copy.setIdCardType(source.getIdCardType());
        copy.setIdentificationNumber(source.getIdentificationNumber());
        copy.setDocumentNote(source.getDocumentNote());

        MultipartFile originalFile = source.getIdCardImage();

        if (originalFile != null && !originalFile.isEmpty()) {

            byte[] fileBytes = originalFile.getBytes();

            InMemoryMultipartFile copiedFile = new InMemoryMultipartFile(
                    originalFile.getName(),
                    originalFile.getOriginalFilename(),
                    originalFile.getContentType(),
                    fileBytes);

            copy.setIdCardImage(copiedFile);
        }

        return copy;
    }
}