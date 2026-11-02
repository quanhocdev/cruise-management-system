package com.project.payment.service.refund;

import com.project.payment.provider.refund.RefundProvider;
import com.project.payment.service.refund.RefundModels.Outcome;
import com.project.payment.service.refund.RefundModels.Refund;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class RefundDispatcher {

    private static final Logger LOG = LoggerFactory.getLogger(RefundDispatcher.class);

    private final RefundService refundService;
    private final RefundProvider refundProvider;

    public RefundDispatcher(
            RefundService refundService,
            RefundProvider refundProvider) {
        this.refundService = refundService;
        this.refundProvider = refundProvider;
    }

    public void dispatch(UUID refundId) {

        boolean claimed = refundService.claim(refundId);

        if (!claimed) {
            return;
        }

        Refund refund = refundService.read(refundId);

        Outcome result;

        try {
            result = refundProvider.submit(refund);

        } catch (Exception e) {

            LOG.warn(
                    "Refund provider submission is unconfirmed: {}",
                    refundId,
                    e);

            result = Outcome.unknown("PROVIDER_UNCONFIRMED");
        }

        refundService.complete(refundId, result);
    }

    @Scheduled(fixedDelayString = "${payment.refunds.poll-ms:10000}", initialDelayString = "${payment.refunds.poll-ms:10000}")
    public void recover() {

        for (UUID id : refundService.queued()) {

            try {
                dispatch(id);

            } catch (Exception e) {

                LOG.warn(
                        "Refund dispatch requires reconciliation: {}",
                        id,
                        e);
            }
        }

        for (UUID id : refundService.awaitingReconciliation()) {

            try {
                Refund refund = refundService.read(id);

                Outcome result = refundProvider.reconcile(refund);

                refundService.complete(id, result);

            } catch (Exception e) {

                LOG.warn(
                        "Refund reconciliation remains pending: {}",
                        id,
                        e);
            }
        }
    }
}