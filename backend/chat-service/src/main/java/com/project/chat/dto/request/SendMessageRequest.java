
package com.project.chat.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SendMessageRequest(

        @NotBlank(message = "Nội dung tin nhắn không được để trống") @Size(max = 5000, message = "Tin nhắn không được vượt quá 5000 ký tự") String content

) {
}
