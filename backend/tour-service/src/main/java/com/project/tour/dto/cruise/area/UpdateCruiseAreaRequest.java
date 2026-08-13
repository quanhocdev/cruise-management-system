package com.project.tour.dto.cruise.area;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import org.springframework.web.multipart.MultipartFile;

import com.project.tour.model.enums.cruise.CruiseAreaStatus;

public class UpdateCruiseAreaRequest {

    @NotBlank(message = "Area name is required")
    @Size(max = 150, message = "Area name must not exceed 150 characters")
    private String name;

    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    private String description;

    @NotNull(message = "Area status is required")
    private CruiseAreaStatus status;

    private MultipartFile image;

    public UpdateCruiseAreaRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public CruiseAreaStatus getStatus() {
        return status;
    }

    public void setStatus(CruiseAreaStatus status) {
        this.status = status;
    }

    public MultipartFile getImage() {
        return image;
    }

    public void setImage(MultipartFile image) {
        this.image = image;
    }
}