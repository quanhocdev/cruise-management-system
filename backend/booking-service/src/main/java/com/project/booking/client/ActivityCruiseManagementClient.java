package com.project.booking.client;

import com.project.booking.dto.onboard.ActivityCruiseUsageManagementInfo;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Component
public class ActivityCruiseManagementClient {

    private final RestClient restClient;
    private final ServiceTokenClient serviceTokenClient;

    public ActivityCruiseManagementClient(
            RestClient.Builder restClientBuilder,
            @Value("${tour-service.url}") String tourServiceUrl,
            ServiceTokenClient serviceTokenClient) {

        this.restClient = restClientBuilder
                .baseUrl(tourServiceUrl)
                .build();

        this.serviceTokenClient = serviceTokenClient;
    }

    @Retry(name = "tourService")
    @CircuitBreaker(name = "tourService")
    public List<ActivityCruiseUsageManagementInfo> getActivityCruiseUsageInfo(
            List<UUID> activityCruiseTourIds) {

        if (activityCruiseTourIds == null
                || activityCruiseTourIds.isEmpty()) {
            return Collections.emptyList();
        }

        String accessToken = serviceTokenClient.getAccessToken();

        ActivityCruiseUsageManagementInfo[] response = restClient
                .post()
                .uri("/internal/activity-cruise-tours/usage-info/batch")
                .headers(headers -> headers.setBearerAuth(accessToken))
                .body(new ActivityCruiseUsageBatchRequest(
                        activityCruiseTourIds))
                .retrieve()
                .body(ActivityCruiseUsageManagementInfo[].class);

        return response != null
                ? Arrays.asList(response)
                : Collections.emptyList();
    }

    private record ActivityCruiseUsageBatchRequest(
            List<UUID> activityCruiseTourIds) {
    }
}