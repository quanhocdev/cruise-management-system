package com.project.tour.service.internal;

import com.project.tour.dto.internal.VisitTourUsageInfo;
import com.project.tour.model.enums.tour.BenefitType;
import com.project.tour.model.operation.PackageBenefit;
import com.project.tour.model.shore.VisitTour;
import com.project.tour.repository.shore.VisitTourRepository;
import com.project.tour.repository.tour.PackageBenefitRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.project.tour.dto.internal.VisitTourUsageManagementInfo;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.math.BigDecimal;
import java.util.UUID;

@Service
public class VisitTourInternalServiceImpl
                implements VisitTourInternalService {

        private final VisitTourRepository visitTourRepository;
        private final PackageBenefitRepository packageBenefitRepository;

        public VisitTourInternalServiceImpl(
                        VisitTourRepository visitTourRepository,
                        PackageBenefitRepository packageBenefitRepository) {

                this.visitTourRepository = visitTourRepository;
                this.packageBenefitRepository = packageBenefitRepository;
        }

        @Override
        @Transactional(readOnly = true)
        public VisitTourUsageInfo getVisitTourUsageInfo(
                        UUID visitTourId,
                        UUID tourPackageId) {

                VisitTour visitTour = visitTourRepository
                                .findById(visitTourId)
                                .orElseThrow(() -> new ResponseStatusException(
                                                HttpStatus.NOT_FOUND,
                                                "Không tìm thấy VisitTour"));

                PackageBenefit benefit = packageBenefitRepository
                                .findAllByTourPackageId(tourPackageId)
                                .stream()
                                .filter(item -> item.getType() != null
                                                && item.getType() == BenefitType.ACTIVITY_VISIT
                                                && visitTourId.equals(item.getReferenceId()))
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

                return new VisitTourUsageInfo(
                                visitTour.getId(),
                                visitTour.getTourId(),
                                visitTour.getScheduleStopId(),
                                visitTour.getName(),
                                visitTour.getStartTime(),
                                visitTour.getEndTime(),
                                visitTour.getMaxPassengers(),
                                visitTour.getPrice(),
                                visitTour.getStatus(),
                                benefit != null
                                                ? benefit.getId()
                                                : null,
                                benefitQuantity,
                                discountPercent);
        }

        @Override
        @Transactional(readOnly = true)
        public List<VisitTourUsageManagementInfo> getVisitTourUsageManagementInfo(
                        List<UUID> visitTourIds) {

                if (visitTourIds == null || visitTourIds.isEmpty()) {
                        return List.of();
                }

                Map<UUID, VisitTour> visitTourMap = visitTourRepository
                                .findAllById(visitTourIds)
                                .stream()
                                .collect(Collectors.toMap(
                                                VisitTour::getId,
                                                Function.identity()));

                return visitTourIds.stream()
                                .distinct()
                                .map(visitTourMap::get)
                                .filter(java.util.Objects::nonNull)
                                .map(visitTour -> new VisitTourUsageManagementInfo(
                                                visitTour.getId(),
                                                visitTour.getTourId(),
                                                visitTour.getName(),
                                                visitTour.getStartTime(),
                                                visitTour.getEndTime(),
                                                visitTour.getMaxPassengers(),
                                                visitTour.getPrice(),
                                                visitTour.getStatus()))
                                .toList();
        }

}