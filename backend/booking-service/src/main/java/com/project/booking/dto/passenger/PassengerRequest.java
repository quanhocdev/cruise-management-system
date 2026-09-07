package com.project.booking.dto.passenger;

import com.project.booking.model.enums.DocumentType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

public class PassengerRequest {

    @NotBlank(message = "Full name is required")
    @Size(max = 150, message = "Full name must be less than 150 characters")
    private String fullName;

    @NotNull(message = "Date of birth is required")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateOfBirth;

    @NotBlank(message = "Gender is required")
    @Size(max = 20, message = "Gender must be less than 20 characters")
    private String gender;

    @Size(max = 30, message = "Phone number must be less than 30 characters")
    private String phoneNumber;

    @Email(message = "Invalid email format")
    @Size(max = 255)
    private String email;

    @NotNull(message = "Document type is required")
    private DocumentType idCardType;

    @NotBlank(message = "Identification number is required")
    @Size(max = 50, message = "Identification number must be less than 50 characters")
    private String identificationNumber;

    @Size(max = 255, message = "Document note must be less than 255 characters")
    private String documentNote;

    private MultipartFile idCardImage;

    public PassengerRequest() {
    }

    // Getters và Setters
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email != null ? email.trim().toLowerCase() : null;
    }

    public DocumentType getIdCardType() {
        return idCardType;
    }

    public void setIdCardType(DocumentType idCardType) {
        this.idCardType = idCardType;
    }

    public String getIdentificationNumber() {
        return identificationNumber;
    }

    public void setIdentificationNumber(String identificationNumber) {
        this.identificationNumber = identificationNumber;
    }

    public String getDocumentNote() {
        return documentNote;
    }

    public void setDocumentNote(String documentNote) {
        this.documentNote = documentNote;
    }

    public MultipartFile getIdCardImage() {
        return idCardImage;
    }

    public void setIdCardImage(MultipartFile idCardImage) {
        this.idCardImage = idCardImage;
    }
}