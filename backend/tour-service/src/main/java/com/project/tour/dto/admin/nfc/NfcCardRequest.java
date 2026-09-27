package com.project.tour.dto.admin.nfc;

import com.project.tour.model.enums.NfcCardStatus;

public record NfcCardRequest(
                String cardUid,
                NfcCardStatus status) {
}