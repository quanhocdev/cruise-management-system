package com.project.tour.dto.tour;

import com.project.tour.model.enums.tour.TourBookingStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record PublicTourDetailResponse(
        UUID id,
        String code,
        String name,
        String description,
        LocalDate startDate,
        LocalDate endDate,
        TourBookingStatus statusBooking,
        LocalDateTime bookingStart,
        LocalDateTime bookingEnd,

        // 1. Thông tin du thuyền
        CruiseDetailRecord cruise,

        // 2. Lịch trình
        List<ScheduleDetailRecord> schedules,

        // 3. Các gói tour & Quyền lợi bên trong
        List<TourPackageRecord> packages,

        // 4. Các hoạt động giải trí trên tàu (AssignmentActivityCruise)
        List<OnboardActivityRecord> onboardActivities,

        // 5. Sản phẩm đi kèm (AssignmentProduct)
        List<ProductRecord> products,

        // 6. Dịch vụ tiện ích (AssignmentService)
        List<ServiceRecord> services) {
    public record CruiseDetailRecord(
            UUID id,
            String name,
            String code,
            String description,
            Integer maxPassengers,
            String imageUrl) {
    }

    public record ScheduleDetailRecord(
            UUID id,
            String name,
            String description,
            Integer dayNumber,
            LocalDate realDay,
            List<ScheduleStopRecord> stops) {
    }

    public record ScheduleStopRecord(
            UUID id,
            Integer stopOrder,
            LocalDateTime arriveAt,
            LocalDateTime leaveAt,
            String portName,
            String portCity,
            String portCountry,
            String portDescription,
            VisitActivityRecord visitActivity // Gắn AssignmentActivityVisit theo điểm dừng
    ) {
    }

    public record VisitActivityRecord(
            UUID id,
            String visitName,
            String visitDescription,
            LocalDateTime startTime,
            LocalDateTime endTime,
            BigDecimal price,
            Integer maxPassengers) {
    }

    public record TourPackageRecord(
            UUID id,
            String name,
            String description,
            BigDecimal price,
            Integer maxPassengers,
            List<PackageBenefitRecord> benefits) {
    }

    public record PackageBenefitRecord(
            UUID id,
            String type,
            UUID referenceId,
            Integer quantity,
            BigDecimal discountPercent) {
    }

    public record OnboardActivityRecord(
            UUID id,
            String activityName,
            String activityDescription,
            LocalDateTime startTime,
            LocalDateTime endTime,
            Integer maxPassengers,
            BigDecimal price,
            String imageUrl) {
    }

    public record ProductRecord(
            UUID id,
            String productName,
            String productDescription,
            BigDecimal price,
            Integer quantity,
            String imageUrl) {
    }

    public record ServiceRecord(
            UUID id,
            String serviceName,
            String serviceDescription,
            BigDecimal price,
            Integer maxPassengers,
            Integer durationMinutes,
            String imageUrl) {
    }
}