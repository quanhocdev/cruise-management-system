package com.project.booking.dto.booking;

import com.project.booking.dto.passenger.PassengerRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

public class CreateBookingRequest {

    @NotNull(message = "Tour ID is required")
    private String tourId;

    @NotNull(message = "Tour Package ID is required")
    private String tourPackageId;

    @NotBlank(message = "Primary contact name is required")
    @Size(max = 150, message = "Contact name must be less than 150 characters")
    private String primaryContactName;

    @NotBlank(message = "Primary contact phone is required")
    @Size(max = 30, message = "Contact phone must be less than 30 characters")
    private String primaryContactPhone;

    @NotEmpty(message = "Passenger list cannot be empty")
    @Valid
    private List<PassengerRequest> passengers = new ArrayList<>();

    public CreateBookingRequest() {
    }

    // Getters và Setters
    public String getTourId() {
        return tourId;
    }

    public void setTourId(String tourId) {
        this.tourId = tourId;
    }

    public String getTourPackageId() {
        return tourPackageId;
    }

    public void setTourPackageId(String tourPackageId) {
        this.tourPackageId = tourPackageId;
    }

    public String getPrimaryContactName() {
        return primaryContactName;
    }

    public void setPrimaryContactName(String primaryContactName) {
        this.primaryContactName = primaryContactName;
    }

    public String getPrimaryContactPhone() {
        return primaryContactPhone;
    }

    public void setPrimaryContactPhone(String primaryContactPhone) {
        this.primaryContactPhone = primaryContactPhone;
    }

    public List<PassengerRequest> getPassengers() {
        return passengers;
    }

    public void setPassengers(List<PassengerRequest> passengers) {
        this.passengers = passengers;
    }
}