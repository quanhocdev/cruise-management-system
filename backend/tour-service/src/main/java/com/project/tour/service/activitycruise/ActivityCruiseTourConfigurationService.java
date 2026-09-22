package com.project.tour.service.activitycruise;

import com.project.tour.exception.AppException;
import com.project.tour.model.activitycruise.ActivityCruiseTour;
import com.project.tour.model.activitycruise.HistoryActivityCruiseTour;
import com.project.tour.model.activitycruise.enums.ActivityCruiseTourStatus;
import com.project.tour.repository.activitycruise.ActivityCruiseTourAssignmentRepository;
import com.project.tour.repository.activitycruise.HistoryActivityCruiseTourRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ActivityCruiseTourConfigurationService {

    private final ActivityCruiseTourAssignmentRepository assignmentRepository;
    private final HistoryActivityCruiseTourRepository historyRepository;

    public ActivityCruiseTourConfigurationService(
            ActivityCruiseTourAssignmentRepository assignmentRepository,
            HistoryActivityCruiseTourRepository historyRepository) {

        this.assignmentRepository = assignmentRepository;
        this.historyRepository = historyRepository;
    }

    /**
     * Hoàn thành cấu hình tất cả ActivityCruiseTour của một Tour.
     *
     * Chỉ được hoàn thành khi:
     * - Tour có ActivityCruiseTour configuration.
     * - Tất cả configuration đều ở trạng thái CONFIGURED.
     * - Chưa từng hoàn thành configuration trước đó.
     *
     * Sau khi hoàn thành, lưu lịch sử configuration.
     */
    public void complete(UUID tourId) {

        // =====================================================
        // 1. KIỂM TRA ĐÃ HOÀN THÀNH CHƯA
        // =====================================================

        if (historyRepository.existsByTourId(tourId)) {

            throw new AppException(
                    "Activity cruise tour configuration has already been completed",
                    HttpStatus.BAD_REQUEST);
        }

        // =====================================================
        // 2. LẤY CẤU HÌNH
        // =====================================================

        List<ActivityCruiseTour> assignments =
                assignmentRepository.findAllByTourIdOrderByCreatedAtAsc(tourId);

        if (assignments.isEmpty()) {

            throw new AppException(
                    "No activity cruise tour configuration found for tour",
                    HttpStatus.NOT_FOUND);
        }

        // =====================================================
        // 3. KIỂM TRA TẤT CẢ CONFIGURED
        // =====================================================

        for (ActivityCruiseTour assignment : assignments) {

            if (assignment.getStatus() != ActivityCruiseTourStatus.CONFIGURED) {

                throw new AppException(
                        "All activity cruise tours must be CONFIGURED before completing configuration",
                        HttpStatus.BAD_REQUEST);
            }

            if (assignment.getActivityCruise() == null) {

                throw new AppException(
                        "Activity cruise configuration is missing",
                        HttpStatus.BAD_REQUEST);
            }
        }

        // =====================================================
        // 4. LƯU LỊCH SỬ HOÀN THÀNH
        // =====================================================

        HistoryActivityCruiseTour history =
                new HistoryActivityCruiseTour();

        history.setTourId(tourId);
        history.setTotalConfigurations(assignments.size());

        historyRepository.save(history);
    }
}
