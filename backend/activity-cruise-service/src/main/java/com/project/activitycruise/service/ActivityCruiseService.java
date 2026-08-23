package com.project.activitycruise.service;

import com.project.activitycruise.dto.ActivityCruiseResponse;
import com.project.activitycruise.dto.CreateActivityCruiseRequest;
import com.project.activitycruise.dto.UpdateActivityCruiseRequest;
import com.project.activitycruise.model.ActivityCruise;
import com.project.activitycruise.model.enums.ActivityCruiseStatus;
import com.project.common.dto.UploadResult;
import com.project.common.service.file.FileStorageService;
import com.project.tour.exception.AppException;
import com.project.tour.mapper.onboard.ActivityCruiseMapper;
import com.project.tour.repository.onboard.ActivityCruiseRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ActivityCruiseService {

    private final ActivityCruiseRepository activityCruiseRepository;
    private final FileStorageService fileStorageService;

    public ActivityCruiseService(
            ActivityCruiseRepository activityCruiseRepository,
            FileStorageService fileStorageService) {
        this.activityCruiseRepository = activityCruiseRepository;
        this.fileStorageService = fileStorageService;
    }

    /*
     * =====================================================
     * CREATE
     * =====================================================
     */
    public ActivityCruiseResponse createActivity(CreateActivityCruiseRequest request) {

        if (activityCruiseRepository.existsByNameIgnoreCase(request.name())) {
            throw new AppException(
                    "Activity name already exists",
                    HttpStatus.CONFLICT);
        }

        ActivityCruise activity = ActivityCruiseMapper.toEntity(request);

        if (request.image() != null && !request.image().isEmpty()) {
            UploadResult uploadResult = fileStorageService.saveMultipart(
                    request.image(),
                    "activity_cruises");

            activity.setImageUrl(uploadResult.getUrl());
            activity.setImagePublicId(uploadResult.getPublicId());
        }

        ActivityCruise savedActivity = activityCruiseRepository.save(activity);

        return ActivityCruiseMapper.toResponse(savedActivity);
    }

    /*
     * =====================================================
     * GET BY ID
     * =====================================================
     */
    @Transactional(readOnly = true)
    public ActivityCruiseResponse getActivityById(UUID id) {

        ActivityCruise activity = findActivity(id);

        return ActivityCruiseMapper.toResponse(activity);
    }

    /*
     * =====================================================
     * GET ALL
     * =====================================================
     */
    @Transactional(readOnly = true)
    public List<ActivityCruiseResponse> getActivities() {

        return activityCruiseRepository
                .findAllByOrderByNameAsc()
                .stream()
                .map(ActivityCruiseMapper::toResponse)
                .toList();
    }

    /*
     * =====================================================
     * GET ACTIVE
     * =====================================================
     */
    @Transactional(readOnly = true)
    public List<ActivityCruiseResponse> getActiveActivities() {

        return activityCruiseRepository
                .findAllByStatusOrderByNameAsc(ActivityCruiseStatus.ACTIVE)
                .stream()
                .map(ActivityCruiseMapper::toResponse)
                .toList();
    }

    /*
     * =====================================================
     * PATCH (UPDATE)
     * =====================================================
     */
    public ActivityCruiseResponse updateActivity(
            UUID id,
            UpdateActivityCruiseRequest request) {

        ActivityCruise activity = findActivity(id);

        if (request.name() != null
                && activityCruiseRepository.existsByNameIgnoreCaseAndIdNot(request.name(), id)) {
            throw new AppException(
                    "Activity name already exists",
                    HttpStatus.CONFLICT);
        }

        String oldPublicId = activity.getImagePublicId();

        ActivityCruiseMapper.updateEntity(activity, request);

        if (request.image() != null && !request.image().isEmpty()) {

            UploadResult uploadResult = fileStorageService.saveMultipart(
                    request.image(),
                    "activity_cruises");

            activity.setImageUrl(uploadResult.getUrl());
            activity.setImagePublicId(uploadResult.getPublicId());

            if (oldPublicId != null && !oldPublicId.isBlank()) {
                fileStorageService.delete(oldPublicId);
            }
        }

        ActivityCruise updatedActivity = activityCruiseRepository.save(activity);

        return ActivityCruiseMapper.toResponse(updatedActivity);
    }

    /*
     * =====================================================
     * DELETE
     * =====================================================
     */
    public void deleteActivity(UUID id) {

        ActivityCruise activity = findActivity(id);

        if (activity.getImagePublicId() != null && !activity.getImagePublicId().isBlank()) {
            fileStorageService.delete(activity.getImagePublicId());
        }

        activityCruiseRepository.delete(activity);
    }

    /*
     * =====================================================
     * FIND HELPER
     * =====================================================
     */
    private ActivityCruise findActivity(UUID id) {

        return activityCruiseRepository
                .findById(id)
                .orElseThrow(() -> new AppException(
                        "Activity cruise not found",
                        HttpStatus.NOT_FOUND));
    }
}