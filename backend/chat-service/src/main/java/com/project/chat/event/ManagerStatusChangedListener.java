
package com.project.chat.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.project.chat.model.enums.ChatDepartment;
import com.project.chat.service.ManagerAssignmentService;
import com.project.common.event.ManagerStatusChangedEvent;

@Component
public class ManagerStatusChangedListener {

    private static final Logger log = LoggerFactory.getLogger(ManagerStatusChangedListener.class);

    private final ManagerAssignmentService assignmentService;

    public ManagerStatusChangedListener(
            ManagerAssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    @KafkaListener(topics = "${app.kafka.topics.manager-status-changed:manager-status-changed-topic}", groupId = "chat-service-group")
    public void handleManagerStatusChanged(
            ManagerStatusChangedEvent event) {

        if (event.userId() == null) {
            log.warn("Bỏ qua ManagerStatusChangedEvent do userId bị null");
            return;
        }

        if (event.roleName() == null || event.roleName().isBlank()) {
            log.warn("Bỏ qua ManagerStatusChangedEvent do roleName bị null hoặc rỗng");
            return;
        }

        ChatDepartment department;

        try {
            department = ChatDepartment.valueOf(
                    event.roleName().trim().toUpperCase());
        } catch (IllegalArgumentException exception) {
            log.warn(
                    "Bỏ qua ManagerStatusChangedEvent do role không hợp lệ: {}",
                    event.roleName());
            return;
        }
        boolean becameInactive = "ACTIVE".equals(event.previousStatus())
                && !"ACTIVE".equals(event.newStatus());

        boolean becameActive = !"ACTIVE".equals(event.previousStatus())
                && "ACTIVE".equals(event.newStatus());

        if (becameInactive) {
            log.info(
                    "Manager {} không còn ACTIVE. Bắt đầu phân công lại conversation của {}",
                    event.userId(),
                    department);

            assignmentService.reassignConversationsFromManager(
                    event.userId());

        } else if (becameActive) {
            log.info(
                    "Manager {} đã ACTIVE. Tìm conversation chưa được phân công của {}",
                    event.userId(),
                    department);

            assignmentService.assignUnassignedConversations(
                    department);
        }
    }
}
