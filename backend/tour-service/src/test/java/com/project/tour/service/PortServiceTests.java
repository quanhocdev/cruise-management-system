package com.project.tour.service;

import com.project.tour.dto.port.CreatePortRequest;
import com.project.tour.dto.port.PortResponse;
import com.project.tour.model.Port;
import com.project.tour.model.enums.PortStatus;
import com.project.tour.exception.ResourceNotFoundException;
import com.project.tour.repository.PortRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PortServiceTests {

    @Mock
    private PortRepository portRepository;

    private PortService portService;

    @BeforeEach
    void setUp() {
        portService = new PortService(portRepository);
    }

    @Test
    void createPortTrimsTextAndSetsActiveStatus() {
        CreatePortRequest request = new CreatePortRequest(
            " Saigon Port ",
            " Ho Chi Minh City ",
            " Vietnam ",
            " District 4 ",
            new BigDecimal("10.7598"),
            new BigDecimal("106.7072"),
            " Passenger port "
        );

        when(portRepository.save(any(Port.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        PortResponse response = portService.createPort(request);

        assertEquals("Saigon Port", response.name());
        assertEquals("Ho Chi Minh City", response.city());
        assertEquals("Vietnam", response.country());
        assertEquals("District 4", response.address());
        assertEquals("Passenger port", response.description());
        assertEquals(PortStatus.ACTIVE, response.status());

        verify(portRepository).save(any(Port.class));
    }

    @Test
    void getPortByIdThrowsWhenPortDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(portRepository.findById(id)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> portService.getPortById(id)
        );

        assertEquals("Port not found with id: " + id, exception.getMessage());
    }

    @Test
    void deactivatePortChangesStatusInsteadOfDeleting() {
        UUID id = UUID.randomUUID();
        Port port = new Port();
        port.setStatus(PortStatus.ACTIVE);

        when(portRepository.findById(id)).thenReturn(Optional.of(port));
        when(portRepository.save(port)).thenReturn(port);

        PortResponse response = portService.deactivatePort(id);

        assertEquals(PortStatus.INACTIVE, response.status());
        verify(portRepository).save(port);
    }
}
