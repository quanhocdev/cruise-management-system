
package com.project.feedback.service;

import com.project.common.event.ProductUsedEvent;
import com.project.common.event.ServiceCompletedEvent;
import com.project.common.event.ActivityCruiseCompletedEvent;
import com.project.common.event.ActivityVisitCompletedEvent;

import com.project.feedback.model.FeedbackInvitation;
import com.project.feedback.model.enums.FeedbackInvitationStatus;
import com.project.feedback.model.enums.FeedbackType;
import com.project.feedback.repository.FeedbackInvitationRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class FeedbackInvitationService {

    private final FeedbackInvitationRepository invitationRepository;

    public FeedbackInvitationService(
            FeedbackInvitationRepository invitationRepository) {
        this.invitationRepository = invitationRepository;
    }

    /*
     * =====================================================
     * PRODUCT USED
     * =====================================================
     */
    @Transactional
    public void createProductInvitation(ProductUsedEvent event) {

        createInvitation(
                event.bookingId(),
                event.bookingPassengerId(),
                event.bookingOwnerUserId(),
                FeedbackType.PRODUCT,
                event.productTourId(),
                event.productName(),
                event.sourceUsageId());
    }

    /*
     * =====================================================
     * SERVICE COMPLETED
     * =====================================================
     */
    @Transactional
    public void createServiceInvitation(ServiceCompletedEvent event) {

        createInvitation(
                event.bookingId(),
                event.bookingPassengerId(),
                event.bookingOwnerUserId(),
                FeedbackType.SERVICE,
                event.serviceTourId(),
                event.serviceName(),
                event.sourceUsageId());
    }

    /*
     * =====================================================
     * ACTIVITY CRUISE COMPLETED
     * =====================================================
     */
    @Transactional
    public void createActivityCruiseInvitation(
            ActivityCruiseCompletedEvent event) {

        createInvitation(
                event.bookingId(),
                event.bookingPassengerId(),
                event.bookingOwnerUserId(),
                FeedbackType.ACTIVITY_CRUISE,
                event.activityCruiseTourId(),
                event.activityName(),
                event.sourceUsageId());
    }

    /*
     * =====================================================
     * ACTIVITY VISIT COMPLETED
     * =====================================================
     */
    @Transactional
    public void createActivityVisitInvitation(
            ActivityVisitCompletedEvent event) {

        createInvitation(
                event.bookingId(),
                event.bookingPassengerId(),
                event.bookingOwnerUserId(),
                FeedbackType.ACTIVITY_VISIT,
                event.visitTourId(),
                event.activityName(),
                event.sourceUsageId());
    }

    /*
     * =====================================================
     * COMMON CREATE INVITATION
     * =====================================================
     */
    private void createInvitation(
            Long bookingId,
            Long bookingPassengerId,
            UUID reviewerUserId,
            FeedbackType feedbackType,
            UUID targetId,
            String targetName,
            Long sourceUsageId) {

        // Kiểm tra event hợp lệ
        if (bookingId == null
                || bookingPassengerId == null
                || reviewerUserId == null
                || feedbackType == null
                || targetId == null
                || sourceUsageId == null) {

            throw new IllegalArgumentException(
                    "Feedback invitation event contains missing required fields");
        }

        // Chống Kafka redelivery tạo invitation trùng
        // Chỉ kiểm tra cùng loại trải nghiệm + cùng lượt sử dụng
        if (invitationRepository.existsByFeedbackTypeAndSourceUsageId(
                feedbackType,
                sourceUsageId)) {
            return;
        }

        FeedbackInvitation invitation = new FeedbackInvitation();

        invitation.setBookingId(bookingId);
        invitation.setBookingPassengerId(bookingPassengerId);
        invitation.setReviewerUserId(reviewerUserId);
        invitation.setFeedbackType(feedbackType);
        invitation.setTargetId(targetId);
        invitation.setTargetName(targetName);
        invitation.setSourceUsageId(sourceUsageId);
        invitation.setStatus(FeedbackInvitationStatus.PENDING);

        invitationRepository.save(invitation);
    }
}
