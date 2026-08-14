package com.project.tour.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.common.mapper.location.AddressMapper;
import com.project.common.provider.location.NominatimGeocodingProvider;
import com.project.common.service.location.GeocodingService;
import com.project.common.service.location.NominatimGeocodingService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LocationConfig {

    // Thêm Bean ObjectMapper để Spring quản lý
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public NominatimGeocodingProvider nominatimGeocodingProvider(
            @Value("${nominatim.url}") String nominatimUrl,
            ObjectMapper objectMapper) {

        return new NominatimGeocodingProvider(
                nominatimUrl,
                objectMapper);
    }

    @Bean
    public GeocodingService geocodingService(
            NominatimGeocodingProvider provider,
            AddressMapper addressMapper) {

        return new NominatimGeocodingService(
                provider,
                addressMapper);
    }
}