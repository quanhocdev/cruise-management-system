package com.project.tour.dto.onboard;

import com.project.tour.model.enums.onboard.ActivityCruiseStatus;
import org.springframework.web.multipart.MultipartFile;

public record UpdateActivityCruiseRequest(
        String name,
        String description,
        ActivityCruiseStatus status,
        MultipartFile image) {
}