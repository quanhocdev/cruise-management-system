package com.project.feedback.controller;

import com.project.feedback.dto.*;
import com.project.feedback.exception.FeedbackException;
import com.project.feedback.model.FeedbackTargetType;
import com.project.feedback.service.FeedbackService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/v1/feedbacks")
public class FeedbackController {
    private final FeedbackService service;
    public FeedbackController(FeedbackService service) { this.service = service; }
    @PostMapping ResponseEntity<FeedbackResponse> create(@Valid @RequestBody CreateFeedbackRequest request, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request, userId(jwt)));
    }
    @PutMapping("/{id}") FeedbackResponse update(@PathVariable Long id, @Valid @RequestBody UpdateFeedbackRequest request,
        @AuthenticationPrincipal Jwt jwt) { return service.update(id, request, userId(jwt)); }
    @DeleteMapping("/{id}") ResponseEntity<Void> delete(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        service.delete(id, userId(jwt)); return ResponseEntity.noContent().build();
    }
    @GetMapping("/me") List<FeedbackResponse> mine(@AuthenticationPrincipal Jwt jwt) { return service.getMine(userId(jwt)); }
    @GetMapping("/tours/{tourId}") List<PublicFeedbackResponse> tour(@PathVariable UUID tourId) { return service.getTourFeedback(tourId); }
    @GetMapping("/tours/{tourId}/summary") RatingSummary tourSummary(@PathVariable UUID tourId) { return service.summarizeTour(tourId); }
    @GetMapping("/cruises/{cruiseId}") List<PublicFeedbackResponse> cruise(@PathVariable UUID cruiseId) { return service.getCruiseFeedback(cruiseId); }
    @GetMapping("/cruises/{cruiseId}/summary") RatingSummary cruiseSummary(@PathVariable UUID cruiseId) { return service.summarizeCruise(cruiseId); }
    @GetMapping("/targets/{targetType}/{targetId}") List<PublicFeedbackResponse> target(
        @PathVariable FeedbackTargetType targetType, @PathVariable UUID targetId) {
        return service.getTargetFeedback(targetType, targetId);
    }
    @GetMapping("/targets/{targetType}/{targetId}/summary") RatingSummary targetSummary(
        @PathVariable FeedbackTargetType targetType, @PathVariable UUID targetId) {
        return service.summarizeTarget(targetType, targetId);
    }
    private Long userId(Jwt jwt) {
        Object claim = jwt.getClaim("userId"); if (claim instanceof Number n) return n.longValue();
        try { return Long.valueOf(String.valueOf(claim)); }
        catch (Exception ex) { throw new FeedbackException(HttpStatus.BAD_REQUEST, "JWT userId claim is missing or invalid"); }
    }
}
