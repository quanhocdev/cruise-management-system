package com.project.tour.service.scheduler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.tour.dto.scheduler.SchedulerTourDtos.*;
import com.project.tour.mapper.scheduler.SchedulerTourMapper;
import com.project.tour.model.enums.tour.TourStatusTrip;
import com.project.tour.model.scheduler.Schedule;
import com.project.tour.model.scheduler.ScheduleStop;
import com.project.tour.model.scheduler.Tour;
import com.project.tour.model.scheduler.TourRoute;
import com.project.tour.repository.scheduler.TourRepository;
import com.project.tour.repository.scheduler.TourRouteRepository;
import com.project.tour.repository.tour.schedule.ScheduleRepository;
import com.project.tour.repository.tour.schedule.ScheduleStopRepository;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class SchedulerTourService {

    private final TourRepository tourRepository;
    private final TourRouteRepository tourRouteRepository;
    private final ScheduleRepository scheduleRepository;
    private final ScheduleStopRepository scheduleStopRepository;

    private final SchedulerTourMapper schedulerTourMapper;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private static final String REDIS_KEY_PREFIX = "vessel:location:";

    public SchedulerTourService(
            TourRepository tourRepository,
            TourRouteRepository tourRouteRepository,
            ScheduleRepository scheduleRepository,
            ScheduleStopRepository scheduleStopRepository,
            SchedulerTourMapper schedulerTourMapper,
            StringRedisTemplate redisTemplate,
            ObjectMapper objectMapper) {
        this.tourRepository = tourRepository;
        this.tourRouteRepository = tourRouteRepository;
        this.scheduleRepository = scheduleRepository;
        this.scheduleStopRepository = scheduleStopRepository;
        this.schedulerTourMapper = schedulerTourMapper;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @Transactional(readOnly = true)
    public List<TourSummaryResponse> getToursByStatuses(List<String> statusStrs) {
        // Nếu không truyền lên thì service tự động mặc định lấy READY và IN_PROGRESS
        List<String> effectiveStatuses = (statusStrs == null || statusStrs.isEmpty())
                ? List.of("READY", "IN_PROGRESS")
                : statusStrs;

        List<TourStatusTrip> statuses = effectiveStatuses.stream()
                .map(TourStatusTrip::valueOf)
                .toList();

        List<Tour> tours = tourRepository.findByStatusTripIn(statuses);

        return tours.stream()
                .map(schedulerTourMapper::toSummaryResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public TourDetailResponse getTourDetailsWithTracking(UUID tourId) {
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new RuntimeException("Tour not found with id: " + tourId));

        TourRoute route = tourRouteRepository.findByTour_Id(tourId).orElse(null);

        List<Schedule> schedules = scheduleRepository.findAllByTour_IdOrderByDayNumberAsc(tourId);

        List<List<ScheduleStop>> stopsPerSchedule = schedules.stream()
                .map(s -> scheduleStopRepository.findAllBySchedule_IdOrderByStopOrderAsc(s.getId()))
                .toList();

        VesselPositionInfo currentPosition = null;
        try {
            String redisKey = REDIS_KEY_PREFIX + tourId;
            String jsonValue = redisTemplate.opsForValue().get(redisKey);
            if (jsonValue != null) {
                RedisPositionDto redisDto = objectMapper.readValue(jsonValue, RedisPositionDto.class);
                currentPosition = new VesselPositionInfo(redisDto.latitude(), redisDto.longitude(),
                        redisDto.updatedAt());
            }
        } catch (Exception e) {
            // Bỏ qua lỗi Redis
        }

        return schedulerTourMapper.toDetailResponse(tour, route, schedules, stopsPerSchedule, currentPosition);
    }
}