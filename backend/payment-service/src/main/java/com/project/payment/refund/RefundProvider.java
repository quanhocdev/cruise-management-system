package com.project.payment.refund;

import com.project.payment.refund.RefundModels.*;

public interface RefundProvider {
    Outcome submit(Refund refund);
    Outcome reconcile(Refund refund);
}
