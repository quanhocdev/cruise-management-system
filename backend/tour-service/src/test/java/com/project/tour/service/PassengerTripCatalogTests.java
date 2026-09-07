package com.project.tour.service;

import com.project.tour.model.*;
import com.project.tour.repository.tour.*;
import com.project.tour.repository.cruise.CruiseAreaRepository;
import com.project.tour.service.passenger.PassengerTripCatalog;
import org.junit.jupiter.api.Test;
import java.util.*;
import java.math.BigDecimal;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class PassengerTripCatalogTests {
    final AssignmentProductRepository products = mock(AssignmentProductRepository.class);
    final AssignmentServiceRepository services = mock(AssignmentServiceRepository.class);
    final CruiseAreaRepository areas = mock(CruiseAreaRepository.class);
    final PassengerTripCatalog catalog = new PassengerTripCatalog(products, services, areas);
    final UUID tour = UUID.randomUUID();

    AssignmentProduct product(String status) {
        var p = new AssignmentProduct(); p.setProductTourId(UUID.randomUUID());
        p.setProductName("Water"); p.setStatus(status); return p;
    }

    @Test void returnsOnlyConfiguredEntriesForRequestedTour() {
        var valid = product("CONFIGURED"); valid.setPrice(new BigDecimal("25000.00"));
        valid.setImageUrl("https://example.com/water.png"); valid.setProductDescription("Bottle");
        var missingId = product("CONFIGURED"); missingId.setProductTourId(null);
        var missingName = product("CONFIGURED"); missingName.setProductName(" ");
        when(products.findAllByTourIdOrderByCreatedAtAsc(tour)).thenReturn(List.of(valid,
            product("WAITING_CONFIG"), product("CANCELLED"), product(null), missingId, missingName));
        var entries = catalog.get(tour);
        assertEquals(1, entries.size());
        assertEquals("PRODUCT", entries.get(0).type());
        assertEquals(new BigDecimal("25000.00"), entries.get(0).price());
        assertEquals("Bottle", entries.get(0).description());
        assertEquals(valid.getImageUrl(), entries.get(0).imageUrl());
        verify(products).findAllByTourIdOrderByCreatedAtAsc(tour);
        verify(services).findAllByTourIdOrderByCreatedAtAsc(tour);
        verifyNoMoreInteractions(products, services);
    }

    @Test void mapsServiceDetailsWithoutInventingPriceOrAvailability() {
        var service = new AssignmentService(); service.setServiceTourId(UUID.randomUUID());
        service.setServiceName("Spa"); service.setStatus("CONFIGURED");
        service.setDurationMinutes(30); service.setMaxPassengers(5);
        UUID areaId = UUID.randomUUID(); service.setCruiseAreaId(areaId);
        var area = new CruiseArea(); area.setName("Deck 1");
        when(areas.findById(areaId)).thenReturn(Optional.of(area));
        when(services.findAllByTourIdOrderByCreatedAtAsc(tour)).thenReturn(List.of(service));
        var item = catalog.get(tour).get(0);
        assertEquals("SERVICE", item.type()); assertNull(item.price());
        assertEquals("Deck 1", item.location()); assertEquals(30, item.durationMinutes());
        assertEquals(5, item.maxPassengers());
        service.setStatus("WAITING_CONFIG"); assertTrue(catalog.get(tour).isEmpty());
        service.setStatus("CONFIGURED"); service.setServiceTourId(null); assertTrue(catalog.get(tour).isEmpty());
    }

    @Test void emptyCatalogIsNotAnError() {
        assertTrue(catalog.get(tour).isEmpty());
    }

    @Test void configuredCatalogRemainsReadableThroughTripLifecycle() {
        for (String status : List.of("NOT_STARTED", "IN_PROGRESS", "COMPLETED", "OUT_OF_STOCK")) {
            when(products.findAllByTourIdOrderByCreatedAtAsc(tour)).thenReturn(List.of(product(status)));
            assertEquals(status, catalog.get(tour).get(0).status());
        }
    }

    @Test void missingAreaDoesNotHideConfiguredItem() {
        var p = product("CONFIGURED"); p.setCruiseAreaId(UUID.randomUUID());
        when(products.findAllByTourIdOrderByCreatedAtAsc(tour)).thenReturn(List.of(p));
        assertNull(catalog.get(tour).get(0).location());
    }
}
