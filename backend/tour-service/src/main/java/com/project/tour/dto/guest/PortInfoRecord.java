package com.project.tour.dto.guest;

import java.util.UUID;

public record PortInfoRecord(
        UUID id,
        String name,
        String code,
        String city,
        String country,
        String address) {
}