
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
    public void createProductInvitation(
            ProductUsedEvent event,
            UUID reviewerUserId) {

        createInvitation(
                event.bookingId(),
                event.bookingPassengerId(),
                reviewerUserId,
                FeedbackType.PRODUCT,
                event.productTourId());
    }

    /*
     * =====================================================
     * SERVICE COMPLETED
     * =====================================================
     */
    @Transactional
    public void createServiceInvitation(
            ServiceCompletedEvent event,
            UUID reviewerUserId) {

        createInvitation(
                event.bookingId(),
                event.bookingPassengerId(),
                reviewerUserId,
                FeedbackType.SERVICE,
                event.serviceTourId());
    }

    /*
     * =====================================================
     * ACTIVITY CRUISE COMPLETED
     * =====================================================
     */
    @Transactional
    public void createActivityCruiseInvitation(
            ActivityCruiseCompletedEvent event,
            UUID reviewerUserId) {

        createInvitation(
                event.bookingId(),
                event.bookingPassengerId(),
                reviewerUserId,
                FeedbackType.ACTIVITY_CRUISE,
                event.activityCruiseTourId());
    }

    /*
     * =====================================================
     * ACTIVITY VISIT COMPLETED
     * =====================================================
     */
    @Transactional
    public void createActivityVisitInvitation(
            ActivityVisitCompletedEvent event,
            UUID reviewerUserId) {

        createInvitation(
                event.bookingId(),
                event.bookingPassengerId(),
                reviewerUserId,
                FeedbackType.ACTIVITY_VISIT,
                event.visitTourId());
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
            UUID targetId) {

        if (invitationRepository.existsByBookingIdAndFeedbackTypeAndTargetId(
                bookingId,
                feedbackType,
                targetId)) {
            return;
        }

        FeedbackInvitation invitation = new FeedbackInvitation();

        invitation.setBookingId(bookingId);
        invitation.setBookingPassengerId(bookingPassengerId);
        invitation.setReviewerUserId(reviewerUserId);
        invitation.setFeedbackType(feedbackType);
        invitation.setTargetId(targetId);
        invitation.setStatus(FeedbackInvitationStatus.PENDING);

        invitationRepository.save(invitation);
    }
}
