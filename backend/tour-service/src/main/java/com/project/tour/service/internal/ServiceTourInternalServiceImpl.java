package com.project.tour.service.internal;

import com.project.tour.dto.internal.ServiceUsageInfo;
import com.project.tour.model.convenience.enums.ServiceStatus;
import com.project.tour.model.convenience.service.ServiceTour;
import com.project.tour.model.enums.tour.BenefitType;
import com.project.tour.model.operation.PackageBenefit;
import com.project.tour.repository.convenience.ServiceTourRepository;
import com.project.tour.repository.tour.PackageBenefitRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class ServiceTourInternalServiceImpl
        implements ServiceTourInternalService {

    private final ServiceTourRepository serviceTourRepository;
    private final PackageBenefitRepository packageBenefitRepository;

    public ServiceTourInternalServiceImpl(
            ServiceTourRepository serviceTourRepository,
            PackageBenefitRepository packageBenefitRepository) {

        this.serviceTourRepository = serviceTourRepository;
        this.packageBenefitRepository = packageBenefitRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceUsageInfo getServiceUsageInfo(
            UUID serviceTourId,
            UUID tourPackageId) {

        ServiceTour serviceTour = serviceTourRepository
                .findById(serviceTourId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy ServiceTour"));

        if (serviceTour.getService() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "ServiceTour chưa được cấu hình Service");
        }

        PackageBenefit benefit = packageBenefitRepository
                .findAllByTourPackageId(tourPackageId)
                .stream()
                .filter(item -> item.getType() != null
                        && item.getType() == BenefitType.SERVICE
                        && serviceTourId.equals(item.getReferenceId()))
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

        return new ServiceUsageInfo(
                serviceTour.getId(),
                serviceTour.getTourId(),
                serviceTour.getService().getId(),
                serviceTour.getService().getName(),
                serviceTour.getService().getPrice(),
                serviceTour.getStatus(),
                serviceTour.getService().getStatus() == ServiceStatus.ACTIVE,
                serviceTour.getMaxPassengers(),
                serviceTour.getDurationMinutes(),
                benefit != null ? benefit.getId() : null,
                benefitQuantity,
                discountPercent);
    }
}