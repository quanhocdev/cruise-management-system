package com.project.tour.dto;

import com.project.tour.model.enums.NfcCardStatus;

public record NfcCardRequest(
        String cardUid,
        NfcCardStatus status) {
}