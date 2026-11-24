
package com.project.chat.service.chat;

import com.google.api.gax.rpc.ApiException;
import com.project.chat.dto.chat.ChatMessageRequest;
import com.project.chat.dto.chat.ChatMessageResponse;
import com.project.chat.dto.chat.ChatSessionRequest;
import com.project.chat.dto.chat.ChatWelcomeResponse;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

@Service
public class ChatService {

    private final DialogflowClientService dialogflowClientService;
    private final DialogflowSuggestionService dialogflowSuggestionService;
    private static final Logger log = LoggerFactory.getLogger(ChatService.class);

    public ChatService(
            DialogflowClientService dialogflowClientService,
            DialogflowSuggestionService dialogflowSuggestionService) {
        this.dialogflowClientService = dialogflowClientService;
        this.dialogflowSuggestionService = dialogflowSuggestionService;
    }

    public ChatWelcomeResponse openChat(ChatSessionRequest request) {
        try {
            String requestedSessionId = request == null ? null : request.sessionId();

            log.info("[CHAT WELCOME] Bắt đầu mở chat. SessionId: {}", requestedSessionId);

            log.info("[CHAT WELCOME] Đang gọi Dialogflow WELCOME event");

            DialogflowClientService.DialogflowResult result = dialogflowClientService
                    .detectWelcomeIntent(requestedSessionId);

            log.info("[CHAT WELCOME] Dialogflow trả lời thành công. Intent: {}",
                    result.intentName());

            log.info("[CHAT WELCOME] Đang tải câu gợi ý");

            List<String> suggestions = dialogflowSuggestionService.getRandomSuggestions();

            log.info("[CHAT WELCOME] Tải thành công {} câu gợi ý",
                    suggestions.size());

            return new ChatWelcomeResponse(
                    result.message(),
                    result.sessionId(),
                    result.intentName(),
                    suggestions);

        } catch (IllegalArgumentException ex) {
            log.error("[CHAT WELCOME] Dữ liệu không hợp lệ", ex);

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Thông tin phiên hội thoại không hợp lệ");

        } catch (IllegalStateException ex) {
            log.error("[CHAT WELCOME] Cấu hình câu gợi ý không hợp lệ", ex);

            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Không thể tải câu gợi ý từ cấu hình Dialogflow");

        } catch (ApiException ex) {
            log.error("[CHAT WELCOME] Lỗi Dialogflow API", ex);

            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Không thể kết nối đến dịch vụ chatbot");

        } catch (Exception ex) {
            log.error("[CHAT WELCOME] Lỗi không xác định khi mở chat", ex);
            throw ex;
        }
    }

    public ChatMessageResponse sendMessage(ChatMessageRequest request) {
        if (request == null
                || request.message() == null
                || request.message().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Nội dung câu hỏi không được để trống");
        }

        try {
            DialogflowClientService.DialogflowResult result = dialogflowClientService.detectIntent(
                    request.message().trim(),
                    request.sessionId());

            return new ChatMessageResponse(
                    result.message(),
                    result.sessionId(),
                    result.intentName());

        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Thông tin phiên hội thoại không hợp lệ");

        } catch (ApiException ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Không thể kết nối đến dịch vụ chatbot");
        }
    }
}
