
package com.project.common.event;

import java.util.UUID;

public record RefundResultEvent(
        UUID paymentRefundId,
        UUID paymentClientRequestId,
        String status,
        String providerCode,
        String providerTransactionNo) {
}
