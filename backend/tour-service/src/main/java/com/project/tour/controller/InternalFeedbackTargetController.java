package com.project.tour.controller;

import com.project.tour.dto.tour.FeedbackTargetContext;
import com.project.tour.exception.AppException;
import com.project.tour.model.*;
import com.project.tour.repository.tour.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.UUID;

@RestController
@RequestMapping("/internal/tours/{tourId}/feedback-targets")
public class InternalFeedbackTargetController {
    private final AssignmentActivityCruiseRepository onboardRepository;
    private final AssignmentActivityVisitRepository shoreRepository;
    private final AssignmentProductRepository productRepository;
    private final AssignmentServiceRepository serviceRepository;
    private final byte[] expectedApiKey;

    public InternalFeedbackTargetController(AssignmentActivityCruiseRepository onboardRepository,
            AssignmentActivityVisitRepository shoreRepository, AssignmentProductRepository productRepository,
            AssignmentServiceRepository serviceRepository, @Value("${internal.api-key}") String apiKey) {
        this.onboardRepository = onboardRepository;
        this.shoreRepository = shoreRepository;
        this.productRepository = productRepository;
        this.serviceRepository = serviceRepository;
        this.expectedApiKey = apiKey.getBytes(StandardCharsets.UTF_8);
    }

    @GetMapping("/{targetType}/{targetId}")
    public FeedbackTargetContext context(@PathVariable UUID tourId, @PathVariable String targetType,
            @PathVariable UUID targetId,
            @RequestHeader(value = "X-Internal-Api-Key", required = false) String apiKey) {
        authorize(apiKey);
        return switch (targetType) {
            case "ONBOARD_ACTIVITY" -> onboard(tourId, targetId);
            case "SHORE_ACTIVITY" -> shore(tourId, targetId);
            case "PRODUCT" -> product(tourId, targetId);
            case "SERVICE" -> service(tourId, targetId);
            default -> throw new AppException("Unsupported feedback target type: " + targetType, HttpStatus.BAD_REQUEST);
        };
    }

    private FeedbackTargetContext onboard(UUID tourId, UUID targetId) {
        AssignmentActivityCruise item = onboardRepository.findByActivityCruiseTourId(targetId)
            .orElseThrow(() -> notFound(targetId));
        verifyTour(tourId, item.getTourId());
        return result(tourId, "ONBOARD_ACTIVITY", targetId, "COMPLETED".equals(item.getStatus()));
    }

    private FeedbackTargetContext shore(UUID tourId, UUID targetId) {
        AssignmentActivityVisit item = shoreRepository.findByVisitTourId(targetId).orElseThrow(() -> notFound(targetId));
        verifyTour(tourId, item.getTourId());
        return result(tourId, "SHORE_ACTIVITY", targetId, "COMPLETED".equals(item.getStatus()));
    }

    private FeedbackTargetContext product(UUID tourId, UUID targetId) {
        AssignmentProduct item = productRepository.findByProductTourId(targetId).orElseThrow(() -> notFound(targetId));
        verifyTour(tourId, item.getTourId());
        return result(tourId, "PRODUCT", targetId, "COMPLETED".equals(item.getStatus()));
    }

    private FeedbackTargetContext service(UUID tourId, UUID targetId) {
        AssignmentService item = serviceRepository.findByServiceTourId(targetId).orElseThrow(() -> notFound(targetId));
        verifyTour(tourId, item.getTourId());
        return result(tourId, "SERVICE", targetId, "COMPLETED".equals(item.getStatus()));
    }

    private FeedbackTargetContext result(UUID tourId, String type, UUID targetId, boolean completed) {
        return new FeedbackTargetContext(tourId, type, targetId, completed);
    }

    private void verifyTour(UUID requested, UUID actual) {
        if (!requested.equals(actual)) throw new AppException("Feedback target does not belong to this tour", HttpStatus.CONFLICT);
    }

    private AppException notFound(UUID id) {
        return new AppException("Feedback target not found: " + id, HttpStatus.NOT_FOUND);
    }

    private void authorize(String suppliedApiKey) {
        byte[] supplied = suppliedApiKey == null ? new byte[0] : suppliedApiKey.getBytes(StandardCharsets.UTF_8);
        if (!MessageDigest.isEqual(expectedApiKey, supplied))
            throw new AppException("Invalid internal API key", HttpStatus.UNAUTHORIZED);
    }
}
