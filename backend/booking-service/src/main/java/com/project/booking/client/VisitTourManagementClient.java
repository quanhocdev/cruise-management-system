
package com.project.booking.client;

import com.project.booking.dto.shore.ActivityVisitUsageBatchRequest;
import com.project.booking.dto.shore.ActivityVisitUsageManagementInfo;
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
public class VisitTourManagementClient {

    private final RestClient restClient;
    private final ServiceTokenClient serviceTokenClient;

    public VisitTourManagementClient(
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
    public List<ActivityVisitUsageManagementInfo> getVisitTourUsageInfo(
            List<UUID> visitTourIds) {

        if (visitTourIds == null || visitTourIds.isEmpty()) {
            return Collections.emptyList();
        }

        String accessToken = serviceTokenClient.getAccessToken();

        ActivityVisitUsageManagementInfo[] response = restClient
                .post()
                .uri("/internal/visit-tours/usage-info/batch")
                .headers(headers -> headers.setBearerAuth(accessToken))
                .body(new ActivityVisitUsageBatchRequest(visitTourIds))
                .retrieve()
                .body(ActivityVisitUsageManagementInfo[].class);

        return response != null
                ? Arrays.asList(response)
                : Collections.emptyList();
    }
}
