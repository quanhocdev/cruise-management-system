package com.project.tour.service.passenger;

import com.project.tour.repository.convenience.ProductTourRepository;
import com.project.tour.repository.convenience.ServiceTourRepository;
import com.project.tour.repository.cruise.CruiseAreaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
@Transactional(readOnly = true)
public class PassengerTripCatalog {

    private final ProductTourRepository products;
    private final ServiceTourRepository services;
    private final CruiseAreaRepository areas;

    private static final Set<String> VISIBLE = Set.of(
            "CONFIGURED",
            "NOT_STARTED",
            "IN_PROGRESS",
            "COMPLETED",
            "OUT_OF_STOCK");

    public PassengerTripCatalog(
            ProductTourRepository products,
            ServiceTourRepository services,
            CruiseAreaRepository areas) {

        this.products = products;
        this.services = services;
        this.areas = areas;
    }

    public List<Item> get(UUID tourId) {

        var result = new ArrayList<Item>();

        for (var p : products.findAllByTourIdOrderByCreatedAtAsc(tourId)) {

            var product = p.getProduct();

            if (!visible(
                    p.getId(),
                    p.getStatus() != null ? p.getStatus().name() : null,
                    product != null ? product.getName() : null)) {
                continue;
            }

            result.add(new Item(
                    p.getId(),
                    "PRODUCT",
                    product.getName(),
                    product.getDescription(),
                    product.getImageUrl(),
                    product.getPrice(),
                    location(p.getCruiseAreaId()),
                    null,
                    null,
                    p.getStatus().name()));
        }

        for (var s : services.findAllByTourIdOrderByCreatedAtAsc(tourId)) {

            var service = s.getService();

            if (!visible(
                    s.getId(),
                    s.getStatus() != null ? s.getStatus().name() : null,
                    service != null ? service.getName() : null)) {
                continue;
            }

            result.add(new Item(
                    s.getId(),
                    "SERVICE",
                    service.getName(),
                    service.getDescription(),
                    service.getImageUrl(),
                    service.getPrice(),
                    location(s.getCruiseAreaId()),
                    s.getDurationMinutes(),
                    s.getMaxPassengers(),
                    s.getStatus().name()));
        }

        result.sort(
                Comparator.comparing(Item::type)
                        .thenComparing(Item::name)
                        .thenComparing(Item::id));

        return result;
    }

    private boolean visible(
            UUID id,
            String status,
            String name) {

        return id != null
                && status != null
                && VISIBLE.contains(status)
                && name != null
                && !name.isBlank();
    }

    private String location(UUID id) {

        return id == null
                ? null
                : areas.findById(id)
                        .map(a -> a.getName())
                        .orElse(null);
    }

    public record Item(
            UUID id,
            String type,
            String name,
            String description,
            String imageUrl,
            BigDecimal price,
            String location,
            Integer durationMinutes,
            Integer maxPassengers,
            String status) {
    }
}