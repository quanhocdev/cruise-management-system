package com.project.feedback.service;

import com.project.feedback.dto.*;
import com.project.feedback.model.FeedbackTargetType;
import java.util.*;

public interface FeedbackService {
    FeedbackOptions options(Long bookingId, UUID userId);
    FeedbackResponse create(CreateFeedbackRequest request, UUID userId);
    FeedbackResponse update(Long id, UpdateFeedbackRequest request, UUID userId);
    void delete(Long id, UUID userId);
    List<FeedbackResponse> getMine(UUID userId);
    List<PublicFeedbackResponse> getTourFeedback(UUID tourId);
    List<PublicFeedbackResponse> getCruiseFeedback(UUID cruiseId);
    List<PublicFeedbackResponse> getTargetFeedback(FeedbackTargetType targetType, UUID targetId);
    RatingSummary summarizeTour(UUID tourId);
    RatingSummary summarizeCruise(UUID cruiseId);
    RatingSummary summarizeTarget(FeedbackTargetType targetType, UUID targetId);
    List<FeedbackResponse> getAllForAdmin();
    FeedbackResponse moderate(Long id, ModerateFeedbackRequest request, UUID adminId);
}
