package com.project.tour.model;

import com.project.tour.model.enums.PolicyStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "cancel_policies", uniqueConstraints = @UniqueConstraint(
    name = "uk_cancel_policy_days", columnNames = {"policy_id", "days_before"}
))
public class CancelPolicy {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "policy_id", nullable = false)
    private Policy policy;
    @Column(name = "days_before", nullable = false)
    private Integer daysBefore;
    @Column(name = "refund_percent", nullable = false, precision = 5, scale = 2)
    private BigDecimal refundPercent;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PolicyStatus status;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public Policy getPolicy() { return policy; }
    public void setPolicy(Policy policy) { this.policy = policy; }
    public Integer getDaysBefore() { return daysBefore; }
    public void setDaysBefore(Integer daysBefore) { this.daysBefore = daysBefore; }
    public BigDecimal getRefundPercent() { return refundPercent; }
    public void setRefundPercent(BigDecimal refundPercent) { this.refundPercent = refundPercent; }
    public PolicyStatus getStatus() { return status; }
    public void setStatus(PolicyStatus status) { this.status = status; }
}
