
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
import java.util.concurrent.ThreadLocalRandom;

@Service
public class ChatService {

    private final DialogflowClientService dialogflowClientService;

    private static final List<String> HOTLINE_SUGGESTIONS = List.of(
            "Liên hệ hotline",
            "Tôi muốn gặp tư vấn viên",
            "Cho tôi xin thông tin liên hệ");

    private static final List<String> BOOKING_SUGGESTIONS = List.of(
            "Hướng dẫn đặt tour",
            "Quy trình đặt tour như thế nào?",
            "Tôi muốn biết cách đặt tour");

    private static final List<String> SYSTEM_SUGGESTIONS = List.of(
            "Giới thiệu hệ thống",
            "Hệ thống Cruise có những chức năng gì?",
            "Tôi muốn tìm hiểu về hệ thống");

    public ChatService(DialogflowClientService dialogflowClientService) {
        this.dialogflowClientService = dialogflowClientService;
    }

    public ChatWelcomeResponse openChat(ChatSessionRequest request) {
        try {
            String requestedSessionId = request == null ? null : request.sessionId();

            DialogflowClientService.DialogflowResult result = dialogflowClientService.detectWelcomeIntent(
                    requestedSessionId);

            List<String> suggestions = List.of(
                    pickRandom(HOTLINE_SUGGESTIONS),
                    pickRandom(BOOKING_SUGGESTIONS),
                    pickRandom(SYSTEM_SUGGESTIONS));

            return new ChatWelcomeResponse(
                    result.message(),
                    result.sessionId(),
                    result.intentName(),
                    suggestions);

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

    private String pickRandom(List<String> suggestions) {
        int index = ThreadLocalRandom.current().nextInt(suggestions.size());
        return suggestions.get(index);
    }
}
