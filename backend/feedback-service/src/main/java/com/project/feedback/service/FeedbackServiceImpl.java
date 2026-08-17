package com.project.feedback.service;

import com.project.feedback.client.*;
import com.project.feedback.dto.*;
import com.project.feedback.exception.FeedbackException;
import com.project.feedback.model.*;
import com.project.feedback.repository.FeedbackRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.net.URI;
import java.time.Instant;
import java.util.*;

@Service
public class FeedbackServiceImpl implements FeedbackService {
    private final FeedbackRepository repository; private final BookingClient bookingClient; private final TourClient tourClient;
    public FeedbackServiceImpl(FeedbackRepository repository, BookingClient bookingClient, TourClient tourClient) {
        this.repository = repository; this.bookingClient = bookingClient; this.tourClient = tourClient;
    }
    @Override @Transactional
    public FeedbackResponse create(CreateFeedbackRequest request, Long userId) {
        if (repository.existsByBookingIdAndReviewerUserId(request.bookingId(), userId))
            throw new FeedbackException(HttpStatus.CONFLICT, "You already reviewed this booking");
        FeedbackEligibility eligibility = bookingClient.eligibility(request.bookingId(), userId);
        if (!eligibility.participated())
            throw new FeedbackException(HttpStatus.FORBIDDEN, "Only passengers who completed the voyage can leave feedback");
        FeedbackTourContext tour = tourClient.context(eligibility.tourId());
        if (!tour.completed()) throw new FeedbackException(HttpStatus.CONFLICT, "Tour is not completed yet");
        if (tour.cruiseId() == null) throw new FeedbackException(HttpStatus.CONFLICT, "Tour has no assigned cruise");
        Instant now = Instant.now(); Feedback f = new Feedback();
        f.setBookingId(request.bookingId()); f.setPassengerVoyageId(eligibility.passengerVoyageId());
        f.setReviewerUserId(userId); f.setTourId(tour.tourId()); f.setCruiseId(tour.cruiseId());
        applyContent(f, request.rating(), request.content(), request.imageUrls());
        f.setStatus(FeedbackStatus.PUBLISHED); f.setCreatedAt(now); f.setUpdatedAt(now);
        return toResponse(repository.save(f));
    }
    @Override @Transactional
    public FeedbackResponse update(Long id, UpdateFeedbackRequest request, Long userId) {
        Feedback f = owned(id, userId); ensureUserEditable(f);
        applyContent(f, request.rating(), request.content(), request.imageUrls()); f.setUpdatedAt(Instant.now());
        return toResponse(repository.save(f));
    }
    @Override @Transactional
    public void delete(Long id, Long userId) {
        Feedback f = owned(id, userId); ensureUserEditable(f); f.setStatus(FeedbackStatus.DELETED); f.setUpdatedAt(Instant.now()); repository.save(f);
    }
    @Override @Transactional(readOnly = true)
    public List<FeedbackResponse> getMine(Long userId) { return map(repository.findAllByReviewerUserIdOrderByCreatedAtDesc(userId)); }
    @Override @Transactional(readOnly = true)
    public List<PublicFeedbackResponse> getTourFeedback(UUID id) { return publicMap(repository.findAllByTourIdAndStatusOrderByCreatedAtDesc(id, FeedbackStatus.PUBLISHED)); }
    @Override @Transactional(readOnly = true)
    public List<PublicFeedbackResponse> getCruiseFeedback(UUID id) { return publicMap(repository.findAllByCruiseIdAndStatusOrderByCreatedAtDesc(id, FeedbackStatus.PUBLISHED)); }
    @Override @Transactional(readOnly = true) public RatingSummary summarizeTour(UUID id) { return repository.summarizeTour(id); }
    @Override @Transactional(readOnly = true) public RatingSummary summarizeCruise(UUID id) { return repository.summarizeCruise(id); }
    @Override @Transactional(readOnly = true) public List<FeedbackResponse> getAllForAdmin() { return map(repository.findAllByOrderByCreatedAtDesc()); }
    @Override @Transactional
    public FeedbackResponse moderate(Long id, ModerateFeedbackRequest request, Long adminId) {
        if (request.status() == FeedbackStatus.DELETED)
            throw new FeedbackException(HttpStatus.BAD_REQUEST, "Admin cannot set the user-deleted status");
        if ((request.status() == FeedbackStatus.HIDDEN || request.status() == FeedbackStatus.REMOVED)
                && (request.reason() == null || request.reason().isBlank()))
            throw new FeedbackException(HttpStatus.BAD_REQUEST, "A moderation reason is required");
        Feedback f = find(id); if (f.getStatus() == FeedbackStatus.DELETED)
            throw new FeedbackException(HttpStatus.CONFLICT, "Deleted feedback cannot be moderated");
        f.setStatus(request.status()); f.setModerationReason(request.status() == FeedbackStatus.PUBLISHED ? null : request.reason().trim());
        f.setModeratedByUserId(adminId); f.setUpdatedAt(Instant.now()); return toResponse(repository.save(f));
    }
    private void applyContent(Feedback f, Integer rating, String content, List<String> urls) {
        f.setRating(rating); f.setContent(content.trim()); f.setImageUrls(validateUrls(urls));
    }
    private List<String> validateUrls(List<String> urls) {
        if (urls == null) return new ArrayList<>(); List<String> normalized = new ArrayList<>();
        for (String raw : urls) {
            try { URI uri = URI.create(raw.trim()); if (!"https".equalsIgnoreCase(uri.getScheme()) || uri.getHost() == null) throw new IllegalArgumentException(); }
            catch (IllegalArgumentException ex) { throw new FeedbackException(HttpStatus.BAD_REQUEST, "Feedback images must use valid HTTPS URLs"); }
            normalized.add(raw.trim());
        }
        if (new HashSet<>(normalized).size() != normalized.size()) throw new FeedbackException(HttpStatus.BAD_REQUEST, "Duplicate image URLs are not allowed");
        return normalized;
    }
    private Feedback owned(Long id, Long userId) { Feedback f = find(id); if (!Objects.equals(f.getReviewerUserId(), userId))
        throw new FeedbackException(HttpStatus.FORBIDDEN, "You cannot change this feedback"); return f; }
    private Feedback find(Long id) { return repository.findById(id).orElseThrow(() -> new FeedbackException(HttpStatus.NOT_FOUND, "Feedback not found: " + id)); }
    private void ensureUserEditable(Feedback f) { if (f.getStatus() == FeedbackStatus.DELETED || f.getStatus() == FeedbackStatus.REMOVED)
        throw new FeedbackException(HttpStatus.CONFLICT, "Feedback can no longer be changed"); }
    private List<FeedbackResponse> map(List<Feedback> items) { return items.stream().map(this::toResponse).toList(); }
    private List<PublicFeedbackResponse> publicMap(List<Feedback> items) { return items.stream().map(f ->
        new PublicFeedbackResponse(f.getId(), f.getTourId(), f.getCruiseId(), f.getRating(), f.getContent(),
            List.copyOf(f.getImageUrls()), f.getCreatedAt(), f.getUpdatedAt())).toList(); }
    private FeedbackResponse toResponse(Feedback f) { return new FeedbackResponse(f.getId(), f.getBookingId(), f.getPassengerVoyageId(),
        f.getReviewerUserId(), f.getTourId(), f.getCruiseId(), f.getRating(), f.getContent(), List.copyOf(f.getImageUrls()),
        f.getStatus(), f.getModerationReason(), f.getCreatedAt(), f.getUpdatedAt()); }
}
