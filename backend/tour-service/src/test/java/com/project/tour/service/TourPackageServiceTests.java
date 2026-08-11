package com.project.tour.service;

import com.project.tour.dto.tourpackage.CreateTourPackageRequest;
import com.project.tour.dto.tourpackage.TourPackageResponse;
import com.project.tour.exception.DuplicateResourceException;
import com.project.tour.model.TourPackage;
import com.project.tour.model.enums.TourPackageStatus;
import com.project.tour.repository.TourPackageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TourPackageServiceTests {

    @Mock TourPackageRepository repository;
    TourPackageService service;

    @BeforeEach
    void setUp() {
        service = new TourPackageService(repository);
    }

    @Test
    void createNormalizesDataAndActivatesPackage() {
        when(repository.existsByNameIgnoreCase("Ha Long Discovery")).thenReturn(false);
        when(repository.save(any(TourPackage.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        TourPackageResponse response = service.create(new CreateTourPackageRequest(
            "  Ha Long   Discovery  ", 3, 2, "  Three-day cruise  "
        ));

        assertEquals("Ha Long Discovery", response.name());
        assertEquals("Three-day cruise", response.description());
        assertEquals(TourPackageStatus.ACTIVE, response.status());
    }

    @Test
    void createRejectsDuplicateName() {
        when(repository.existsByNameIgnoreCase("Ha Long Discovery")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> service.create(
            new CreateTourPackageRequest("Ha Long Discovery", 3, 2, null)
        ));
    }

    @Test
    void createRejectsMoreNightsThanDays() {
        assertThrows(IllegalArgumentException.class, () -> service.create(
            new CreateTourPackageRequest("Invalid package", 2, 3, null)
        ));
    }
}
