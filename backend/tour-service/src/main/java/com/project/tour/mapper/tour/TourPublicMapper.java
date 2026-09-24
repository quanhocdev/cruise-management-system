package com.project.tour.mapper.tour;

import com.project.tour.dto.tour.PublicTourDetailResponse;
import com.project.tour.dto.tour.PublicTourSummaryResponse;
import com.project.tour.model.*;
import com.project.tour.model.activitycruise.ActivityCruiseTour;
import com.project.tour.model.activityvisit.VisitTour;
import com.project.tour.model.convenience.product.ProductTour;
import com.project.tour.model.convenience.service.ServiceTour;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TourPublicMapper {

        // 1. Map danh sách tóm tắt cho trang chủ
        public static PublicTourSummaryResponse toSummaryResponse(
                        Tour tour,
                        BigDecimal startingPrice) {

                if (tour == null)
                        return null;

                String cruiseName = tour.getCruise() != null
                                ? tour.getCruise().getName()
                                : null;

                String cruiseImageUrl = tour.getCruise() != null
                                ? tour.getCruise().getImageUrl()
                                : null;

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
                                tour.getStatusTrip(),
                                tour.getBookingStart(),
                                tour.getBookingEnd(),
                                startingPrice);
        }

        // 2. Map chi tiết đầy đủ cho trang product page
        public static PublicTourDetailResponse toDetailResponse(
                        Tour tour,
                        List<Schedule> schedules,
                        Map<UUID, List<ScheduleStop>> scheduleIdToStopsMap,
                        Map<UUID, VisitTour> stopIdToVisitMap,
                        List<TourPackage> packages,
                        Map<UUID, List<PackageBenefit>> packageIdToBenefitsMap,
                        List<ActivityCruiseTour> onboardActivities,
                        List<ProductTour> products,
                        List<ServiceTour> services) {

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
                List<PublicTourDetailResponse.ScheduleDetailRecord> scheduleRecords = schedules.stream()
                                .map(schedule -> {

                                        List<ScheduleStop> stops = scheduleIdToStopsMap.getOrDefault(
                                                        schedule.getId(),
                                                        List.of());

                                        List<PublicTourDetailResponse.ScheduleStopRecord> stopRecords = stops.stream()
                                                        .map(stop -> {

                                                                VisitTour visit = stopIdToVisitMap.get(stop.getId());

                                                                PublicTourDetailResponse.VisitActivityRecord visitRecord = visit != null
                                                                                ? new PublicTourDetailResponse.VisitActivityRecord(
                                                                                                visit.getId(),
                                                                                                visit.getName(),
                                                                                                visit.getDescription(),
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
                                                                                port != null ? port.getDescription()
                                                                                                : null,
                                                                                visitRecord);
                                                        })
                                                        .toList();

                                        return new PublicTourDetailResponse.ScheduleDetailRecord(
                                                        schedule.getId(),
                                                        schedule.getName(),
                                                        schedule.getDescription(),
                                                        schedule.getDayNumber(),
                                                        schedule.getRealDay(),
                                                        stopRecords);
                                })
                                .toList();

                // Packages & Benefits
                List<PublicTourDetailResponse.TourPackageRecord> packageRecords = packages.stream().map(pkg -> {
                        List<PackageBenefit> benefits = packageIdToBenefitsMap.getOrDefault(pkg.getId(), List.of());

                        List<PublicTourDetailResponse.PackageBenefitRecord> benefitRecords = benefits.stream()
                                        .map(benefit -> new PublicTourDetailResponse.PackageBenefitRecord(
                                                        benefit.getId(),
                                                        benefit.getType() != null
                                                                        ? benefit.getType().name()
                                                                        : null,
                                                        benefit.getReferenceId(),
                                                        benefit.getQuantity(),
                                                        benefit.getDiscountPercent()))
                                        .toList();

                        // Lấy capacity trực tiếp từ RoomType liên kết với TourPackage
                        Integer roomCapacity = (pkg.getRoomType() != null)
                                        ? pkg.getRoomType().getCapacity()
                                        : 2;

                        return new PublicTourDetailResponse.TourPackageRecord(
                                        pkg.getId(),
                                        pkg.getName(),
                                        pkg.getDescription(),
                                        pkg.getPrice(),
                                        roomCapacity,
                                        benefitRecords);
                }).toList();

                // Onboard Activities
                List<PublicTourDetailResponse.OnboardActivityRecord> onboardRecords = onboardActivities.stream()
                                .map(a -> new PublicTourDetailResponse.OnboardActivityRecord(
                                                a.getId(),
                                                a.getActivityName(),
                                                a.getActivityDescription(),
                                                a.getStartTime(),
                                                a.getEndTime(),
                                                a.getMaxPassengers(),
                                                a.getPrice(),
                                                a.getImageUrl()))
                                .toList();

                // Products
                List<PublicTourDetailResponse.ProductRecord> productRecords = products.stream()
                                .map(productTour -> {

                                        var product = productTour.getProduct();

                                        return new PublicTourDetailResponse.ProductRecord(
                                                        productTour.getId(),
                                                        product != null
                                                                        ? product.getName()
                                                                        : null,
                                                        product != null
                                                                        ? product.getDescription()
                                                                        : null,
                                                        product != null
                                                                        ? product.getPrice()
                                                                        : null,
                                                        productTour.getQuantity(),
                                                        product != null
                                                                        ? product.getImageUrl()
                                                                        : null);
                                })
                                .toList();

                // Services
                List<PublicTourDetailResponse.ServiceRecord> serviceRecords = services.stream()
                                .map(serviceTour -> {

                                        var service = serviceTour.getService();

                                        return new PublicTourDetailResponse.ServiceRecord(
                                                        serviceTour.getId(),
                                                        service != null
                                                                        ? service.getName()
                                                                        : null,
                                                        service != null
                                                                        ? service.getDescription()
                                                                        : null,
                                                        service != null
                                                                        ? service.getPrice()
                                                                        : null,
                                                        serviceTour.getMaxPassengers(),
                                                        serviceTour.getDurationMinutes(),
                                                        service != null
                                                                        ? service.getImageUrl()
                                                                        : null);
                                })
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