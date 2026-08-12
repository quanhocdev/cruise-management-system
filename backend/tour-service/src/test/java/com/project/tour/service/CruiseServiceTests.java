package com.project.tour.service;

import com.project.tour.dto.cruise.CreateCruiseRequest;
import com.project.tour.dto.cruise.CruiseResponse;
import com.project.tour.exception.DuplicateResourceException;
import com.project.tour.exception.ResourceNotFoundException;
import com.project.tour.model.Cruise;
import com.project.tour.model.enums.CruiseStatus;
import com.project.tour.repository.cruise.CruiseRepository;
import com.project.tour.service.cruise.CruiseService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CruiseServiceTests {

        @Mock
        private CruiseRepository cruiseRepository;

        private CruiseService cruiseService;

        @BeforeEach
        void setUp() {
                cruiseService = new CruiseService(cruiseRepository);
        }

        @Test
        void createCruiseNormalizesCodeAndSetsActiveStatus() {
                CreateCruiseRequest request = new CreateCruiseRequest(
                                " Ocean Star ",
                                " ocean-star-01 ",
                                " Multi-day cruise ",
                                12,
                                2500);

                when(cruiseRepository.existsByCodeIgnoreCase("OCEAN-STAR-01"))
                                .thenReturn(false);
                when(cruiseRepository.save(any(Cruise.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));

                CruiseResponse response = cruiseService.createCruise(request);

                assertEquals("Ocean Star", response.name());
                assertEquals("OCEAN-STAR-01", response.code());
                assertEquals("Multi-day cruise", response.description());
                assertEquals(12, response.totalDecks());
                assertEquals(2500, response.maxPassengers());
                assertEquals(CruiseStatus.ACTIVE, response.status());
        }

        @Test
        void createCruiseRejectsDuplicateCode() {
                CreateCruiseRequest request = new CreateCruiseRequest(
                                "Ocean Star",
                                "ocean-star-01",
                                null,
                                12,
                                2500);

                when(cruiseRepository.existsByCodeIgnoreCase("OCEAN-STAR-01"))
                                .thenReturn(true);

                DuplicateResourceException exception = assertThrows(
                                DuplicateResourceException.class,
                                () -> cruiseService.createCruise(request));

                assertEquals(
                                "Cruise code already exists: OCEAN-STAR-01",
                                exception.getMessage());
                verify(cruiseRepository, never()).save(any(Cruise.class));
        }

        @Test
        void getCruiseByIdThrowsWhenCruiseDoesNotExist() {
                UUID id = UUID.randomUUID();
                when(cruiseRepository.findById(id)).thenReturn(Optional.empty());

                assertThrows(
                                ResourceNotFoundException.class,
                                () -> cruiseService.getCruiseById(id));
        }

        @Test
        void deactivateCruiseChangesStatusInsteadOfDeleting() {
                UUID id = UUID.randomUUID();
                Cruise cruise = new Cruise();
                cruise.setStatus(CruiseStatus.ACTIVE);

                when(cruiseRepository.findById(id)).thenReturn(Optional.of(cruise));
                when(cruiseRepository.save(cruise)).thenReturn(cruise);

                CruiseResponse response = cruiseService.deactivateCruise(id);

                assertEquals(CruiseStatus.INACTIVE, response.status());
                verify(cruiseRepository).save(cruise);
        }
}
