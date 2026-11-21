
package com.project.tour.service.passenger;

import com.project.tour.dto.passenger.itinerary.PassengerScheduleDTO;
import com.project.tour.mapper.passenger.ScheduleMapper;
import com.project.tour.model.scheduler.Schedule;
import com.project.tour.repository.scheduler.TourRepository;
import com.project.tour.repository.tour.schedule.ScheduleRepository;
import com.project.tour.repository.tour.schedule.ScheduleStopRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class PassengerItineraryService {

    private final TourRepository tourRepository;
    private final ScheduleRepository scheduleRepository;
    private final ScheduleStopRepository scheduleStopRepository;
    private final ScheduleMapper scheduleMapper;

    public PassengerItineraryService(
            TourRepository tourRepository,
            ScheduleRepository scheduleRepository,
            ScheduleStopRepository scheduleStopRepository,
            ScheduleMapper scheduleMapper) {
        this.tourRepository = tourRepository;
        this.scheduleRepository = scheduleRepository;
        this.scheduleStopRepository = scheduleStopRepository;
        this.scheduleMapper = scheduleMapper;
    }

    @Transactional(readOnly = true)
    public List<PassengerScheduleDTO> getItinerary(UUID tourId) {

        if (!tourRepository.existsById(tourId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Không tìm thấy tour");
        }

        List<Schedule> schedules = scheduleRepository
                .findAllByTour_IdOrderByDayNumberAsc(tourId);

        return schedules.stream()
                .map(schedule -> {
                    var stops = scheduleStopRepository
                            .findAllBySchedule_IdOrderByStopOrderAsc(
                                    schedule.getId());

                    return scheduleMapper.toDTO(schedule, stops);
                })
                .toList();
    }
}
