package com.project.payment.refund;

import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class RefundDispatcher {
    private static final Logger LOG = LoggerFactory.getLogger(RefundDispatcher.class);
    private final RefundStore store;
    private final RefundProvider provider;
    public RefundDispatcher(RefundStore store,RefundProvider provider) { this.store=store; this.provider=provider; }

    public void dispatch(UUID id) {
        store.claim(id).ifPresent(refund -> {
            RefundModels.Outcome result;
            try { result=provider.submit(refund); }
            catch (Exception e) { result=RefundModels.Outcome.unknown("PROVIDER_UNCONFIRMED"); }
            store.complete(id,result);
        });
    }

    @Scheduled(fixedDelayString="${payment.refunds.poll-ms:10000}",initialDelayString="${payment.refunds.poll-ms:10000}")
    public void recover() {
        for (UUID id : store.queued()) {
            try { dispatch(id); }
            catch (Exception e) { LOG.warn("Refund dispatch requires reconciliation: {}",id); }
        }
        for (UUID id : store.awaitingReconciliation()) {
            try { store.complete(id,provider.reconcile(store.read(id))); }
            catch (Exception e) { LOG.warn("Refund reconciliation remains pending: {}",id); }
        }
    }
}
