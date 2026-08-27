package com.project.common.event;

import com.project.common.event.enums.TourAssignmentType;

import java.util.UUID;

public record TourAssignmentEvent(

                UUID tourId,

                UUID targetId,

                TourAssignmentType type

) {
}