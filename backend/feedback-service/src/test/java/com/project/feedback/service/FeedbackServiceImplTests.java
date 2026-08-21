package com.project.feedback.service;

import com.project.feedback.client.*;
import com.project.feedback.dto.*;
import com.project.feedback.exception.FeedbackException;
import com.project.feedback.model.*;
import com.project.feedback.repository.FeedbackRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.Instant;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeedbackServiceImplTests {
    @Mock FeedbackRepository repository; @Mock BookingClient bookingClient; @Mock TourClient tourClient;
    FeedbackServiceImpl service;
    UUID tourId; UUID cruiseId;
    @BeforeEach void setup() { service = new FeedbackServiceImpl(repository, bookingClient, tourClient);
        tourId = UUID.randomUUID(); cruiseId = UUID.randomUUID(); }

    @Test void completedPassengerCanCreateOneFeedback() {
        when(bookingClient.eligibility(10L, 7L)).thenReturn(new FeedbackEligibility(10L, tourId, 20L, true));
        when(tourClient.context(tourId)).thenReturn(new FeedbackTourContext(tourId, cruiseId, true));
        when(repository.save(any())).thenAnswer(i -> { Feedback f = i.getArgument(0); f.setId(1L); return f; });
        FeedbackResponse result = service.create(createRequest(), 7L);
        assertEquals(5, result.rating()); assertEquals(FeedbackStatus.PUBLISHED, result.status());
        assertEquals(cruiseId, result.cruiseId());
    }

    @Test void duplicateFeedbackIsRejectedBeforeRemoteCalls() {
        when(repository.existsByBookingIdAndReviewerUserId(10L, 7L)).thenReturn(true);
        assertThrows(FeedbackException.class, () -> service.create(createRequest(), 7L));
        verifyNoInteractions(bookingClient, tourClient);
    }

    @Test void passengerWhoDidNotDisembarkCannotReview() {
        when(bookingClient.eligibility(10L, 7L)).thenReturn(new FeedbackEligibility(10L, tourId, 20L, false));
        assertThrows(FeedbackException.class, () -> service.create(createRequest(), 7L));
        verifyNoInteractions(tourClient);
    }

    @Test void unfinishedTourCannotBeReviewed() {
        when(bookingClient.eligibility(10L, 7L)).thenReturn(new FeedbackEligibility(10L, tourId, 20L, true));
        when(tourClient.context(tourId)).thenReturn(new FeedbackTourContext(tourId, cruiseId, false));
        assertThrows(FeedbackException.class, () -> service.create(createRequest(), 7L));
    }

    @Test void invalidOrDuplicateImageUrlsAreRejected() {
        stubEligible();
        assertThrows(FeedbackException.class, () -> service.create(
            new CreateFeedbackRequest(10L, 5, "Good", List.of("http://unsafe.test/a.jpg")), 7L));
    }

    @Test void onlyOwnerCanUpdateFeedback() {
        Feedback f = feedback(); when(repository.findById(1L)).thenReturn(Optional.of(f));
        assertThrows(FeedbackException.class, () -> service.update(1L,
            new UpdateFeedbackRequest(4, "Updated", List.of()), 99L));
    }

    @Test void adminCanHideFeedbackWithReason() {
        Feedback f = feedback(); when(repository.findById(1L)).thenReturn(Optional.of(f));
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));
        FeedbackResponse result = service.moderate(1L,
            new ModerateFeedbackRequest(FeedbackStatus.HIDDEN, "Spam"), 100L);
        assertEquals(FeedbackStatus.HIDDEN, result.status()); assertEquals("Spam", result.moderationReason());
    }

    private void stubEligible() {
        when(bookingClient.eligibility(10L, 7L)).thenReturn(new FeedbackEligibility(10L, tourId, 20L, true));
        when(tourClient.context(tourId)).thenReturn(new FeedbackTourContext(tourId, cruiseId, true));
    }
    private CreateFeedbackRequest createRequest() { return new CreateFeedbackRequest(10L, 5, "Excellent cruise",
        List.of("https://cdn.example.com/review.jpg")); }
    private Feedback feedback() { Feedback f = new Feedback(); f.setId(1L); f.setBookingId(10L); f.setPassengerVoyageId(20L);
        f.setReviewerUserId(7L); f.setTourId(tourId); f.setCruiseId(cruiseId); f.setRating(5); f.setContent("Good");
        f.setImageUrls(new ArrayList<>()); f.setStatus(FeedbackStatus.PUBLISHED); f.setCreatedAt(Instant.now()); f.setUpdatedAt(Instant.now()); return f; }
}
