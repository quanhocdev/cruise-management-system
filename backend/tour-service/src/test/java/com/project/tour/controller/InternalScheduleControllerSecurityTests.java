// package com.project.tour.controller;

// import com.project.tour.config.*;
// import com.project.tour.model.Schedule;
// import com.project.tour.model.enums.ScheduleStatus;
// import com.project.tour.repository.ScheduleRepository;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
// import org.springframework.context.annotation.Import;
// import org.springframework.test.context.TestPropertySource;
// import org.springframework.test.context.bean.override.mockito.MockitoBean;
// import org.springframework.test.web.servlet.MockMvc;
// import java.time.LocalDate;
// import java.util.*;
// import static org.mockito.Mockito.when;
// import static
// org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
// import static
// org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// @WebMvcTest(InternalScheduleController.class)
// @Import({SecurityConfig.class, JwtConfig.class})
// @TestPropertySource(properties = {
// "jwt.secret=cruise-management-system-local-secret-key-2026",
// "internal.api-key=test-internal-key"
// })
// class InternalScheduleControllerSecurityTests {
// @Autowired MockMvc mockMvc;
// @MockitoBean ScheduleRepository repository;
// private static final UUID ID =
// UUID.fromString("11111111-1111-1111-1111-111111111111");

// @Test void missingInternalKeyIsRejected() throws Exception {
// mockMvc.perform(get("/internal/schedules/{id}/booking-context",
// ID)).andExpect(status().isUnauthorized());
// }
// @Test void correctInternalKeyReturnsContext() throws Exception {
// Schedule schedule = new Schedule(); schedule.setId(ID);
// schedule.setCapacity(100);
// schedule.setStartDate(LocalDate.now().plusDays(10));
// schedule.setStatus(ScheduleStatus.OPEN);
// when(repository.findById(ID)).thenReturn(Optional.of(schedule));
// mockMvc.perform(get("/internal/schedules/{id}/booking-context", ID)
// .header("X-Internal-Api-Key",
// "test-internal-key")).andExpect(status().isOk());
// }
// }
