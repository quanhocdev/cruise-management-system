
package com.project.feedback.controller;

import com.project.feedback.dto.response.FeedbackInvitationResponse;
import com.project.feedback.dto.response.FeedbackResponse;
import com.project.feedback.dto.request.SubmitFeedbackRequest;
import com.project.feedback.security.JwtUserUtil;
import com.project.feedback.service.FeedbackService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/passengers/feedback")
public class PassengerFeedbackController {

        private final FeedbackService feedbackService;

        public PassengerFeedbackController(FeedbackService feedbackService) {
                this.feedbackService = feedbackService;
        }

        /**
         * Lấy tất cả lời mời đánh giá của tài khoản đang đăng nhập.
         */
        @GetMapping("/invitations")
        public ResponseEntity<List<FeedbackInvitationResponse>> getMyInvitations(
                        Principal principal) {

                UUID reviewerUserId = JwtUserUtil.extractUserId(principal);

                return ResponseEntity.ok(
                                feedbackService.getMyInvitations(reviewerUserId));
        }

        /**
         * Lấy các lời mời đang chờ đánh giá.
         */
        @GetMapping("/invitations/pending")
        public ResponseEntity<List<FeedbackInvitationResponse>> getMyPendingInvitations(
                        Principal principal) {

                UUID reviewerUserId = JwtUserUtil.extractUserId(principal);

                return ResponseEntity.ok(
                                feedbackService.getMyPendingInvitations(reviewerUserId));
        }

        /**
         * Gửi đánh giá cho một lời mời.
         */
        @PostMapping("/invitations/{id}/submit")
        public ResponseEntity<FeedbackResponse> submitFeedback(
                        @PathVariable UUID id,
                        @RequestBody SubmitFeedbackRequest request,
                        Principal principal) {

                UUID reviewerUserId = JwtUserUtil.extractUserId(principal);

                return ResponseEntity.ok(
                                feedbackService.submitFeedback(
                                                reviewerUserId, id, request));
        }

        /**
         * Bỏ qua một lời mời đánh giá.
         */
        @PostMapping("/invitations/{id}/skip")
        public ResponseEntity<FeedbackInvitationResponse> skipInvitation(
                        @PathVariable UUID id,
                        Principal principal) {

                UUID reviewerUserId = JwtUserUtil.extractUserId(principal);

                return ResponseEntity.ok(
                                feedbackService.skipInvitation(reviewerUserId, id));
        }

        /**
         * Xem đánh giá đã gửi cho một lời mời.
         */
        @GetMapping("/invitations/{id}/feedback")
        public ResponseEntity<FeedbackResponse> getMyFeedback(
                        @PathVariable UUID id,
                        Principal principal) {

                UUID reviewerUserId = JwtUserUtil.extractUserId(principal);

                return ResponseEntity.ok(
                                feedbackService.getMyFeedback(reviewerUserId, id));
        }
}
