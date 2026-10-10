package com.project.booking.service.finance;

import com.project.booking.model.ActivityCruiseUsage;
import com.project.booking.model.ActivityVisitUsage;
import com.project.booking.model.ProductUsage;
import com.project.booking.model.ServiceUsage;
import com.project.booking.repository.ActivityCruiseUsageRepository;
import com.project.booking.repository.ActivityVisitUsageRepository;
import com.project.booking.repository.ProductUsageRepository;
import com.project.booking.repository.ServiceUsageRepository;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Đọc toàn bộ usage của 1 passenger từ 4 nguồn, dùng chung cho preview và
 * confirm checkout.
 */
@Component
public class PassengerUsageReader {

    public record PassengerUsages(
            List<ActivityVisitUsage> activityVisits,
            List<ActivityCruiseUsage> activityCruises,
            List<ServiceUsage> services,
            List<ProductUsage> products) {
    }

    private final ActivityVisitUsageRepository activityVisitUsageRepository;
    private final ActivityCruiseUsageRepository activityCruiseUsageRepository;
    private final ServiceUsageRepository serviceUsageRepository;
    private final ProductUsageRepository productUsageRepository;

    public PassengerUsageReader(
            ActivityVisitUsageRepository activityVisitUsageRepository,
            ActivityCruiseUsageRepository activityCruiseUsageRepository,
            ServiceUsageRepository serviceUsageRepository,
            ProductUsageRepository productUsageRepository) {
        this.activityVisitUsageRepository = activityVisitUsageRepository;
        this.activityCruiseUsageRepository = activityCruiseUsageRepository;
        this.serviceUsageRepository = serviceUsageRepository;
        this.productUsageRepository = productUsageRepository;
    }

    public PassengerUsages read(Long bookingPassengerId) {
        return new PassengerUsages(
                activityVisitUsageRepository.findAllByBookingPassengerIdOrderByUsedAtDesc(bookingPassengerId),
                activityCruiseUsageRepository.findAllByBookingPassengerIdOrderByUsedAtDesc(bookingPassengerId),
                serviceUsageRepository.findAllByBookingPassenger_IdOrderByUsedAtDesc(bookingPassengerId),
                productUsageRepository.findAllByBookingPassenger_IdOrderByUsedAtDesc(bookingPassengerId));
    }
}