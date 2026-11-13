
package com.project.tour.model.admin;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "policy_refund_rules", indexes = {
        @Index(name = "idx_policy_refund_rule_policy", columnList = "policy_id")
})
public class PolicyRefundRule {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id", nullable = false)
    private Policy policy;

    @Column(name = "min_days_before_departure", nullable = false)
    private Integer minDaysBeforeDeparture;

    @Column(name = "max_days_before_departure")
    private Integer maxDaysBeforeDeparture;

    @Column(name = "refund_percentage", nullable = false, precision = 5, scale = 2)
    private BigDecimal refundPercentage;

    @Column(length = 500)
    private String description;

    public PolicyRefundRule() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Policy getPolicy() {
        return policy;
    }

    public void setPolicy(Policy policy) {
        this.policy = policy;
    }

    public Integer getMinDaysBeforeDeparture() {
        return minDaysBeforeDeparture;
    }

    public void setMinDaysBeforeDeparture(Integer minDaysBeforeDeparture) {
        this.minDaysBeforeDeparture = minDaysBeforeDeparture;
    }

    public Integer getMaxDaysBeforeDeparture() {
        return maxDaysBeforeDeparture;
    }

    public void setMaxDaysBeforeDeparture(Integer maxDaysBeforeDeparture) {
        this.maxDaysBeforeDeparture = maxDaysBeforeDeparture;
    }

    public BigDecimal getRefundPercentage() {
        return refundPercentage;
    }

    public void setRefundPercentage(BigDecimal refundPercentage) {
        this.refundPercentage = refundPercentage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
