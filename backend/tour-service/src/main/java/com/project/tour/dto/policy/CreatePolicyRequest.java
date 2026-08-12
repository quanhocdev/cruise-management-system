package com.project.tour.dto.policy;

import com.project.tour.model.enums.PolicyType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreatePolicyRequest {

    @NotNull(message = "Policy type is required")
    private PolicyType type;

    @NotBlank(message = "Policy title is required")
    @Size(max = 200, message = "Policy title must not exceed 200 characters")
    private String title;

    @NotBlank(message = "Policy content is required")
    private String content;

    public PolicyType getType() {
        return type;
    }

    public void setType(PolicyType type) {
        this.type = type;
    }

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
}