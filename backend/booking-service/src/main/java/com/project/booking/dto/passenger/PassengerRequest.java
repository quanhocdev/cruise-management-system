package com.project.booking.dto.passenger;

import com.project.booking.model.enums.DocumentType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

/**
 * DTO dùng để tạo mới hoặc cập nhật thông tin hành khách.
 * Không chứa id, userId, timestamps.
 */
public record PassengerRequest(

        @NotBlank(message = "Full name is required") @Size(max = 150, message = "Full name must be less than 150 characters") String fullName,

        @NotNull(message = "Date of birth is required") LocalDate dateOfBirth,

        @NotBlank(message = "Gender is required") @Size(max = 20, message = "Gender must be less than 20 characters") String gender,

        @Size(max = 30, message = "Phone number must be less than 30 characters") String phoneNumber,

        @Email(message = "Invalid email format") @Size(max = 255) String email,

        @NotNull(message = "Document type is required") DocumentType idCardType,

        @NotBlank(message = "Identification number is required") @Size(max = 50, message = "Identification number must be less than 50 characters") String identificationNumber,

        @Size(max = 255, message = "Document note must be less than 255 characters") String documentNote,

        // Trường này sẽ được set ở Service sau khi upload ảnh lên Cloudinary
        String idCardImageUrl) {
    // Compact constructor để validate bổ sung nếu cần
    public PassengerRequest {
        // Ví dụ: chuẩn hóa email về chữ thường
        if (email != null) {
            email = email.trim().toLowerCase();
        }
    }
}