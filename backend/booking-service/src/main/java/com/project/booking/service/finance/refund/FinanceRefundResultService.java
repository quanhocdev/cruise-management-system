
package com.project.booking.service.finance.refund;

import com.project.common.event.RefundResultEvent;

public interface FinanceRefundResultService {

    void processRefundResult(RefundResultEvent event);
}
