package com.project.payment.provider.refund;

import com.project.payment.dto.refund.RefundModels.Outcome;
import com.project.payment.dto.refund.RefundModels.Refund;

public interface RefundProvider {

    Outcome submit(Refund refund);

    Outcome reconcile(Refund refund);
}