
package com.project.payment.service;

import com.project.payment.repository.BillCollectionRepository;
import com.project.payment.repository.BillCollectionRepository.CollectionRow;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class BillCollectionReport {

    private final BillCollectionRepository repository;

    public BillCollectionReport(BillCollectionRepository repository) {
        this.repository = repository;
    }

    public record Collection(
            Long billId,
            BigDecimal collected,
            BigDecimal returned,
            BigDecimal refundPending,
            long successfulPayments,
            boolean needsReview) {
    }

    @Transactional(readOnly = true)
    public List<Collection> read(List<Long> ids) {

        if (ids == null
                || ids.isEmpty()
                || ids.size() > 100
                || ids.stream().anyMatch(id -> id == null || id <= 0)
                || new HashSet<>(ids).size() != ids.size()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "INVALID_BILL_IDS");
        }

        Map<Long, Collection> result = new LinkedHashMap<>();

        for (Long id : ids) {
            result.put(
                    id,
                    new Collection(
                            id,
                            BigDecimal.ZERO,
                            BigDecimal.ZERO,
                            BigDecimal.ZERO,
                            0,
                            false));
        }

        List<CollectionRow> rows = repository.findCollections(ids);

        for (CollectionRow row : rows) {
            result.put(
                    row.getBillId(),
                    new Collection(
                            row.getBillId(),
                            row.getCollected(),
                            row.getReturned(),
                            row.getRefundPending(),
                            row.getSuccessfulPayments() != null
                                    ? row.getSuccessfulPayments()
                                    : 0L,
                            Boolean.TRUE.equals(row.getNeedsReview())));
        }

        return List.copyOf(result.values());
    }
}
