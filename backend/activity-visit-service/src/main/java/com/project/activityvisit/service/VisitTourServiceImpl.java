package com.project.activityvisit.service;

import com.project.activityvisit.dto.CreateVisitTourRequest;
import com.project.activityvisit.dto.UpdateVisitTourRequest;
import com.project.activityvisit.dto.VisitTourResponse;
import com.project.activityvisit.exception.AppException;
import com.project.activityvisit.mapper.VisitTourMapper;
import com.project.activityvisit.model.VisitTour;
import com.project.activityvisit.model.enums.VisitTourStatus;
import com.project.activityvisit.repository.VisitTourRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class VisitTourServiceImpl implements VisitTourService {

        private final VisitTourRepository visitTourRepository;
        private final VisitTourValidator validator;

        public VisitTourServiceImpl(
                        VisitTourRepository visitTourRepository,
                        VisitTourValidator validator) {

                this.visitTourRepository = visitTourRepository;
                this.validator = validator;
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

                /*
                 * Kafka hiện tại chỉ gửi:
                 *
                 * - tourId
                 * - scheduleStopId
                 *
                 * Vì vậy các thông tin cấu hình vẫn để null:
                 *
                 * - name
                 * - description
                 * - startTime
                 * - endTime
                 * - maxPassengers
                 * - price
                 *
                 * status mặc định = WAITING_CONFIG
                 */

                VisitTour saved = visitTourRepository.save(visitTour);

                return VisitTourMapper.toResponse(saved);
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