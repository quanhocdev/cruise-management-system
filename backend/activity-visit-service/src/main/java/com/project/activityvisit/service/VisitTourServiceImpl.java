package com.project.activityvisit.service;

import com.project.activityvisit.dto.CreateVisitTourRequest;
import com.project.activityvisit.dto.TourVisitSyncResponse;
import com.project.activityvisit.dto.UpdateVisitTourRequest;
import com.project.activityvisit.dto.VisitTourResponse;
import com.project.activityvisit.exception.AppException;
import com.project.activityvisit.mapper.TourVisitSyncMapper;
import com.project.activityvisit.mapper.VisitTourMapper;
import com.project.activityvisit.model.ScheduleOfActivityVisit;
import com.project.activityvisit.model.ScheduleStopOfActivityVisit;
import com.project.activityvisit.model.TourOfAcitvityVisit;
import com.project.activityvisit.model.VisitTour;
import com.project.activityvisit.model.enums.VisitTourStatus;
import com.project.activityvisit.repository.TourOfAcitvityVisitRepository;
import com.project.activityvisit.repository.VisitTourRepository;
import com.project.common.event.TourMasterSyncEvent;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class VisitTourServiceImpl implements VisitTourService {

        private final VisitTourRepository visitTourRepository;
        private final VisitTourValidator validator;
        private final TourOfAcitvityVisitRepository tourOfAcitvityVisitRepository;

        public VisitTourServiceImpl(
                        VisitTourRepository visitTourRepository,
                        VisitTourValidator validator,
                        TourOfAcitvityVisitRepository tourOfAcitvityVisitRepository) {

                this.visitTourRepository = visitTourRepository;
                this.validator = validator;
                this.tourOfAcitvityVisitRepository = tourOfAcitvityVisitRepository;
        }

        // =====================================================
        // GET ALL
        // =====================================================

        @Override
        @Transactional(readOnly = true)
        public List<VisitTourResponse> getAll() {

                return visitTourRepository
                                .findAllByOrderByCreatedAtDesc()
                                .stream()
                                .map(VisitTourMapper::toResponse)
                                .toList();
        }

        // =====================================================
        // GET BY ID
        // =====================================================

        @Override
        @Transactional(readOnly = true)
        public VisitTourResponse getById(UUID id) {

                VisitTour visitTour = findById(id);

                return VisitTourMapper.toResponse(visitTour);
        }

        // =====================================================
        // GET BY SCHEDULE STOP
        // =====================================================

        @Override
        @Transactional(readOnly = true)
        public List<VisitTourResponse> getByScheduleStop(
                        UUID scheduleStopId) {

                return visitTourRepository
                                .findAllByScheduleStopIdOrderByStartTimeAsc(
                                                scheduleStopId)
                                .stream()
                                .map(VisitTourMapper::toResponse)
                                .toList();
        }

        // =====================================================
        // GET BY TOUR
        // =====================================================

        @Override
        @Transactional(readOnly = true)
        public List<VisitTourResponse> getByTour(
                        UUID tourId) {

                return visitTourRepository
                                .findAllByTourIdOrderByStartTimeAsc(tourId)
                                .stream()
                                .map(VisitTourMapper::toResponse)
                                .toList();
        }

        // =====================================================
        // CREATE / CONFIGURE VISIT TOUR
        // =====================================================

        @Override
        @Transactional
        public VisitTourResponse create(
                        UUID scheduleStopId,
                        CreateVisitTourRequest request) {

                validator.validateCreate(request);

                // 1. Tìm bản ghi VisitTour đã được sinh sẵn từ sự kiện Kafka dựa vào
                // scheduleStopId
                VisitTour visitTour = visitTourRepository.findByScheduleStopId(scheduleStopId)
                                .orElseThrow(() -> new AppException(
                                                "Visit tour not found for this schedule stop. Please check Kafka event.",
                                                HttpStatus.NOT_FOUND));

                // 2. Cập nhật các thông tin cấu hình từ form Frontend gửi lên
                visitTour.setName(request.name());
                visitTour.setDescription(request.description());
                visitTour.setStartTime(request.startTime());
                visitTour.setEndTime(request.endTime());
                visitTour.setMaxPassengers(request.maxPassengers());
                visitTour.setPrice(request.price());

                // Chuyển trạng thái sang đã cấu hình (hoặc tùy theo logic của bạn)
                visitTour.setStatus(VisitTourStatus.CONFIGURED);

                // 3. Lưu lại bản ghi (tourId đã có sẵn từ bản ghi do Kafka tạo, không bị null
                // nữa)
                VisitTour saved = visitTourRepository.save(visitTour);

                return VisitTourMapper.toResponse(saved);
        }
        // =====================================================
        // UPDATE - PATCH
        // =====================================================

        @Override
        @Transactional
        public VisitTourResponse update(
                        UUID id,
                        UpdateVisitTourRequest request) {

                VisitTour visitTour = findById(id);

                validator.validateUpdate(request);

                VisitTourMapper.updateEntity(
                                visitTour,
                                request);

                VisitTour saved = visitTourRepository.save(visitTour);

                return VisitTourMapper.toResponse(saved);
        }

        // =====================================================
        // KAFKA - CREATE FROM TOUR APPROVED
        // =====================================================

        @Override
        @Transactional
        public VisitTourResponse createVisitTourFromEvent(
                        UUID tourId,
                        UUID scheduleStopId) {

                // =================================================
                // KIỂM TRA DUPLICATE
                // =================================================

                if (visitTourRepository.existsByTourIdAndScheduleStopId(
                                tourId,
                                scheduleStopId)) {

                        throw new AppException(
                                        "VisitTour already exists for this tour and schedule stop",
                                        HttpStatus.CONFLICT);
                }

                // =================================================
                // TẠO VISIT TOUR
                // =================================================

                VisitTour visitTour = new VisitTour();

                visitTour.setTourId(tourId);
                visitTour.setScheduleStopId(scheduleStopId);

                VisitTour saved = visitTourRepository.save(visitTour);

                return VisitTourMapper.toResponse(saved);
        }

        // =====================================================
    // KAFKA - SYNC TOUR MASTER DATA (3 BẢNG: TOUR - SCHEDULE - STOP)
    // =====================================================

    @Override
    @Transactional
    public void syncTourMasterData(TourMasterSyncEvent event) {
        // 1. Tạo hoặc cập nhật Tour chính
        TourOfAcitvityVisit tour = tourOfAcitvityVisitRepository.findById(event.tourId())
                .orElseGet(() -> {
                    TourOfAcitvityVisit newTour = new TourOfAcitvityVisit();
                    newTour.setId(event.tourId());
                    return newTour;
                });

        tour.setCode(event.code());
        tour.setName(event.name());
        tour.setDescription(event.description());
        tour.setStartDate(event.startDate());
        tour.setEndDate(event.endDate());
        tour.setStatusTrip(event.statusTrip());

        // Clear danh sách cũ nếu đồng bộ lại để tránh bị trùng lặp khóa chính
        tour.getSchedules().clear();

        // 2. Map và lưu danh sách Schedules (Lịch trình từng ngày)
        if (event.schedules() != null) {
            List<ScheduleOfActivityVisit> scheduleEntities = new java.util.ArrayList<>();

            for (com.project.common.event.TourMasterSyncEvent.ScheduleDetail sDetail : event.schedules()) {
                ScheduleOfActivityVisit schedule = new ScheduleOfActivityVisit();
                schedule.setId(sDetail.scheduleId());
                schedule.setTour(tour);
                schedule.setName(sDetail.name());
                schedule.setDescription(sDetail.description());
                schedule.setDayNumber(sDetail.dayNumber());
                schedule.setRealDay(sDetail.realDay());
                schedule.setStatus(sDetail.status());

                // 3. Map và lưu danh sách Stops (Điểm dừng & Thời gian cập cảng)
                if (sDetail.stops() != null) {
                    List<ScheduleStopOfActivityVisit> stopEntities = new java.util.ArrayList<>();

                    for (com.project.common.event.TourMasterSyncEvent.ScheduleStopDetail stopDetail : sDetail.stops()) {
                        ScheduleStopOfActivityVisit stop = new ScheduleStopOfActivityVisit();
                        stop.setId(stopDetail.scheduleStopId());
                        stop.setSchedule(schedule);
                        stop.setPortId(stopDetail.portId());
                        stop.setPortName(stopDetail.portName());
                        stop.setStopOrder(stopDetail.stopOrder());
                        stop.setArriveAt(stopDetail.arriveAt());
                        stop.setLeaveAt(stopDetail.leaveAt());

                        stopEntities.add(stop);
                    }
                    schedule.setStops(stopEntities);
                }

                scheduleEntities.add(schedule);
            }
            tour.setSchedules(scheduleEntities);
        }

        // Lưu toàn bộ phân cấp nhờ cấu trúc CascadeType.ALL
        tourOfAcitvityVisitRepository.save(tour);
    }
@Override
@Transactional(readOnly = true)
public TourVisitSyncResponse getMasterTourById(UUID tourId) {
    TourOfAcitvityVisit tour = tourOfAcitvityVisitRepository.findById(tourId)
            .orElseThrow(() -> new AppException("Master tour not found for visit sync", HttpStatus.NOT_FOUND));
    
    return TourVisitSyncMapper.toResponse(tour);
}
@Override
@Transactional(readOnly = true)
public List<TourVisitSyncResponse> getAllMasterTours() {
    return tourOfAcitvityVisitRepository.findAll().stream()
            .map(TourVisitSyncMapper::toResponse)
            .toList();
}
        // =====================================================
        // DELETE
        // =====================================================

        @Override
        @Transactional
        public void delete(UUID id) {

                VisitTour visitTour = findById(id);

                visitTourRepository.delete(visitTour);
        }

        // =====================================================
        // FINDER
        // =====================================================

        private VisitTour findById(UUID id) {

                return visitTourRepository
                                .findById(id)
                                .orElseThrow(() -> new AppException(
                                                "Visit tour not found",
                                                HttpStatus.NOT_FOUND));
        }
}