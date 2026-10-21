package com.project.payment.service;

import java.math.BigDecimal;
import java.util.*;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class BillCollectionReport {
    private final NamedParameterJdbcTemplate jdbc;
    public BillCollectionReport(org.springframework.jdbc.core.JdbcTemplate jdbc) {
        this.jdbc=new NamedParameterJdbcTemplate(jdbc);
    }
    public record Collection(Long billId, BigDecimal collected, BigDecimal returned,
                             BigDecimal refundPending, long successfulPayments, boolean needsReview) {}

    @Transactional(readOnly=true)
    public List<Collection> read(List<Long> ids) {
        if(ids==null || ids.isEmpty() || ids.size()>100 || ids.stream().anyMatch(id->id==null || id<=0)
                || new HashSet<>(ids).size()!=ids.size())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"INVALID_BILL_IDS");
        var result=new LinkedHashMap<Long,Collection>();
        ids.forEach(id->result.put(id,new Collection(id,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,0,false)));
        jdbc.query("""
            WITH refunds AS (
              SELECT r.payment_id,
                COALESCE(sum(r.amount) FILTER (WHERE r.status='SUCCEEDED'),0) AS returned,
                COALESCE(sum(r.amount) FILTER (WHERE r.status IN ('QUEUED','SENDING','PROCESSING','UNKNOWN')),0) AS pending
              FROM payment.payment_refunds r JOIN payment.payments p ON p.id=r.payment_id
              WHERE p.reference_type='BILL' AND p.reference_id IN (:ids) GROUP BY r.payment_id
            )
            SELECT p.reference_id, sum(p.amount) AS collected, sum(COALESCE(r.returned,0)) AS returned,
              sum(COALESCE(r.pending,0)) AS pending, count(*) AS successful,
              bool_or(COALESCE(r.returned,0)>p.amount OR
                (p.status='REFUNDED' AND COALESCE(r.returned,0)<>p.amount)) AS needs_review
            FROM payment.payments p LEFT JOIN refunds r ON r.payment_id=p.id
            WHERE p.reference_type='BILL' AND p.reference_id IN (:ids) AND p.status IN ('SUCCESS','REFUNDED')
            GROUP BY p.reference_id
            """,Map.of("ids",ids),(org.springframework.jdbc.core.RowCallbackHandler) r-> {
                long count=r.getLong("successful");
                var row=new Collection(r.getLong("reference_id"),r.getBigDecimal("collected"),
                    r.getBigDecimal("returned"),r.getBigDecimal("pending"),count,r.getBoolean("needs_review")||count>1);
                result.put(row.billId(),row);
            });
        return List.copyOf(result.values());
    }
}
