package com.project.booking.service;

import com.project.booking.dto.ActivityCruiseUsageRequest;
import com.project.booking.dto.ActivityCruiseUsageResponse;
import com.project.booking.mapper.ActivityCruiseUsageMapper;
import com.project.booking.model.ActivityCruiseUsage;
import com.project.booking.repository.ActivityCruiseUsageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ActivityCruiseUsageServiceImpl
        implements ActivityCruiseUsageService {

    private final ActivityCruiseUsageRepository
            activityCruiseUsageRepository;

    private final ActivityCruiseUsageMapper
            activityCruiseUsageMapper;

    public ActivityCruiseUsageServiceImpl(
            ActivityCruiseUsageRepository activityCruiseUsageRepository,
            ActivityCruiseUsageMapper activityCruiseUsageMapper) {

        this.activityCruiseUsageRepository =
                activityCruiseUsageRepository;

        this.activityCruiseUsageMapper =
                activityCruiseUsageMapper;
    }

    @Override
    @Transactional
    public ActivityCruiseUsageResponse create(
            ActivityCruiseUsageRequest request) {

        throw new UnsupportedOperationException(
                "ActivityCruiseUsage create chưa được implement.");
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActivityCruiseUsageResponse> getByBookingPassengerId(
            Long bookingPassengerId) {

        return activityCruiseUsageRepository
                .findByBookingPassengerId(bookingPassengerId)
                .stream()
                .map(activityCruiseUsageMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActivityCruiseUsageResponse> getByUserId(
            Long userId) {

        throw new UnsupportedOperationException(
                "getByUserId chưa được implement.");
    }
}