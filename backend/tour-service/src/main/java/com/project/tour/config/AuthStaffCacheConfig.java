package com.project.tour.config;

import java.time.Duration;
import java.util.List;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.project.tour.client.auth.ShoreStaffResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthStaffCacheConfig {

    @Bean("serviceTokenCache")
    public Cache<String, String> serviceTokenCache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofMinutes(8))
                .maximumSize(10)
                .build();
    }

    @Bean("shoreStaffCache")
    public Cache<String, List<ShoreStaffResponse>> shoreStaffCache(
            @Value("${shore.staff-cache.ttl-minutes:2}") long ttlMinutes,
            @Value("${shore.staff-cache.max-size:500}") long maxSize) {

        return Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofMinutes(ttlMinutes))
                .maximumSize(maxSize)
                .build();
    }
}