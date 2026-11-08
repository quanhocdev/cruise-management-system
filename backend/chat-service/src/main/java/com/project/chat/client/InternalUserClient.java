package com.project.chat.client;

import com.project.chat.dto.response.ManagerInfoResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class InternalUserClient {

    private final RestClient restClient;
    private final ServiceTokenClient serviceTokenClient;

    public InternalUserClient(
            RestClient.Builder restClientBuilder,
            ServiceTokenClient serviceTokenClient,
            @Value("${auth-service.url}") String authServiceUrl) {
        this.restClient = restClientBuilder
                .baseUrl(authServiceUrl)
                .build();

        this.serviceTokenClient = serviceTokenClient;
    }

    public List<ManagerInfoResponse> getManagersByRoleName(String roleName) {

        String accessToken = serviceTokenClient.getAccessToken();

        List<ManagerInfoResponse> response = restClient.get()
                .uri("/api/internal/users/by-role-name/{roleName}", roleName)
                .header(
                        HttpHeaders.AUTHORIZATION,
                        "Bearer " + accessToken)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });

        return response != null ? response : List.of();
    }
}