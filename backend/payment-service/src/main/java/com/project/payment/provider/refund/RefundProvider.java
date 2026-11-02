package com.project.payment.provider.refund;

import com.project.payment.service.refund.RefundModels.Outcome;
import com.project.payment.service.refund.RefundModels.Refund;

public interface RefundProvider {

    Outcome submit(Refund refund);

    Outcome reconcile(Refund refund);
}