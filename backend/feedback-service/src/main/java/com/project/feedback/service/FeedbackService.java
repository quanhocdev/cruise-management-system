package com.project.feedback.service;

import com.project.feedback.dto.*;
import com.project.feedback.model.FeedbackTargetType;
import java.util.*;

public interface FeedbackService {
    FeedbackResponse create(CreateFeedbackRequest request, Long userId);
    FeedbackResponse update(Long id, UpdateFeedbackRequest request, Long userId);
    void delete(Long id, Long userId);
    List<FeedbackResponse> getMine(Long userId);
    List<PublicFeedbackResponse> getTourFeedback(UUID tourId);
    List<PublicFeedbackResponse> getCruiseFeedback(UUID cruiseId);
    List<PublicFeedbackResponse> getTargetFeedback(FeedbackTargetType targetType, UUID targetId);
    RatingSummary summarizeTour(UUID tourId);
    RatingSummary summarizeCruise(UUID cruiseId);
    RatingSummary summarizeTarget(FeedbackTargetType targetType, UUID targetId);
    List<FeedbackResponse> getAllForAdmin();
    FeedbackResponse moderate(Long id, ModerateFeedbackRequest request, Long adminId);
}
