package com.project.payment.service.refund;

import com.project.payment.dto.refund.RefundModels.Outcome;
import com.project.payment.dto.refund.RefundModels.Refund;
import com.project.payment.provider.refund.RefundProvider;

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

        LOG.info("[REFUND SCHEDULER] Started");

        var queuedRefunds = refundService.queued();

        LOG.info(
                "[REFUND SCHEDULER] Queued refunds found: {}",
                queuedRefunds.size());

        for (UUID id : queuedRefunds) {
            LOG.info("[REFUND SCHEDULER] Dispatching refund: {}", id);

            try {
                dispatch(id);
            } catch (Exception e) {
                LOG.warn(
                        "[REFUND SCHEDULER] Dispatch failed: {}",
                        id,
                        e);
            }
        }

        var pendingRefunds = refundService.awaitingReconciliation();

        LOG.info(
                "[REFUND SCHEDULER] Refunds awaiting reconciliation: {}",
                pendingRefunds.size());

        for (UUID id : pendingRefunds) {

            LOG.info("[REFUND SCHEDULER] Reconciling refund: {}", id);

            try {
                Refund refund = refundService.read(id);

                Outcome result = refundProvider.reconcile(refund);

                LOG.info(
                        "[REFUND SCHEDULER] Reconciliation result: id={}, status={}, code={}",
                        id,
                        result.status(),
                        result.code());

                refundService.complete(id, result);

            } catch (Exception e) {
                LOG.warn(
                        "[REFUND SCHEDULER] Reconciliation failed: {}",
                        id,
                        e);
            }
        }
    }
}