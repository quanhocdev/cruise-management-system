package com.project.feedback.service;

import com.project.feedback.dto.*;
import java.util.*;

public interface FeedbackService {
    FeedbackResponse create(CreateFeedbackRequest request, Long userId);
    FeedbackResponse update(Long id, UpdateFeedbackRequest request, Long userId);
    void delete(Long id, Long userId);
    List<FeedbackResponse> getMine(Long userId);
    List<PublicFeedbackResponse> getTourFeedback(UUID tourId);
    List<PublicFeedbackResponse> getCruiseFeedback(UUID cruiseId);
    RatingSummary summarizeTour(UUID tourId);
    RatingSummary summarizeCruise(UUID cruiseId);
    List<FeedbackResponse> getAllForAdmin();
    FeedbackResponse moderate(Long id, ModerateFeedbackRequest request, Long adminId);
}
