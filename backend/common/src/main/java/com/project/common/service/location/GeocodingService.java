package com.project.common.service.location;

import com.project.common.dto.location.AddressResponse;

import java.math.BigDecimal;

public interface GeocodingService {

    AddressResponse getAddress(
            BigDecimal latitude,
            BigDecimal longitude);
}