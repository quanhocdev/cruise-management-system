package com.project.feedback.controller;

import com.project.feedback.dto.*;
import com.project.feedback.exception.FeedbackException;
import com.project.feedback.service.FeedbackService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin/feedbacks")
public class AdminFeedbackController {
    private final FeedbackService service;
    public AdminFeedbackController(FeedbackService service) { this.service = service; }
    @GetMapping List<FeedbackResponse> all() { return service.getAllForAdmin(); }
    @PatchMapping("/{id}/moderation") FeedbackResponse moderate(@PathVariable Long id,
        @Valid @RequestBody ModerateFeedbackRequest request, @AuthenticationPrincipal Jwt jwt) {
        return service.moderate(id, request, userId(jwt));
    }
    private Long userId(Jwt jwt) {
        Object claim = jwt.getClaim("userId"); if (claim instanceof Number n) return n.longValue();
        try { return Long.valueOf(String.valueOf(claim)); }
        catch (Exception ex) { throw new FeedbackException(HttpStatus.BAD_REQUEST, "JWT userId claim is missing or invalid"); }
    }
}
