package com.project.activitycruise.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

import com.project.activitycruise.model.enums.ActivityCruiseStatus;

public record CreateActivityCruiseRequest(
        @NotBlank(message = "Tên hoạt động không được để trống") String name,

        String description,

        @NotNull(message = "Trạng thái không được để trống") ActivityCruiseStatus status,

        MultipartFile image) {
}