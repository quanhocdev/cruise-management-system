package com.project.tour.service.internal;

import com.project.tour.dto.internal.ActivityCruiseUsageInfo;
import com.project.tour.model.enums.tour.BenefitType;
import com.project.tour.model.onboard.ActivityCruise;
import com.project.tour.model.onboard.ActivityCruiseTour;
import com.project.tour.model.onboard.enums.ActivityCruiseStatus;
import com.project.tour.model.operation.PackageBenefit;
import com.project.tour.repository.onboard.ActivityCruiseTourRepository;
import com.project.tour.repository.tour.PackageBenefitRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class ActivityCruiseTourInternalServiceImpl
        implements ActivityCruiseTourInternalService {

    private final ActivityCruiseTourRepository activityCruiseTourRepository;
    private final PackageBenefitRepository packageBenefitRepository;

    public ActivityCruiseTourInternalServiceImpl(
            ActivityCruiseTourRepository activityCruiseTourRepository,
            PackageBenefitRepository packageBenefitRepository) {

        this.activityCruiseTourRepository = activityCruiseTourRepository;

        this.packageBenefitRepository = packageBenefitRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public ActivityCruiseUsageInfo getActivityCruiseUsageInfo(
            UUID activityCruiseTourId,
            UUID tourPackageId) {

        ActivityCruiseTour activityCruiseTour = activityCruiseTourRepository
                .findById(activityCruiseTourId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy ActivityCruiseTour"));

        ActivityCruise activityCruise = activityCruiseTour.getActivityCruise();

        if (activityCruise == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "ActivityCruiseTour chưa được cấu hình ActivityCruise");
        }

        PackageBenefit benefit = packageBenefitRepository
                .findAllByTourPackageId(tourPackageId)
                .stream()
                .filter(item -> item.getType() != null
                        && item.getType() == BenefitType.ACTIVITY_CRUISE
                        && activityCruiseTourId.equals(
                                item.getReferenceId()))
                .findFirst()
                .orElse(null);

        Integer benefitQuantity = benefit != null
                && benefit.getQuantity() != null
                        ? benefit.getQuantity()
                        : 0;

        BigDecimal discountPercent = benefit != null
                && benefit.getDiscountPercent() != null
                        ? benefit.getDiscountPercent()
                        : BigDecimal.ZERO;

        return new ActivityCruiseUsageInfo(
                activityCruiseTour.getId(),
                activityCruiseTour.getTourId(),
                activityCruise.getId(),
                activityCruiseTour.getActivityName(),
                activityCruiseTour.getStartTime(),
                activityCruiseTour.getEndTime(),
                activityCruiseTour.getMaxPassengers(),
                activityCruiseTour.getPrice(),
                activityCruiseTour.getStatus(),
                activityCruise.getStatus() == ActivityCruiseStatus.ACTIVE,
                benefit != null
                        ? benefit.getId()
                        : null,
                benefitQuantity,
                discountPercent);
    }
}