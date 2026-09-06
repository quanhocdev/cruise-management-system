package com.project.tour.controller;

import com.project.tour.config.*;
import com.project.tour.model.*;
import com.project.tour.repository.tour.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InternalFeedbackTargetController.class)
@Import({SecurityConfig.class, JwtConfig.class})
@TestPropertySource(properties = {"jwt.secret=cruise-management-system-local-secret-key-2026", "internal.api-key=test-internal-key"})
class InternalFeedbackTargetControllerTests {
    @Autowired MockMvc mockMvc;
    @MockitoBean AssignmentActivityCruiseRepository onboardRepository;
    @MockitoBean AssignmentActivityVisitRepository shoreRepository;
    @MockitoBean AssignmentProductRepository productRepository;
    @MockitoBean AssignmentServiceRepository serviceRepository;

    private final UUID tourId = UUID.randomUUID();
    private final UUID targetId = UUID.randomUUID();

    @Test void missingInternalKeyIsRejected() throws Exception {
        mockMvc.perform(get("/internal/tours/{tourId}/feedback-targets/ONBOARD_ACTIVITY/{targetId}", tourId, targetId))
            .andExpect(status().isUnauthorized());
    }

    @Test void completedOnboardActivityInTourIsEligible() throws Exception {
        Tour tour = new Tour(); tour.setId(tourId);
        AssignmentActivityCruise activity = new AssignmentActivityCruise();
        activity.setTourId(tour.getId()); activity.setStatus("COMPLETED");
        when(onboardRepository.findByActivityCruiseTourId(targetId)).thenReturn(Optional.of(activity));

        mockMvc.perform(get("/internal/tours/{tourId}/feedback-targets/ONBOARD_ACTIVITY/{targetId}", tourId, targetId)
                .header("X-Internal-Api-Key", "test-internal-key"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.completed").value(true))
            .andExpect(jsonPath("$.targetType").value("ONBOARD_ACTIVITY"));
    }

    @Test void targetFromAnotherTourIsRejected() throws Exception {
        Tour other = new Tour(); other.setId(UUID.randomUUID());
        AssignmentActivityCruise activity = new AssignmentActivityCruise();
        activity.setTourId(other.getId()); activity.setStatus("COMPLETED");
        when(onboardRepository.findByActivityCruiseTourId(targetId)).thenReturn(Optional.of(activity));

        mockMvc.perform(get("/internal/tours/{tourId}/feedback-targets/ONBOARD_ACTIVITY/{targetId}", tourId, targetId)
                .header("X-Internal-Api-Key", "test-internal-key"))
            .andExpect(status().isConflict());
    }
}
