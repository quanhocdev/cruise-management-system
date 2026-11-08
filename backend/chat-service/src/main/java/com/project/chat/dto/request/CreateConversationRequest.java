
package com.project.chat.dto.request;

import com.project.chat.model.enums.ChatDepartment;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateConversationRequest(

        @NotNull(message = "Booking ID không được để trống") @Positive(message = "Booking ID phải lớn hơn 0") Long bookingId,

        @NotNull(message = "Phòng ban hỗ trợ không được để trống") ChatDepartment department

) {
}
