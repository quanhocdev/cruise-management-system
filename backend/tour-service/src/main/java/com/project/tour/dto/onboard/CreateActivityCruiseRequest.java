package com.project.tour.dto.onboard;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreateActivityCruiseRequest(
        @NotNull(message = "Cruise Area ID không được để trống") Long cruiseAreaId,

        @NotBlank(message = "Tên hoạt động không được để trống") @Size(max = 255, message = "Tên hoạt động không quá 255 ký tự") String name,

        String description,

        @NotNull(message = "Thời gian bắt đầu không được để trống") LocalDateTime startTime,

        @NotNull(message = "Thời gian kết thúc không được để trống") LocalDateTime endTime,

        @Min(value = 1, message = "Số lượng hành khách tối đa phải lớn hơn 0") Integer maxPassengers,

        @DecimalMin(value = "0.0", message = "Giá vé không được nhỏ hơn 0") BigDecimal price,

        String status,
        String imageUrl,
        String imagePublicId) {
}