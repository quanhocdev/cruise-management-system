package com.project.tour.dto.admin.roomtype;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

public record CreateRoomTypeImageRequest(
        MultipartFile image,

        @Size(max = 100, message = "Label must not exceed 100 characters") String label,

        @Min(value = 0, message = "Sort order must not be negative") Integer sortOrder) {
}