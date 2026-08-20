package com.project.tour.dto.tour.convenience.service;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record ServiceTourConfigRequest(

        @NotNull(message = "Service không được để trống") UUID serviceId,

        @NotNull(message = "Số hành khách tối đa không được để trống") @Positive(message = "Số hành khách tối đa phải lớn hơn 0") Integer maxPassengers,

        @NotNull(message = "Thời lượng dịch vụ không được để trống") @Positive(message = "Thời lượng dịch vụ phải lớn hơn 0") Integer durationMinutes

) {
}