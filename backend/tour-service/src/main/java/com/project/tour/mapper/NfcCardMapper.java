package com.project.tour.mapper;

import com.project.tour.dto.NfcCardResponse;
import com.project.tour.model.NfcCard;
import org.springframework.stereotype.Component;

@Component
public class NfcCardMapper {
    public NfcCardResponse toResponse(NfcCard card) {
        return new NfcCardResponse(
                card.getId(),
                card.getCardUid(),
                card.getStatus(),
                card.getCreatedAt(),
                card.getUpdatedAt());
    }
}