package com.project.tour.dto.activitycruise;

import org.springframework.web.multipart.MultipartFile;

import com.project.tour.model.onboard.enums.ActivityCruiseStatus;

public record UpdateActivityCruiseRequest(

                String name,

                String description,

                ActivityCruiseStatus status,

                MultipartFile image) {

}
