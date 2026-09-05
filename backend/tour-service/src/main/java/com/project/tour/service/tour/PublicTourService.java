package com.project.tour.service.tour;

import com.project.tour.dto.tour.PublicTourDetailResponse;
import com.project.tour.dto.tour.PublicTourSummaryResponse;
import com.project.tour.mapper.tour.TourPublicMapper;
import com.project.tour.model.*;
import com.project.tour.model.enums.tour.TourStatusTrip;
import com.project.tour.repository.tour.*;
import com.project.tour.repository.tour.schedule.ScheduleRepository;
import com.project.tour.repository.tour.schedule.ScheduleStopRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class PublicTourService {

    private final TourRepository tourRepository;
    private final TourPackageRepository tourPackageRepository;
    private final PackageBenefitRepository packageBenefitRepository;
    private final ScheduleRepository scheduleRepository;
    private final ScheduleStopRepository scheduleStopRepository;
    private final AssignmentActivityVisitRepository assignmentActivityVisitRepository;
    private final AssignmentActivityCruiseRepository assignmentActivityCruiseRepository;
    private final AssignmentProductRepository assignmentProductRepository;
    private final AssignmentServiceRepository assignmentServiceRepository;

    public PublicTourService(
            TourRepository tourRepository,
            TourPackageRepository tourPackageRepository,
            PackageBenefitRepository packageBenefitRepository,
            ScheduleRepository scheduleRepository,
            ScheduleStopRepository scheduleStopRepository,
            AssignmentActivityVisitRepository assignmentActivityVisitRepository,
            AssignmentActivityCruiseRepository assignmentActivityCruiseRepository,
            AssignmentProductRepository assignmentProductRepository,
            AssignmentServiceRepository assignmentServiceRepository) {
        this.tourRepository = tourRepository;
        this.tourPackageRepository = tourPackageRepository;
        this.packageBenefitRepository = packageBenefitRepository;
        this.scheduleRepository = scheduleRepository;
        this.scheduleStopRepository = scheduleStopRepository;
        this.assignmentActivityVisitRepository = assignmentActivityVisitRepository;
        this.assignmentActivityCruiseRepository = assignmentActivityCruiseRepository;
        this.assignmentProductRepository = assignmentProductRepository;
        this.assignmentServiceRepository = assignmentServiceRepository;
    }

    // 1. Lấy danh sách tour tóm tắt cho trang chủ
    public List<PublicTourSummaryResponse> getPublicTourSummaries() {
        List<Tour> tours = tourRepository.findByStatusTripIn(
                List.of(TourStatusTrip.READY, TourStatusTrip.APPROVED));

        return tours.stream().map(tour -> {
            BigDecimal startingPrice = tourPackageRepository.findLowestPriceByTourId(tour.getId());
            return TourPublicMapper.toSummaryResponse(tour, startingPrice);
        }).toList();
    }

    // 2. Lấy chi tiết đầy đủ của một tour
    public PublicTourDetailResponse getPublicTourDetail(UUID tourId) {
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy Tour"));

        // Lấy danh sách lịch trình
        List<Schedule> schedules = scheduleRepository.findAllByTour_IdOrderByDayNumberAsc(tourId);

        // Map Schedule ID sang danh sách ScheduleStop
        Map<UUID, List<ScheduleStop>> scheduleIdToStopsMap = schedules.stream()
                .collect(Collectors.toMap(
                        Schedule::getId,
                        s -> scheduleStopRepository.findAllBySchedule_IdOrderByStopOrderAsc(s.getId())));

        // Map ScheduleStop ID sang AssignmentActivityVisit
        Map<UUID, AssignmentActivityVisit> stopIdToVisitMap = assignmentActivityVisitRepository
                .findAllByTourIdOrderByCreatedAtAsc(tourId)
                .stream()
                .collect(Collectors.toMap(
                        AssignmentActivityVisit::getScheduleStopId,
                        v -> v,
                        (v1, v2) -> v1));

        // Lấy danh sách gói tour & benefits
        List<TourPackage> packages = tourPackageRepository.findAllByTourId(tourId);
        Map<UUID, List<PackageBenefit>> packageIdToBenefitsMap = packages.stream()
                .collect(Collectors.toMap(
                        TourPackage::getId,
                        pkg -> packageBenefitRepository.findAllByTourPackageId(pkg.getId())));

        // Lấy các assignment khác trên tàu
        List<AssignmentActivityCruise> onboardActivities = assignmentActivityCruiseRepository.findAllByTourId(tourId);
        List<AssignmentProduct> products = assignmentProductRepository.findAllByTourIdOrderByCreatedAtAsc(tourId);
        List<AssignmentService> services = assignmentServiceRepository.findAllByTourIdOrderByCreatedAtAsc(tourId);

        // Ủy quyền toàn bộ việc lắp ráp dữ liệu sang Mapper
        return TourPublicMapper.toDetailResponse(
                tour,
                schedules,
                scheduleIdToStopsMap,
                stopIdToVisitMap,
                packages,
                packageIdToBenefitsMap,
                onboardActivities,
                products,
                services);
    }
}