package com.project.tour.service;

import com.project.tour.dto.schedule.CreateScheduleRequest;
import com.project.tour.dto.schedule.ScheduleResponse;
import com.project.tour.exception.DuplicateResourceException;
import com.project.tour.model.*;
import com.project.tour.model.enums.*;
import com.project.tour.repository.*;
import com.project.tour.repository.cruise.CruiseRepository;
import com.project.tour.service.schedule.ScheduleService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScheduleServiceTests {
    @Mock
    ScheduleRepository repository;
    @Mock
    TourPackageRepository packageRepository;
    @Mock
    CruiseRepository cruiseRepository;
    ScheduleService service;
    UUID packageId;
    UUID cruiseId;
    TourPackage tourPackage;
    Cruise cruise;

    @BeforeEach
    void setUp() {
        service = new ScheduleService(repository, packageRepository, cruiseRepository);
        packageId = UUID.randomUUID();
        cruiseId = UUID.randomUUID();
        tourPackage = new TourPackage();
        tourPackage.setId(packageId);
        tourPackage.setName("Ha Long");
        tourPackage.setNumberOfDays(3);
        tourPackage.setStatus(TourPackageStatus.ACTIVE);
        cruise = new Cruise();
        cruise.setId(cruiseId);
        cruise.setName("Ocean Star");
        cruise.setMaxPassengers(200);
        cruise.setStatus(CruiseStatus.ACTIVE);
    }

    @Test
    void createLinksReferencesAndNormalizesCode() {
        when(packageRepository.findById(packageId)).thenReturn(Optional.of(tourPackage));
        when(cruiseRepository.findById(cruiseId)).thenReturn(Optional.of(cruise));
        when(repository.save(any(Schedule.class))).thenAnswer(i -> i.getArgument(0));
        LocalDate start = LocalDate.of(2026, 9, 1);
        ScheduleResponse response = service.create(new CreateScheduleRequest(
                packageId, cruiseId, " hl-001 ", start, start.plusDays(2), 150));
        assertEquals("HL-001", response.code());
        assertEquals(ScheduleStatus.DRAFT, response.status());
        assertEquals(packageId, response.tourPackageId());
    }

    @Test
    void createRejectsCruiseDateConflict() {
        when(packageRepository.findById(packageId)).thenReturn(Optional.of(tourPackage));
        when(cruiseRepository.findById(cruiseId)).thenReturn(Optional.of(cruise));
        when(repository.hasCruiseDateConflict(eq(cruiseId), any(), any(), isNull())).thenReturn(true);
        LocalDate start = LocalDate.of(2026, 9, 1);
        assertThrows(DuplicateResourceException.class, () -> service.create(
                new CreateScheduleRequest(packageId, cruiseId, "HL-001", start, start.plusDays(2), 150)));
    }

    @Test
    void createRejectsDurationDifferentFromPackage() {
        when(packageRepository.findById(packageId)).thenReturn(Optional.of(tourPackage));
        when(cruiseRepository.findById(cruiseId)).thenReturn(Optional.of(cruise));
        LocalDate start = LocalDate.of(2026, 9, 1);
        assertThrows(IllegalArgumentException.class, () -> service.create(
                new CreateScheduleRequest(packageId, cruiseId, "HL-001", start, start.plusDays(1), 150)));
    }
}
