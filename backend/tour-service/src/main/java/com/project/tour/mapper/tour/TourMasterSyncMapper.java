package com.project.tour.mapper.tour;

import com.project.common.event.TourAssignmentEvent;
import com.project.common.event.TourMasterSyncEvent;
import com.project.tour.model.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TourMasterSyncMapper {

    public static TourMasterSyncEvent toEvent(
            Tour tour,
            List<CruiseDeck> decks,
            Map<UUID, List<CruiseArea>> deckIdToAreasMap,
            List<Schedule> schedules,
            Map<UUID, List<ScheduleStop>> scheduleIdToStopsMap,
            List<TourAssignmentEvent> assignments) {
        Cruise cruise = tour.getCruise();

        List<TourMasterSyncEvent.DeckDetail> deckDetails = decks.stream()
                .map(deck -> {
                    List<CruiseArea> areas = deckIdToAreasMap.getOrDefault(deck.getId(), List.of());
                    List<TourMasterSyncEvent.AreaDetail> areaDetails = areas.stream()
                            .map(area -> new TourMasterSyncEvent.AreaDetail(
                                    area.getId(),
                                    area.getName(),
                                    area.getDescription(),
                                    area.getStatus() != null ? area.getStatus().name() : null,
                                    area.getImageUrl()))
                            .toList();

                    return new TourMasterSyncEvent.DeckDetail(
                            deck.getId(),
                            deck.getDeckNumber(),
                            deck.getStatus() != null ? deck.getStatus().name() : null,
                            areaDetails);
                })
                .toList();

        TourMasterSyncEvent.CruiseDetail cruiseDetail = new TourMasterSyncEvent.CruiseDetail(
                cruise.getId(),
                cruise.getCode(),
                cruise.getName(),
                cruise.getDescription(),
                cruise.getMaxPassengers(),
                cruise.getImageUrl(),
                cruise.getStatus() != null ? cruise.getStatus().name() : null,
                deckDetails);

        List<TourMasterSyncEvent.ScheduleDetail> scheduleDetails = schedules.stream()
                .map(schedule -> {
                    List<ScheduleStop> stops = scheduleIdToStopsMap.getOrDefault(schedule.getId(), List.of());
                    List<TourMasterSyncEvent.ScheduleStopDetail> stopDetails = stops.stream()
                            .map(stop -> new TourMasterSyncEvent.ScheduleStopDetail(
                                    stop.getId(),
                                    stop.getPort().getId(),
                                    stop.getPort().getName(),
                                    stop.getStopOrder(),
                                    stop.getArriveAt(),
                                    stop.getLeaveAt()))
                            .toList();

                    return new TourMasterSyncEvent.ScheduleDetail(
                            schedule.getId(),
                            schedule.getName(),
                            schedule.getDescription(),
                            schedule.getDayNumber(),
                            schedule.getRealDay(),
                            schedule.getStatus() != null ? schedule.getStatus().name() : null,
                            stopDetails);
                })
                .toList();

        return new TourMasterSyncEvent(
                tour.getId(),
                tour.getCode(),
                tour.getName(),
                tour.getDescription(),
                tour.getStartDate(),
                tour.getEndDate(),
                tour.getStatusTrip().name(),
                cruiseDetail,
                scheduleDetails,
                assignments);
    }
}