
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
public class AuthConvenienceStaffClient {

    private static final String CACHE_KEY = "convenience-staff-list";
    private static final String CONVENIENCE_STAFF_ROLE = "CONVENIENCE_STAFF";

    private final RestClient restClient;
    private final ServiceTokenClient serviceTokenClient;
    private final Cache<String, List<ShoreStaffResponse>> convenienceStaffCache;

    public AuthConvenienceStaffClient(
            RestClient.Builder restClientBuilder,
            ServiceTokenClient serviceTokenClient,
            @Qualifier("convenienceStaffCache") Cache<String, List<ShoreStaffResponse>> convenienceStaffCache,
            @Value("${auth-service.url}") String authServiceUrl) {

        this.restClient = restClientBuilder
                .baseUrl(authServiceUrl)
                .build();

        this.serviceTokenClient = serviceTokenClient;
        this.convenienceStaffCache = convenienceStaffCache;
    }

    public List<ShoreStaffResponse> getAllConvenienceStaff() {
        List<ShoreStaffResponse> cached = convenienceStaffCache.getIfPresent(CACHE_KEY);

        if (cached != null) {
            return cached;
        }

        synchronized (this) {
            cached = convenienceStaffCache.getIfPresent(CACHE_KEY);

            if (cached != null) {
                return cached;
            }

            ShoreStaffResponse[] response = restClient.get()
                    .uri("/internal/staff/convenience")
                    .headers(headers -> headers.setBearerAuth(
                            serviceTokenClient.getAccessToken()))
                    .retrieve()
                    .body(ShoreStaffResponse[].class);

            List<ShoreStaffResponse> staff = response == null
                    ? List.of()
                    : java.util.Arrays.stream(response)
                            .filter(item -> CONVENIENCE_STAFF_ROLE.equals(item.role()))
                            .toList();

            convenienceStaffCache.put(CACHE_KEY, staff);
            return staff;
        }
    }

    public List<ShoreStaffResponse> getConvenienceStaffByIds(List<UUID> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return List.of();
        }

        Set<UUID> requestedIds = userIds.stream()
                .filter(id -> id != null)
                .collect(Collectors.toSet());

        return getAllConvenienceStaff().stream()
                .filter(staff -> requestedIds.contains(staff.userId()))
                .toList();
    }
}
