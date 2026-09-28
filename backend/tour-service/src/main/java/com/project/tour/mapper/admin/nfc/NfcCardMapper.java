package com.project.tour.mapper.admin.nfc;

import com.project.tour.dto.admin.nfc.NfcCardResponse;
import com.project.tour.model.admin.NfcCard;

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