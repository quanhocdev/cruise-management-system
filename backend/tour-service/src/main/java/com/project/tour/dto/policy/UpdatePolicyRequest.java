package com.project.tour.dto.policy;

import com.project.tour.model.enums.PolicyStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UpdatePolicyRequest {

    @NotBlank(message = "Policy title is required")
    @Size(max = 200, message = "Policy title must not exceed 200 characters")
    private String title;

    @NotBlank(message = "Policy content is required")
    private String content;

    @NotNull(message = "Policy status is required")
    private PolicyStatus status;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public PolicyStatus getStatus() {
        return status;
    }

    public void setStatus(PolicyStatus status) {
        this.status = status;
    }
}