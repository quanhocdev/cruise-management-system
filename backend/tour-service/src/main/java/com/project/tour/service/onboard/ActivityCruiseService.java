package com.project.tour.service.onboard;

import com.project.tour.dto.onboard.ActivityCruiseResponse;
import com.project.tour.dto.onboard.CreateActivityCruiseRequest;
import com.project.tour.dto.onboard.UpdateActivityCruiseRequest;
import com.project.tour.mapper.onboard.ActivityCruiseMapper;
import com.project.tour.model.ActivityCruise;
import com.project.tour.repository.onboard.ActivityCruiseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ActivityCruiseService {

    private final ActivityCruiseRepository activityCruiseRepository;
    private final ActivityCruiseMapper activityCruiseMapper;

    public ActivityCruiseService(ActivityCruiseRepository activityCruiseRepository,
            ActivityCruiseMapper activityCruiseMapper) {
        this.activityCruiseRepository = activityCruiseRepository;
        this.activityCruiseMapper = activityCruiseMapper;
    }

    @Transactional(readOnly = true)
    public Page<ActivityCruiseResponse> getAllActivities(Pageable pageable) {
        return activityCruiseRepository.findAll(pageable)
                .map(activityCruiseMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<ActivityCruiseResponse> getActivitiesByArea(Long cruiseAreaId, Pageable pageable) {
        return activityCruiseRepository.findByCruiseAreaId(cruiseAreaId, pageable)
                .map(activityCruiseMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public ActivityCruiseResponse getActivityById(Long id) {
        ActivityCruise activity = activityCruiseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hoạt động du thuyền với ID: " + id));
        return activityCruiseMapper.toResponse(activity);
    }

    @Transactional
    public ActivityCruiseResponse createActivity(CreateActivityCruiseRequest request) {
        if (request.endTime().isBefore(request.startTime())) {
            throw new IllegalArgumentException("Thời gian kết thúc không thể trước thời gian bắt đầu");
        }

        ActivityCruise activity = activityCruiseMapper.toEntity(request);
        ActivityCruise saved = activityCruiseRepository.save(activity);
        return activityCruiseMapper.toResponse(saved);
    }

    @Transactional
    public ActivityCruiseResponse updateActivity(Long id, UpdateActivityCruiseRequest request) {
        ActivityCruise activity = activityCruiseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hoạt động du thuyền với ID: " + id));

        if (request.endTime() != null && request.startTime() != null
                && request.endTime().isBefore(request.startTime())) {
            throw new IllegalArgumentException("Thời gian kết thúc không thể trước thời gian bắt đầu");
        }

        activityCruiseMapper.updateEntityFromRequest(request, activity);
        ActivityCruise updated = activityCruiseRepository.save(activity);
        return activityCruiseMapper.toResponse(updated);
    }

    @Transactional
    public void deleteActivity(Long id) {
        if (!activityCruiseRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy hoạt động du thuyền với ID: " + id);
        }
        activityCruiseRepository.deleteById(id);
    }
}