package com.project.tour.dto.onboard;

import com.project.tour.model.enums.onboard.ActivityCruiseStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record CreateActivityCruiseRequest(
        @NotBlank(message = "Tên hoạt động không được để trống") String name,

        String description,

        @NotNull(message = "Trạng thái không được để trống") ActivityCruiseStatus status,

        MultipartFile image) {
}