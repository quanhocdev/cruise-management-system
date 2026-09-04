package com.project.activitycruise.dto;

import org.springframework.web.multipart.MultipartFile;

import com.project.activitycruise.model.enums.ActivityCruiseStatus;

public record UpdateActivityCruiseRequest(
        String name,
        String description,
        ActivityCruiseStatus status,
        MultipartFile image) {
}