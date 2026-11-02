package com.project.payment.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "payment_refunds", schema = "payment", indexes = {
        @Index(name = "idx_refunds_payment", columnList = "payment_id, created_at"),
        @Index(name = "idx_refunds_pending", columnList = "status, updated_at")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uk_payment_refunds_client_request", columnNames = "client_request_id")
})
public class PaymentRefund {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "payment_id", nullable = false)
    private Long paymentId;

    @Column(name = "client_request_id", nullable = false)
    private UUID clientRequestId;

    @Column(name = "request_hash", nullable = false, length = 64)
    private String requestHash;

    @Column(name = "actor_id", nullable = false)
    private UUID actorId;

    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "reason", nullable = false, length = 1000)
    private String reason;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "transaction_type", nullable = false, length = 2)
    private String transactionType;

    @Column(name = "original_transaction_no", nullable = false, length = 100)
    private String originalTransactionNo;

    @Column(name = "original_created_at", nullable = false)
    private Instant originalCreatedAt;

    @Column(name = "provider_transaction_no", length = 100)
    private String providerTransactionNo;

    @Column(name = "provider_code", length = 40)
    private String providerCode;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "attempted_at")
    private Instant attemptedAt;

    public PaymentRefund() {
    }

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();

        if (id == null) {
            id = UUID.randomUUID();
        }

        if (createdAt == null) {
            createdAt = now;
        }

        if (updatedAt == null) {
            updatedAt = now;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public UUID getClientRequestId() {
        return clientRequestId;
    }

    public void setClientRequestId(UUID clientRequestId) {
        this.clientRequestId = clientRequestId;
    }

    public String getRequestHash() {
        return requestHash;
    }

    public void setRequestHash(String requestHash) {
        this.requestHash = requestHash;
    }

    public UUID getActorId() {
        return actorId;
    }

    public void setActorId(UUID actorId) {
        this.actorId = actorId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getOriginalTransactionNo() {
        return originalTransactionNo;
    }

    public void setOriginalTransactionNo(String originalTransactionNo) {
        this.originalTransactionNo = originalTransactionNo;
    }

    public Instant getOriginalCreatedAt() {
        return originalCreatedAt;
    }

    public void setOriginalCreatedAt(Instant originalCreatedAt) {
        this.originalCreatedAt = originalCreatedAt;
    }

    public String getProviderTransactionNo() {
        return providerTransactionNo;
    }

    public void setProviderTransactionNo(String providerTransactionNo) {
        this.providerTransactionNo = providerTransactionNo;
    }

    public String getProviderCode() {
        return providerCode;
    }

    public void setProviderCode(String providerCode) {
        this.providerCode = providerCode;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Instant getAttemptedAt() {
        return attemptedAt;
    }

    public void setAttemptedAt(Instant attemptedAt) {
        this.attemptedAt = attemptedAt;
    }
}