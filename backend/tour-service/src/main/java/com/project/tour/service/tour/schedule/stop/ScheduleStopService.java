package com.project.tour.service.tour.schedule.stop;

import com.project.tour.dto.tour.schedule.stop.CreateScheduleStopRequest;
import com.project.tour.dto.tour.schedule.stop.ScheduleStopResponse;
import com.project.tour.dto.tour.schedule.stop.UpdateScheduleStopRequest;
import com.project.tour.exception.AppException;
import com.project.tour.mapper.tour.schedule.ScheduleStopMapper;
import com.project.tour.model.Port;
import com.project.tour.model.Schedule;
import com.project.tour.model.ScheduleStop;
import com.project.tour.repository.PortRepository;
import com.project.tour.repository.tour.schedule.ScheduleRepository;
import com.project.tour.repository.tour.schedule.ScheduleStopRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.project.tour.model.Tour;
import com.project.tour.model.enums.tour.TourStatusTrip;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ScheduleStopService {

        private final ScheduleStopRepository scheduleStopRepository;
        private final ScheduleRepository scheduleRepository;
        private final PortRepository portRepository;

        public ScheduleStopService(
                        ScheduleStopRepository scheduleStopRepository,
                        ScheduleRepository scheduleRepository,
                        PortRepository portRepository) {

                this.scheduleStopRepository = scheduleStopRepository;
                this.scheduleRepository = scheduleRepository;
                this.portRepository = portRepository;
        }

        private void validateTourIsDraft(Schedule schedule) {

                Tour tour = schedule.getTour();

                if (tour == null) {
                        throw new AppException(
                                        "Schedule is not associated with a tour",
                                        HttpStatus.BAD_REQUEST);
                }

                if (tour.getStatusTrip() != TourStatusTrip.DRAFT) {
                        throw new AppException(
                                        "Tour must be in DRAFT status to modify schedule stop",
                                        HttpStatus.BAD_REQUEST);
                }
        }

        public ScheduleStopResponse create(
                        UUID scheduleId,
                        CreateScheduleStopRequest request) {

                Schedule schedule = findSchedule(scheduleId);
                validateTourIsDraft(schedule);

                if (request.getLeaveAt().isBefore(request.getArriveAt())
                                || request.getLeaveAt().isEqual(request.getArriveAt())) {

                        throw new AppException(
                                        "Leave time must be after arrival time",
                                        HttpStatus.BAD_REQUEST);
                }

                if (scheduleStopRepository
                                .existsBySchedule_IdAndStopOrder(
                                                scheduleId,
                                                request.getStopOrder())) {

                        throw new AppException(
                                        "Stop order already exists in this schedule",
                                        HttpStatus.CONFLICT);
                }

                Port port = findPort(request.getPortId());

                ScheduleStop stop = ScheduleStopMapper.toEntity(
                                request,
                                schedule,
                                port);

                ScheduleStop saved = scheduleStopRepository.save(stop);

                return ScheduleStopMapper.toResponse(saved);
        }

        @Transactional(readOnly = true)
        public ScheduleStopResponse getById(
                        UUID scheduleId,
                        UUID stopId) {

                ScheduleStop stop = findById(
                                scheduleId,
                                stopId);

                return ScheduleStopMapper.toResponse(stop);
        }

        @Transactional(readOnly = true)
        public List<ScheduleStopResponse> getAll(
                        UUID scheduleId) {

                findSchedule(scheduleId);

                return scheduleStopRepository
                                .findAllBySchedule_IdOrderByStopOrderAsc(scheduleId)
                                .stream()
                                .map(ScheduleStopMapper::toResponse)
                                .toList();
        }

        public ScheduleStopResponse update(
                        UUID scheduleId,
                        UUID stopId,
                        UpdateScheduleStopRequest request) {

                ScheduleStop stop = findById(
                                scheduleId,
                                stopId);

                validateTourIsDraft(stop.getSchedule());
                if (request.getLeaveAt().isBefore(request.getArriveAt())
                                || request.getLeaveAt().isEqual(request.getArriveAt())) {

                        throw new AppException(
                                        "Leave time must be after arrival time",
                                        HttpStatus.BAD_REQUEST);
                }

                if (scheduleStopRepository
                                .existsBySchedule_IdAndStopOrderAndIdNot(
                                                scheduleId,
                                                request.getStopOrder(),
                                                stopId)) {

                        throw new AppException(
                                        "Stop order already exists in this schedule",
                                        HttpStatus.CONFLICT);
                }

                Port port = findPort(request.getPortId());

                ScheduleStopMapper.updateEntity(
                                stop,
                                request,
                                port);

                ScheduleStop updated = scheduleStopRepository.save(stop);

                return ScheduleStopMapper.toResponse(updated);
        }

        public void delete(
                        UUID scheduleId,
                        UUID stopId) {

                ScheduleStop stop = findById(
                                scheduleId,
                                stopId);

                validateTourIsDraft(stop.getSchedule());
                scheduleStopRepository.delete(stop);
        }

        private Schedule findSchedule(UUID scheduleId) {

                return scheduleRepository.findById(scheduleId)
                                .orElseThrow(() -> new AppException(
                                                "Schedule not found",
                                                HttpStatus.NOT_FOUND));
        }

        private Port findPort(UUID portId) {

                return portRepository.findById(portId)
                                .orElseThrow(() -> new AppException(
                                                "Port not found",
                                                HttpStatus.NOT_FOUND));
        }

        private ScheduleStop findById(
                        UUID scheduleId,
                        UUID stopId) {

                return scheduleStopRepository
                                .findByIdAndSchedule_Id(
                                                stopId,
                                                scheduleId)
                                .orElseThrow(() -> new AppException(
                                                "Schedule stop not found",
                                                HttpStatus.NOT_FOUND));
        }
}