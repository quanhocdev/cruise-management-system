
package com.project.tour.client.auth;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class AuthOnboardStaffClient {

    private static final String ONBOARD_STAFF_ROLE = "ONBOARD_STAFF";

    private final RestClient restClient;
    private final ServiceTokenClient serviceTokenClient;

    public AuthOnboardStaffClient(
            RestClient.Builder restClientBuilder,
            ServiceTokenClient serviceTokenClient,
            @Value("${auth-service.url}") String authServiceUrl) {

        this.restClient = restClientBuilder
                .baseUrl(authServiceUrl)
                .build();

        this.serviceTokenClient = serviceTokenClient;
    }

    public List<ShoreStaffResponse> getAllOnboardStaff() {

        ShoreStaffResponse[] response = restClient.get()
                .uri("/internal/staff/onboard")
                .headers(headers -> headers.setBearerAuth(
                        serviceTokenClient.getAccessToken()))
                .retrieve()
                .body(ShoreStaffResponse[].class);

        if (response == null) {
            return List.of();
        }

        return java.util.Arrays.stream(response)
                .filter(item -> ONBOARD_STAFF_ROLE.equals(item.role()))
                .toList();
    }

    public List<ShoreStaffResponse> getOnboardStaffByIds(List<UUID> userIds) {

        if (userIds == null || userIds.isEmpty()) {
            return List.of();
        }

        Set<UUID> requestedIds = userIds.stream()
                .filter(id -> id != null)
                .collect(Collectors.toSet());

        return getAllOnboardStaff().stream()
                .filter(staff -> requestedIds.contains(staff.userId()))
                .toList();
    }
}
