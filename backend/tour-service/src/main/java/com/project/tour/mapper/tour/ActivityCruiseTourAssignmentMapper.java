// package com.project.tour.mapper.tour;

// import
// com.project.activitycruise.dto.onboard.ActivityCruiseTourConfigRequest;
// import com.project.activitycruise.model.ActivityCruise;
// import com.project.activitycruise.model.ActivityCruiseTour;
// import
// com.project.tour.dto.tour.operation.ActivityCruiseTourAssignmentResponse;

// public final class ActivityCruiseTourAssignmentMapper {

// private ActivityCruiseTourAssignmentMapper() {
// }

// public static ActivityCruiseTourAssignmentResponse toResponse(
// ActivityCruiseTour assignment) {

// var tour = assignment.getTour();
// var cruiseArea = assignment.getCruiseArea();

// var cruiseDeck = cruiseArea != null
// ? cruiseArea.getCruiseDeck()
// : null;

// var activityCruise = assignment.getActivityCruise();

// return new ActivityCruiseTourAssignmentResponse(

// assignment.getId(),

// tour != null ? tour.getId() : null,
// tour != null ? tour.getCode() : null,
// tour != null ? tour.getName() : null,

// activityCruise != null
// ? activityCruise.getId()
// : null,

// activityCruise != null
// ? activityCruise.getName()
// : null,

// cruiseArea != null
// ? cruiseArea.getId()
// : null,

// cruiseArea != null
// ? cruiseArea.getName()
// : null,

// cruiseDeck != null
// ? cruiseDeck.getId()
// : null,

// cruiseDeck != null
// ? cruiseDeck.getDeckNumber()
// : null,

// assignment.getStartTime(),
// assignment.getEndTime(),
// assignment.getMaxPassengers(),
// assignment.getPrice(),
// assignment.getStatus(),
// assignment.getCreatedAt(),
// assignment.getUpdatedAt());
// }

// public static void applyConfig(
// ActivityCruiseTour assignment,
// ActivityCruiseTourConfigRequest request,
// ActivityCruise activityCruise) {

// assignment.setActivityCruise(activityCruise);

// assignment.setStartTime(
// request.startTime());

// assignment.setEndTime(
// request.endTime());

// assignment.setMaxPassengers(
// request.maxPassengers());

// assignment.setPrice(
// request.price());
// }
// }