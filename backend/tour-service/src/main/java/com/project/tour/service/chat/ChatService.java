
package com.project.tour.service.chat;

import com.project.tour.dto.chat.ChatMessageRequest;
import com.project.tour.dto.chat.ChatMessageResponse;
import com.project.tour.dto.chat.ChatSessionRequest;
import com.project.tour.dto.chat.ChatWelcomeResponse;
import com.google.api.gax.rpc.ApiException;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ChatService {

    private final DialogflowClientService dialogflowClientService;
    private final DialogflowSuggestionService dialogflowSuggestionService;

    public ChatService(
            DialogflowClientService dialogflowClientService,
            DialogflowSuggestionService dialogflowSuggestionService) {
        this.dialogflowClientService = dialogflowClientService;
        this.dialogflowSuggestionService = dialogflowSuggestionService;
    }

    public ChatWelcomeResponse openChat(ChatSessionRequest request) {
        try {
            String requestedSessionId = request == null ? null : request.sessionId();

            DialogflowClientService.DialogflowResult result = dialogflowClientService.detectWelcomeIntent(
                    requestedSessionId);

            List<String> suggestions = dialogflowSuggestionService.getRandomSuggestions();

            return new ChatWelcomeResponse(
                    result.message(),
                    result.sessionId(),
                    result.intentName(),
                    suggestions);

        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Thông tin phiên hội thoại không hợp lệ");

        } catch (IllegalStateException ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Không thể tải câu gợi ý từ cấu hình Dialogflow");

        } catch (ApiException ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Không thể kết nối đến dịch vụ chatbot");
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
