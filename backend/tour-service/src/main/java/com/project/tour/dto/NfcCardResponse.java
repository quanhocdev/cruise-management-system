package com.project.tour.dto;

import com.project.tour.model.enums.NfcCardStatus;
import java.time.LocalDateTime;
import java.util.UUID;

public record NfcCardResponse(
        UUID id,
        String cardUid,
        NfcCardStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}