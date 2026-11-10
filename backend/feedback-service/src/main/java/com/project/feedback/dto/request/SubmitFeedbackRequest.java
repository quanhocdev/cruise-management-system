
package com.project.feedback.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record SubmitFeedbackRequest(

        @Min(value = 1, message = "Rating phải từ 1 đến 5") @Max(value = 5, message = "Rating phải từ 1 đến 5") Integer rating,

        @Size(max = 2000, message = "Comment không được vượt quá 2000 ký tự") String comment

) {
}
