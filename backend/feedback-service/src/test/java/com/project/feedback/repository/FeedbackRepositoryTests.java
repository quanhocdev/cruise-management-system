package com.project.feedback.repository;

import com.project.feedback.dto.RatingSummary;
import com.project.feedback.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import java.time.Instant;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest(properties = {
    "spring.jpa.properties.hibernate.default_schema=PUBLIC",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class FeedbackRepositoryTests {
    @Autowired FeedbackRepository repository;

    @Test void summariesOnlyIncludePublishedFeedback() {
        UUID tourId = UUID.randomUUID(); UUID cruiseId = UUID.randomUUID();
        repository.saveAndFlush(feedback(1L, 7L, tourId, cruiseId, 5, FeedbackStatus.PUBLISHED));
        repository.saveAndFlush(feedback(2L, 8L, tourId, cruiseId, 3, FeedbackStatus.PUBLISHED));
        repository.saveAndFlush(feedback(3L, 9L, tourId, cruiseId, 1, FeedbackStatus.HIDDEN));
        RatingSummary tour = repository.summarizeTour(tourId); RatingSummary cruise = repository.summarizeCruise(cruiseId);
        assertEquals(2, tour.reviewCount()); assertEquals(4.0, tour.averageRating(), 0.001);
        assertEquals(2, cruise.reviewCount()); assertEquals(4.0, cruise.averageRating(), 0.001);
    }

    @Test void sameUserCannotReviewSameBookingTwice() {
        UUID tourId = UUID.randomUUID(); UUID cruiseId = UUID.randomUUID();
        repository.saveAndFlush(feedback(1L, 7L, tourId, cruiseId, 5, FeedbackStatus.PUBLISHED));
        assertThrows(DataIntegrityViolationException.class, () ->
            repository.saveAndFlush(feedback(1L, 7L, tourId, cruiseId, 4, FeedbackStatus.PUBLISHED)));
    }

    private Feedback feedback(Long bookingId, Long userId, UUID tourId, UUID cruiseId, int rating, FeedbackStatus status) {
        Feedback f = new Feedback(); f.setBookingId(bookingId); f.setPassengerVoyageId(bookingId * 10);
        f.setReviewerUserId(userId); f.setTourId(tourId); f.setCruiseId(cruiseId); f.setRating(rating);
        f.setContent("Review"); f.setImageUrls(new ArrayList<>()); f.setStatus(status);
        f.setCreatedAt(Instant.now()); f.setUpdatedAt(Instant.now()); return f;
    }
}
