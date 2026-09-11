package com.project.tour.service.passenger;

import com.project.tour.repository.tour.AssignmentProductRepository;
import com.project.tour.repository.tour.AssignmentServiceRepository;
import com.project.tour.repository.cruise.CruiseAreaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.*;

/** Read-only projection of convenience configuration snapshots, not live stock. */
@Service
@Transactional(readOnly = true)
public class PassengerTripCatalog {
    private final AssignmentProductRepository products;
    private final AssignmentServiceRepository services;
    private final CruiseAreaRepository areas;
    private static final Set<String> VISIBLE = Set.of("CONFIGURED", "NOT_STARTED", "IN_PROGRESS", "COMPLETED", "OUT_OF_STOCK");

    public PassengerTripCatalog(AssignmentProductRepository products, AssignmentServiceRepository services,
                                CruiseAreaRepository areas) {
        this.products = products;
        this.services = services;
        this.areas = areas;
    }

    public List<Item> get(UUID tourId) {
        var result = new ArrayList<Item>();
        for (var p : products.findAllByTourIdOrderByCreatedAtAsc(tourId)) {
            if (!visible(p.getProductTourId(), p.getStatus(), p.getProductName())) continue;
            result.add(new Item(p.getProductTourId(), "PRODUCT", p.getProductName(), p.getProductDescription(),
                p.getImageUrl(), p.getPrice(), location(p.getCruiseAreaId()), null, null, p.getStatus()));
        }
        for (var s : services.findAllByTourIdOrderByCreatedAtAsc(tourId)) {
            if (!visible(s.getServiceTourId(), s.getStatus(), s.getServiceName())) continue;
            result.add(new Item(s.getServiceTourId(), "SERVICE", s.getServiceName(), s.getServiceDescription(),
                s.getImageUrl(), s.getPrice(), location(s.getCruiseAreaId()), s.getDurationMinutes(), s.getMaxPassengers(), s.getStatus()));
        }
        result.sort(Comparator.comparing(Item::type).thenComparing(Item::name).thenComparing(Item::id));
        return result;
    }

    private boolean visible(UUID id, String status, String name) {
        return id != null && status != null && VISIBLE.contains(status) && name != null && !name.isBlank();
    }

    private String location(UUID id) {
        return id == null ? null : areas.findById(id).map(a -> a.getName()).orElse(null);
    }

    public record Item(UUID id, String type, String name, String description, String imageUrl,
                       BigDecimal price, String location, Integer durationMinutes, Integer maxPassengers, String status) {}
}
