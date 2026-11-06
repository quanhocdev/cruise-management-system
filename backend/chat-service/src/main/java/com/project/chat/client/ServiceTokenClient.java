package com.project.chat.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.benmanes.caffeine.cache.Cache;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Component
public class ServiceTokenClient {

    private static final String CACHE_KEY = "chat-service-token";

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
            @Value("${service.oauth2.scope}") String scope) {
        this.restClient = restClientBuilder
                .baseUrl(authServiceUrl)
                .build();

        this.serviceTokenCache = serviceTokenCache;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.scope = scope;
    }

    public String getAccessToken() {
        String cachedToken = serviceTokenCache.getIfPresent(CACHE_KEY);

        if (cachedToken != null) {
            return cachedToken;
        }

        return requestNewAccessToken();
    }

    private synchronized String requestNewAccessToken() {

        // Kiểm tra lại để tránh nhiều request cùng xin token
        // khi có nhiều request đến Chat Service đồng thời.
        String cachedToken = serviceTokenCache.getIfPresent(CACHE_KEY);

        if (cachedToken != null) {
            return cachedToken;
        }

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();

        formData.add("grant_type", "client_credentials");
        formData.add("scope", scope);

        ServiceTokenResponse response = restClient.post()
                .uri("/oauth2/token")
                .headers(headers -> headers.setBasicAuth(clientId, clientSecret))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(formData)
                .retrieve()
                .body(ServiceTokenResponse.class);

        if (response == null
                || response.getAccessToken() == null
                || response.getAccessToken().isBlank()) {
            throw new IllegalStateException(
                    "Cannot obtain access token from Auth Service");
        }

        String accessToken = response.getAccessToken();

        serviceTokenCache.put(CACHE_KEY, accessToken);

        return accessToken;
    }

    public static class ServiceTokenResponse {

        @JsonProperty("access_token")
        private String accessToken;

        @JsonProperty("token_type")
        private String tokenType;

        @JsonProperty("scope")
        private String scope;

        @JsonProperty("expires_in")
        private Long expiresIn;

        public String getAccessToken() {
            return accessToken;
        }

        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }

        public String getTokenType() {
            return tokenType;
        }

        public void setTokenType(String tokenType) {
            this.tokenType = tokenType;
        }

        public String getScope() {
            return scope;
        }

        public void setScope(String scope) {
            this.scope = scope;
        }

        public Long getExpiresIn() {
            return expiresIn;
        }

        public void setExpiresIn(Long expiresIn) {
            this.expiresIn = expiresIn;
        }
    }
}