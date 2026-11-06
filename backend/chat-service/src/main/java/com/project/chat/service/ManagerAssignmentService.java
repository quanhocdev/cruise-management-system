
package com.project.chat.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.project.chat.model.ChatConversation;
import com.project.chat.model.ConversationParticipant;
import com.project.chat.model.enums.ChatDepartment;
import com.project.chat.model.enums.ConversationStatus;
import com.project.chat.model.enums.ParticipantRole;
import com.project.chat.repository.ChatConversationRepository;
import com.project.chat.repository.ConversationParticipantRepository;

@Service
public class ManagerAssignmentService {

    private final ManagerService managerService;
    private final ConversationParticipantRepository participantRepository;
    private final ChatConversationRepository conversationRepository;

    public ManagerAssignmentService(
            ManagerService managerService,
            ConversationParticipantRepository participantRepository,
            ChatConversationRepository conversationRepository) {
        this.managerService = managerService;
        this.participantRepository = participantRepository;
        this.conversationRepository = conversationRepository;
    }

    /**
     * Phân công Manager cho conversation.
     *
     * required = true:
     * Không có Manager thì báo lỗi để rollback việc tạo conversation.
     *
     * required = false:
     * Không có Manager thì giữ conversation ở trạng thái chưa được phân công.
     */
    @Transactional
    public UUID assignManager(
            ChatConversation conversation,
            boolean required) {

        List<UUID> managers = managerService.getManagerIdsByRoleName(
                conversation.getDepartment().name());

        if (managers.isEmpty()) {
            if (required) {
                throw new ResponseStatusException(
                        HttpStatus.SERVICE_UNAVAILABLE,
                        "Hiện không có quản lý đang hoạt động cho bộ phận này");
            }

            return null;
        }

        UUID selectedManagerId = selectLeastLoadedManager(managers);

        if (selectedManagerId == null) {
            if (required) {
                throw new ResponseStatusException(
                        HttpStatus.SERVICE_UNAVAILABLE,
                        "Không thể xác định quản lý phù hợp");
            }

            return null;
        }

        addManagerParticipant(conversation, selectedManagerId);

        return selectedManagerId;
    }

    /**
     * Chọn Manager có ít conversation ACTIVE nhất.
     * Nếu bằng nhau thì chọn ngẫu nhiên.
     */
    private UUID selectLeastLoadedManager(List<UUID> managers) {

        long minimumCount = Long.MAX_VALUE;
        List<UUID> candidates = new ArrayList<>();

        for (UUID managerId : managers) {
            long count = participantRepository.countConversationsByManager(
                    managerId,
                    ParticipantRole.MANAGER,
                    ConversationStatus.ACTIVE);

            if (count < minimumCount) {
                minimumCount = count;
                candidates.clear();
                candidates.add(managerId);
            } else if (count == minimumCount) {
                candidates.add(managerId);
            }
        }

        if (candidates.isEmpty()) {
            return null;
        }

        return candidates.get(
                ThreadLocalRandom.current().nextInt(candidates.size()));
    }

    /**
     * Thêm Manager vào conversation nếu chưa tồn tại.
     */
    private void addManagerParticipant(
            ChatConversation conversation,
            UUID managerId) {

        boolean exists = participantRepository
                .existsByConversation_IdAndUserId(
                        conversation.getId(),
                        managerId);

        if (exists) {
            return;
        }

        ConversationParticipant participant = new ConversationParticipant();

        participant.setConversation(conversation);
        participant.setUserId(managerId);
        participant.setParticipantRole(ParticipantRole.MANAGER);

        participantRepository.save(participant);
        participantRepository.flush();
    }

    /**
     * Xóa Manager cũ và phân công lại các conversation đang phụ trách.
     */
    @Transactional
    public void reassignConversationsFromManager(UUID oldManagerId) {

        List<ChatConversation> conversations = participantRepository.findConversationsByManager(
                oldManagerId,
                ParticipantRole.MANAGER,
                ConversationStatus.ACTIVE);

        for (ChatConversation conversation : conversations) {

            participantRepository.deleteByConversation_IdAndUserId(
                    conversation.getId(),
                    oldManagerId);

            // Nếu không còn Manager phù hợp, conversation sẽ tạm thời
            // không có Manager. Không xóa conversation hoặc lịch sử chat.
            assignManager(conversation, false);
        }
    }

    /**
     * Phân công Manager cho các conversation đang chưa có người phụ trách.
     */
    @Transactional
    public void assignUnassignedConversations(ChatDepartment department) {

        List<ChatConversation> conversations = conversationRepository.findUnassignedConversations(
                ConversationStatus.ACTIVE,
                department,
                ParticipantRole.MANAGER);

        for (ChatConversation conversation : conversations) {
            assignManager(conversation, false);
        }
    }

    /**
     * Chuyển đối tượng ManagerInfoResponse thành UUID.
     *
     * ManagerService hiện trả về ManagerInfoResponse có trường userId.
     */
    private UUID extractManagerId(Object manager) {
        try {
            return (UUID) manager.getClass()
                    .getMethod("userId")
                    .invoke(manager);
        } catch (Exception exception) {
            throw new IllegalStateException(
                    "Không thể lấy userId từ ManagerInfoResponse",
                    exception);
        }
    }

    @Transactional
    public void ensureActiveManager(
            ChatConversation conversation,
            boolean required) {

        List<UUID> activeManagerIds = managerService.getManagerIdsByRoleName(
                conversation.getDepartment().name());

        List<ConversationParticipant> participants = participantRepository.findByConversation_Id(
                conversation.getId());

        List<ConversationParticipant> managerParticipants = participants.stream()
                .filter(p -> p.getParticipantRole() == ParticipantRole.MANAGER)
                .toList();

        // Kiểm tra Manager hiện tại còn ACTIVE hay không.
        boolean hasValidManager = managerParticipants.stream()
                .anyMatch(p -> activeManagerIds.contains(p.getUserId()));

        if (hasValidManager) {
            // Đã có Manager hợp lệ, giữ nguyên người phụ trách.
            return;
        }

        // Xóa các Manager cũ không còn ACTIVE.
        for (ConversationParticipant participant : managerParticipants) {
            participantRepository.delete(participant);
        }

        participantRepository.flush();

        // Phân công Manager mới.
        assignManager(conversation, required);
    }
}
