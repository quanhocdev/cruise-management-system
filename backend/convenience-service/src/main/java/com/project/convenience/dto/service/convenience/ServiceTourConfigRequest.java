package com.project.convenience.dto.service.convenience;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record ServiceTourConfigRequest(

        @NotNull(message = "Service không được để trống") UUID serviceId,

        @NotNull(message = "Số hành khách tối đa không được để trống") @Positive(message = "Số hành khách tối đa phải lớn hơn 0") Integer maxPassengers,

        // null = không giới hạn thời gian
        @Positive(message = "Thời lượng dịch vụ phải lớn hơn 0") Integer durationMinutes

) {
}