
package com.project.booking.model;

import com.project.booking.model.enums.BookingRefundStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "booking_refunds", indexes = {
        @Index(name = "idx_booking_refunds_booking", columnList = "booking_id"),
        @Index(name = "idx_booking_refunds_status", columnList = "status"),
        @Index(name = "idx_booking_refunds_requester", columnList = "requested_by_user_id")
})
public class BookingRefund {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Booking được yêu cầu hoàn tiền
    @Column(name = "booking_id", nullable = false)
    private Long bookingId;

    // Người gửi yêu cầu hoàn tiền
    @Column(name = "requested_by_user_id", nullable = false)
    private UUID requestedByUserId;

    // Lý do khách yêu cầu hoàn tiền
    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;

    // Luật hoàn tiền được áp dụng (tham chiếu tour-service)
    @Column(name = "policy_refund_rule_id")
    private UUID policyRefundRuleId;

    // UUID cố định dùng làm clientRequestId khi tạo PaymentRefund
    @Column(name = "payment_client_request_id", unique = true)
    private UUID paymentClientRequestId;

    // Ngày khởi hành tại thời điểm yêu cầu
    @Column(name = "departure_date")
    private LocalDate departureDate;

    // Số ngày còn lại trước khởi hành tại thời điểm yêu cầu
    @Column(name = "days_before_departure")
    private Integer daysBeforeDeparture;

    // Tổng tiền booking tại thời điểm yêu cầu
    @Column(name = "original_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal originalAmount;

    // Tỷ lệ hoàn tiền snapshot (%)
    @Column(name = "refund_percentage", nullable = false, precision = 5, scale = 2)
    private BigDecimal refundPercentage;

    // Số tiền dự kiến được hoàn
    @Column(name = "calculated_refund_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal calculatedRefundAmount;

    // Trạng thái yêu cầu hoàn tiền
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private BookingRefundStatus status = BookingRefundStatus.PENDING;

    // Nhân viên Finance xét duyệt
    @Column(name = "reviewed_by_user_id")
    private UUID reviewedByUserId;

    // Thời điểm xét duyệt
    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    // Lý do Finance từ chối
    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;

        if (status == null) {
            status = BookingRefundStatus.PENDING;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // GETTERS & SETTERS

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public UUID getRequestedByUserId() {
        return requestedByUserId;
    }

    public void setRequestedByUserId(UUID requestedByUserId) {
        this.requestedByUserId = requestedByUserId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public UUID getPolicyRefundRuleId() {
        return policyRefundRuleId;
    }

    public void setPolicyRefundRuleId(UUID policyRefundRuleId) {
        this.policyRefundRuleId = policyRefundRuleId;
    }

    public LocalDate getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(LocalDate departureDate) {
        this.departureDate = departureDate;
    }

    public Integer getDaysBeforeDeparture() {
        return daysBeforeDeparture;
    }

    public void setDaysBeforeDeparture(Integer daysBeforeDeparture) {
        this.daysBeforeDeparture = daysBeforeDeparture;
    }

    public BigDecimal getOriginalAmount() {
        return originalAmount;
    }

    public void setOriginalAmount(BigDecimal originalAmount) {
        this.originalAmount = originalAmount;
    }

    public BigDecimal getRefundPercentage() {
        return refundPercentage;
    }

    public void setRefundPercentage(BigDecimal refundPercentage) {
        this.refundPercentage = refundPercentage;
    }

    public BigDecimal getCalculatedRefundAmount() {
        return calculatedRefundAmount;
    }

    public void setCalculatedRefundAmount(BigDecimal calculatedRefundAmount) {
        this.calculatedRefundAmount = calculatedRefundAmount;
    }

    public UUID getPaymentClientRequestId() {
        return paymentClientRequestId;
    }

    public void setPaymentClientRequestId(UUID paymentClientRequestId) {
        this.paymentClientRequestId = paymentClientRequestId;
    }

    public BookingRefundStatus getStatus() {
        return status;
    }

    public void setStatus(BookingRefundStatus status) {
        this.status = status;
    }

    public UUID getReviewedByUserId() {
        return reviewedByUserId;
    }

    public void setReviewedByUserId(UUID reviewedByUserId) {
        this.reviewedByUserId = reviewedByUserId;
    }

    public LocalDateTime getReviewedAt() {
        return reviewedAt;
    }

    public void setReviewedAt(LocalDateTime reviewedAt) {
        this.reviewedAt = reviewedAt;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
