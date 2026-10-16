package com.project.tour.service.chat;

import com.project.tour.dto.chat.ChatMessageRequest;
import com.project.tour.dto.chat.ChatMessageResponse;

import com.google.api.gax.rpc.ApiException;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ChatService {

    private final DialogflowClientService dialogflowClientService;

    public ChatService(
            DialogflowClientService dialogflowClientService) {
        this.dialogflowClientService = dialogflowClientService;
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