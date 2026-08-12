package com.project.common.service.location;

import com.fasterxml.jackson.databind.JsonNode;
import com.project.common.dto.location.AddressResponse;
import com.project.common.mapper.location.AddressMapper;
import com.project.common.provider.location.NominatimGeocodingProvider;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class NominatimGeocodingService
        implements GeocodingService {

    private final NominatimGeocodingProvider provider;
    private final AddressMapper addressMapper;

    public NominatimGeocodingService(
            NominatimGeocodingProvider provider,
            AddressMapper addressMapper) {
        this.provider = provider;
        this.addressMapper = addressMapper;
    }

    @Override
    public AddressResponse getAddress(
            BigDecimal latitude,
            BigDecimal longitude) {

        if (latitude == null || longitude == null) {
            return new AddressResponse();
        }

        try {

            JsonNode rootNode = provider.reverseGeocode(
                    latitude,
                    longitude);

            return addressMapper.toResponse(rootNode);

        } catch (Exception e) {

            return new AddressResponse();
        }
    }
}