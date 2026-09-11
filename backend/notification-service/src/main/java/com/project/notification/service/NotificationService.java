package com.project.notification.service;

import com.project.common.event.TourApprovedEvent;
import com.project.common.event.TourAssignmentEvent;
import com.project.notification.dto.NotificationApprovalTourResponse;
import com.project.notification.model.NotificationApprovalTour;
import com.project.notification.repository.NotificationApprovalTourRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationService {

    private final NotificationApprovalTourRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public NotificationService(NotificationApprovalTourRepository notificationRepository,
            SimpMessagingTemplate messagingTemplate) {
        this.notificationRepository = notificationRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @Transactional
    public void processTourApproved(TourApprovedEvent event) {
        for (TourAssignmentEvent assignment : event.assignments()) {
            // 1. Tự động sinh actionLink dựa vào type phân công
            String actionLink = generateActionLink(assignment);
            String title = "Phân công mới cho Tour";
            String message = String.format("Bạn có một phân công mới thuộc loại [%s] trong tour.", assignment.type());

            // 2. Lưu vào Database với model NotificationApprovalTour
            NotificationApprovalTour notification = new NotificationApprovalTour();
            notification.setTourId(assignment.tourId());
            notification.setTargetId(assignment.targetId());
            notification.setAssignmentType(assignment.type().name());
            notification.setTitle(title);
            notification.setMessage(message);
            notification.setActionLink(actionLink);
            notification.setRead(false);

            NotificationApprovalTour savedNotification = notificationRepository.save(notification);

            // 3. Đóng gói đúng DTO NotificationApprovalTourResponse trả về cho WebSocket
            NotificationApprovalTourResponse response = new NotificationApprovalTourResponse(
                    savedNotification.getId(),
                    savedNotification.getTourId(),
                    savedNotification.getTargetId(),
                    savedNotification.getAssignmentType(),
                    savedNotification.getTitle(),
                    savedNotification.getMessage(),
                    savedNotification.getActionLink(),
                    savedNotification.isRead(),
                    savedNotification.getCreatedAt());

            // 4. Bắn WebSocket realtime tới client theo kênh phân loại type
            messagingTemplate.convertAndSend("/topic/notifications/" + assignment.type().name().toLowerCase(),
                    response);
        }
    }

    private String generateActionLink(TourAssignmentEvent assignment) {
        switch (assignment.type()) {
            case PRODUCT:
                return String.format("/tours/%s/products", assignment.tourId());
            case SERVICE:
                return String.format("/tours/%s/services", assignment.tourId());
            case ACTIVITY_CRUISE:
                return String.format("/tours/%s/cruise-activities", assignment.tourId());
            case ACTIVITY_VISIT:
                return String.format("/tours/%s/schedules", assignment.tourId());
            default:
                return String.format("/tours/%s/overview", assignment.tourId());
        }
    }
}