
package com.project.common.event;

import java.time.Instant;

public record BillPaymentSuccessEvent(
        Long billId,
        Long paymentId,
        Long userId,
        String status,
        Instant paidAt) {
}
