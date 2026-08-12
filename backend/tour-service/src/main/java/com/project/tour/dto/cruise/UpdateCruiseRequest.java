package com.project.tour.dto.cruise;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

public class UpdateCruiseRequest {

    @NotBlank(message = "Cruise name is required")
    @Size(max = 150, message = "Cruise name must not exceed 150 characters")
    private String name;

    @NotBlank(message = "Cruise code is required")
    @Size(max = 50, message = "Cruise code must not exceed 50 characters")
    private String code;

    @Size(max = 5000, message = "Description must not exceed 5000 characters")
    private String description;

    @NotNull(message = "Max passengers is required")
    @Positive(message = "Max passengers must be greater than 0")
    private Integer maxPassengers;

    private MultipartFile image;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getMaxPassengers() {
        return maxPassengers;
    }

    public void setMaxPassengers(Integer maxPassengers) {
        this.maxPassengers = maxPassengers;
    }

    public MultipartFile getImage() {
        return image;
    }

    public void setImage(MultipartFile image) {
        this.image = image;
    }
}