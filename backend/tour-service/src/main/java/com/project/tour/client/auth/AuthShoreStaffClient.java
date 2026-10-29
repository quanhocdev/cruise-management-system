package com.project.tour.client.auth;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.github.benmanes.caffeine.cache.Cache;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class AuthShoreStaffClient {

    private static final String SHORE_STAFF_CACHE_KEY = "shore-staff-list";
    private static final String SHORE_STAFF_ROLE = "SHORE_STAFF";

    private final RestClient restClient;
    private final ServiceTokenClient serviceTokenClient;
    private final Cache<String, List<ShoreStaffResponse>> shoreStaffCache;

    public AuthShoreStaffClient(
            RestClient.Builder restClientBuilder,
            ServiceTokenClient serviceTokenClient,
            @Qualifier("shoreStaffCache") Cache<String, List<ShoreStaffResponse>> shoreStaffCache,
            @Value("${auth-service.url}") String authServiceUrl) {

        this.restClient = restClientBuilder
                .baseUrl(authServiceUrl)
                .build();

        this.serviceTokenClient = serviceTokenClient;
        this.shoreStaffCache = shoreStaffCache;
    }

    public List<ShoreStaffResponse> getAllShoreStaff() {
        List<ShoreStaffResponse> cached = shoreStaffCache.getIfPresent(
                SHORE_STAFF_CACHE_KEY);

        if (cached != null) {
            return cached;
        }

        synchronized (this) {
            cached = shoreStaffCache.getIfPresent(SHORE_STAFF_CACHE_KEY);

            if (cached != null) {
                return cached;
            }

            ShoreStaffResponse[] response = restClient.get()
                    .uri("/internal/staff/shore")
                    .headers(headers -> headers.setBearerAuth(
                            serviceTokenClient.getAccessToken()))
                    .retrieve()
                    .body(ShoreStaffResponse[].class);

            List<ShoreStaffResponse> staff = response == null
                    ? List.of()
                    : List.of(response).stream()
                            .filter(item -> SHORE_STAFF_ROLE.equals(item.role()))
                            .toList();

            shoreStaffCache.put(SHORE_STAFF_CACHE_KEY, staff);
            return staff;
        }
    }

    public List<ShoreStaffResponse> getShoreStaffByIds(List<UUID> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return List.of();
        }

        Set<UUID> requestedIds = userIds.stream()
                .filter(id -> id != null)
                .collect(Collectors.toSet());

        return getAllShoreStaff().stream()
                .filter(staff -> requestedIds.contains(staff.userId()))
                .toList();
    }
}