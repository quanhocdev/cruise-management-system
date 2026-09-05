package com.project.tour.mapper.tour;

import com.project.tour.dto.tour.PublicTourDetailResponse;
import com.project.tour.dto.tour.PublicTourSummaryResponse;
import com.project.tour.model.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TourPublicMapper {

    // 1. Map danh sách tóm tắt cho trang chủ
    public static PublicTourSummaryResponse toSummaryResponse(Tour tour, BigDecimal startingPrice) {
        if (tour == null)
            return null;

        String cruiseName = tour.getCruise() != null ? tour.getCruise().getName() : null;
        String cruiseImageUrl = tour.getCruise() != null ? tour.getCruise().getImageUrl() : null;

        return new PublicTourSummaryResponse(
                tour.getId(),
                tour.getCode(),
                tour.getName(),
                tour.getDescription(),
                tour.getStartDate(),
                tour.getEndDate(),
                cruiseName,
                cruiseImageUrl,
                tour.getStatusBooking(),
                tour.getBookingStart(),
                tour.getBookingEnd(),
                startingPrice);
    }

    // 2. Map chi tiết đầy đủ cho trang product page
    public static PublicTourDetailResponse toDetailResponse(
            Tour tour,
            List<Schedule> schedules,
            Map<UUID, List<ScheduleStop>> scheduleIdToStopsMap,
            Map<UUID, AssignmentActivityVisit> stopIdToVisitMap,
            List<TourPackage> packages,
            Map<UUID, List<PackageBenefit>> packageIdToBenefitsMap,
            List<AssignmentActivityCruise> onboardActivities,
            List<AssignmentProduct> products,
            List<AssignmentService> services) {
        if (tour == null)
            return null;

        // Cruise
        Cruise cruise = tour.getCruise();
        PublicTourDetailResponse.CruiseDetailRecord cruiseRecord = cruise != null
                ? new PublicTourDetailResponse.CruiseDetailRecord(
                        cruise.getId(),
                        cruise.getName(),
                        cruise.getCode(),
                        cruise.getDescription(),
                        cruise.getMaxPassengers(),
                        cruise.getImageUrl())
                : null;

        // Schedules & Stops & Visit Activities
        List<PublicTourDetailResponse.ScheduleDetailRecord> scheduleRecords = schedules.stream().map(schedule -> {
            List<ScheduleStop> stops = scheduleIdToStopsMap.getOrDefault(schedule.getId(), List.of());

            List<PublicTourDetailResponse.ScheduleStopRecord> stopRecords = stops.stream().map(stop -> {
                AssignmentActivityVisit visit = stopIdToVisitMap.get(stop.getId());
                PublicTourDetailResponse.VisitActivityRecord visitRecord = visit != null
                        ? new PublicTourDetailResponse.VisitActivityRecord(
                                visit.getId(),
                                visit.getVisitName(),
                                visit.getVisitDescription(),
                                visit.getStartTime(),
                                visit.getEndTime(),
                                visit.getPrice(),
                                visit.getMaxPassengers())
                        : null;

                Port port = stop.getPort();
                return new PublicTourDetailResponse.ScheduleStopRecord(
                        stop.getId(),
                        stop.getStopOrder(),
                        stop.getArriveAt(),
                        stop.getLeaveAt(),
                        port != null ? port.getName() : null,
                        port != null ? port.getCity() : null,
                        port != null ? port.getCountry() : null,
                        port != null ? port.getDescription() : null,
                        visitRecord);
            }).toList();

            return new PublicTourDetailResponse.ScheduleDetailRecord(
                    schedule.getId(),
                    schedule.getName(),
                    schedule.getDescription(),
                    schedule.getDayNumber(),
                    schedule.getRealDay(),
                    stopRecords);
        }).toList();

        // Packages & Benefits
        List<PublicTourDetailResponse.TourPackageRecord> packageRecords = packages.stream().map(pkg -> {
            List<PackageBenefit> benefits = packageIdToBenefitsMap.getOrDefault(pkg.getId(), List.of());
            List<PublicTourDetailResponse.PackageBenefitRecord> benefitRecords = benefits.stream()
                    .map(benefit -> new PublicTourDetailResponse.PackageBenefitRecord(
                            benefit.getId(),
                            benefit.getType() != null ? benefit.getType().name() : null,
                            benefit.getReferenceId(),
                            benefit.getQuantity(),
                            benefit.getDiscountPercent()))
                    .toList();

            return new PublicTourDetailResponse.TourPackageRecord(
                    pkg.getId(),
                    pkg.getName(),
                    pkg.getDescription(),
                    pkg.getPrice(),
                    pkg.getMaxPassengers(),
                    benefitRecords);
        }).toList();

        // Onboard Activities
        List<PublicTourDetailResponse.OnboardActivityRecord> onboardRecords = onboardActivities.stream()
                .map(a -> new PublicTourDetailResponse.OnboardActivityRecord(
                        a.getId(), a.getActivityName(), a.getActivityDescription(),
                        a.getStartTime(), a.getEndTime(), a.getMaxPassengers(), a.getPrice(), a.getImageUrl()))
                .toList();

        // Products
        List<PublicTourDetailResponse.ProductRecord> productRecords = products.stream()
                .map(p -> new PublicTourDetailResponse.ProductRecord(
                        p.getId(), p.getProductName(), p.getProductDescription(),
                        p.getPrice(), p.getQuantity(), p.getImageUrl()))
                .toList();

        // Services
        List<PublicTourDetailResponse.ServiceRecord> serviceRecords = services.stream()
                .map(s -> new PublicTourDetailResponse.ServiceRecord(
                        s.getId(), s.getServiceName(), s.getServiceDescription(),
                        s.getPrice(), s.getMaxPassengers(), s.getDurationMinutes(), s.getImageUrl()))
                .toList();

        return new PublicTourDetailResponse(
                tour.getId(),
                tour.getCode(),
                tour.getName(),
                tour.getDescription(),
                tour.getStartDate(),
                tour.getEndDate(),
                tour.getStatusBooking(),
                tour.getBookingStart(),
                tour.getBookingEnd(),
                cruiseRecord,
                scheduleRecords,
                packageRecords,
                onboardRecords,
                productRecords,
                serviceRecords);
    }
}