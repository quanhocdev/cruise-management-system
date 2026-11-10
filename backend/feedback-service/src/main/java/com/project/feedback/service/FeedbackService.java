
package com.project.feedback.service;

import com.project.feedback.dto.request.SubmitFeedbackRequest;
import com.project.feedback.dto.response.FeedbackInvitationResponse;
import com.project.feedback.dto.response.FeedbackResponse;
import com.project.feedback.exception.AppException;
import com.project.feedback.mapper.FeedbackMapper;
import com.project.feedback.model.Feedback;
import com.project.feedback.model.FeedbackInvitation;
import com.project.feedback.model.enums.FeedbackInvitationStatus;
import com.project.feedback.repository.FeedbackInvitationRepository;
import com.project.feedback.repository.FeedbackRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final FeedbackInvitationRepository invitationRepository;
    private final FeedbackMapper feedbackMapper;

    public FeedbackService(
            FeedbackRepository feedbackRepository,
            FeedbackInvitationRepository invitationRepository,
            FeedbackMapper feedbackMapper) {
        this.feedbackRepository = feedbackRepository;
        this.invitationRepository = invitationRepository;
        this.feedbackMapper = feedbackMapper;
    }

    /*
     * =====================================================
     * GET MY INVITATIONS
     * =====================================================
     */
    @Transactional(readOnly = true)
    public List<FeedbackInvitationResponse> getMyInvitations(
            UUID reviewerUserId) {

        return invitationRepository
                .findByReviewerUserIdOrderByCreatedAtDesc(reviewerUserId)
                .stream()
                .map(feedbackMapper::toInvitationResponse)
                .toList();
    }

    /*
     * =====================================================
     * GET MY PENDING INVITATIONS
     * =====================================================
     */
    @Transactional(readOnly = true)
    public List<FeedbackInvitationResponse> getMyPendingInvitations(
            UUID reviewerUserId) {

        return invitationRepository
                .findByReviewerUserIdAndStatusOrderByCreatedAtDesc(
                        reviewerUserId,
                        FeedbackInvitationStatus.PENDING)
                .stream()
                .map(feedbackMapper::toInvitationResponse)
                .toList();
    }

    /*
     * =====================================================
     * SUBMIT FEEDBACK
     * =====================================================
     */
    @Transactional
    public FeedbackResponse submitFeedback(
            UUID reviewerUserId,
            UUID invitationId,
            SubmitFeedbackRequest request) {

        FeedbackInvitation invitation = invitationRepository
                .findByIdAndReviewerUserId(invitationId, reviewerUserId)
                .orElseThrow(() -> new AppException(
                        "Feedback invitation not found",
                        HttpStatus.NOT_FOUND));

        if (invitation.getStatus() != FeedbackInvitationStatus.PENDING) {
            throw new AppException(
                    "Feedback invitation is no longer pending",
                    HttpStatus.CONFLICT);
        }

        if (feedbackRepository.existsByInvitationId(invitationId)) {
            throw new AppException(
                    "Feedback has already been submitted",
                    HttpStatus.CONFLICT);
        }

        Feedback feedback = new Feedback();
        feedback.setInvitation(invitation);
        feedback.setRating(request.rating());
        feedback.setComment(request.comment());

        Feedback savedFeedback = feedbackRepository.save(feedback);

        invitation.setStatus(FeedbackInvitationStatus.COMPLETED);
        invitationRepository.save(invitation);

        return feedbackMapper.toFeedbackResponse(savedFeedback);
    }

    /*
     * =====================================================
     * SKIP INVITATION
     * =====================================================
     */
    @Transactional
    public FeedbackInvitationResponse skipInvitation(
            UUID reviewerUserId,
            UUID invitationId) {

        FeedbackInvitation invitation = invitationRepository
                .findByIdAndReviewerUserId(invitationId, reviewerUserId)
                .orElseThrow(() -> new AppException(
                        "Feedback invitation not found",
                        HttpStatus.NOT_FOUND));

        if (invitation.getStatus() != FeedbackInvitationStatus.PENDING) {
            throw new AppException(
                    "Only pending invitations can be skipped",
                    HttpStatus.CONFLICT);
        }

        invitation.setStatus(FeedbackInvitationStatus.SKIPPED);

        FeedbackInvitation savedInvitation = invitationRepository.save(invitation);

        return feedbackMapper.toInvitationResponse(savedInvitation);
    }

    /*
     * =====================================================
     * GET MY FEEDBACK BY INVITATION
     * =====================================================
     */
    @Transactional(readOnly = true)
    public FeedbackResponse getMyFeedback(
            UUID reviewerUserId,
            UUID invitationId) {

        FeedbackInvitation invitation = invitationRepository
                .findByIdAndReviewerUserId(invitationId, reviewerUserId)
                .orElseThrow(() -> new AppException(
                        "Feedback invitation not found",
                        HttpStatus.NOT_FOUND));

        Feedback feedback = feedbackRepository
                .findByInvitationId(invitation.getId())
                .orElseThrow(() -> new AppException(
                        "Feedback not found",
                        HttpStatus.NOT_FOUND));

        return feedbackMapper.toFeedbackResponse(feedback);
    }
}
