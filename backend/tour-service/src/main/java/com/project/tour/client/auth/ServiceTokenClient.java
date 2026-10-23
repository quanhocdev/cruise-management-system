package com.project.tour.client.auth;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import com.github.benmanes.caffeine.cache.Cache;

@Component
public class ServiceTokenClient {

    private static final String TOKEN_CACHE_KEY = "tour-service-token";

    private final RestClient restClient;
    private final Cache<String, String> serviceTokenCache;

    private final String clientId;
    private final String clientSecret;
    private final String scope;

    public ServiceTokenClient(
            RestClient.Builder restClientBuilder,
            Cache<String, String> serviceTokenCache,
            @Value("${auth-service.url}") String authServiceUrl,
            @Value("${service.oauth2.client-id}") String clientId,
            @Value("${service.oauth2.client-secret}") String clientSecret,
            @Value("${service.oauth2.scope:staff.read}") String scope) {

        this.restClient = restClientBuilder
                .baseUrl(authServiceUrl)
                .build();

        this.serviceTokenCache = serviceTokenCache;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.scope = scope;
    }

    public String getAccessToken() {
        String cachedToken = serviceTokenCache.getIfPresent(TOKEN_CACHE_KEY);

        if (cachedToken != null) {
            return cachedToken;
        }

        synchronized (this) {
            cachedToken = serviceTokenCache.getIfPresent(TOKEN_CACHE_KEY);

            if (cachedToken != null) {
                return cachedToken;
            }

            return requestNewAccessToken();
        }
    }

    private String requestNewAccessToken() {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "client_credentials");
        form.add("scope", scope);

        TokenResponse response = restClient.post()
                .uri("/oauth2/token")
                .headers(headers -> headers.setBasicAuth(clientId, clientSecret))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(form)
                .retrieve()
                .body(TokenResponse.class);

        if (response == null || response.access_token() == null
                || response.access_token().isBlank()) {
            throw new IllegalStateException(
                    "Auth-service returned an empty service access token");
        }

        // Auth-service issues tokens for 10 minutes.
        // Keep the cache TTL shorter to avoid using an expired token.
        serviceTokenCache.put(
                TOKEN_CACHE_KEY,
                response.access_token());

        return response.access_token();
    }

    private record TokenResponse(
            String access_token,
            String token_type,
            String scope,
            long expires_in) {
    }
}