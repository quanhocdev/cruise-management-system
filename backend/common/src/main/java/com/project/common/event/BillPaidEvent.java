package com.project.common.event;

import java.util.List;

public record BillPaidEvent(
        Long billId,
        Long bookingId,
        List<String> nfcCardUids) {
}