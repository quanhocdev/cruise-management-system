package com.project.booking.client;

import com.github.benmanes.caffeine.cache.Cache;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Component
public class ServiceTokenClient {

    private static final String CACHE_KEY = "booking-service-token";

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

            System.out.println(
                    ">>> [OAUTH2 CACHE] Lấy service token từ Caffeine");

            return cachedToken;
        }

        System.out.println(
                ">>> [OAUTH2 CACHE] Không tìm thấy token trong Caffeine");

        System.out.println(
                ">>> [OAUTH2] Đang gọi Auth Service để lấy token mới...");

        return requestNewAccessToken();
    }

    private synchronized String requestNewAccessToken() {

        // Double-check sau khi chờ lock
        String cachedToken = serviceTokenCache.getIfPresent(CACHE_KEY);

        if (cachedToken != null) {

            System.out.println(
                    ">>> [OAUTH2 CACHE] Token đã được thread khác lấy trước đó");

            return cachedToken;
        }

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();

        formData.add(
                "grant_type",
                "client_credentials");

        formData.add(
                "scope",
                scope);

        System.out.println(
                ">>> [OAUTH2] POST /oauth2/token");

        ServiceTokenResponse response = restClient
                .post()
                .uri("/oauth2/token")
                .headers(headers -> headers.setBasicAuth(
                        clientId,
                        clientSecret))
                .contentType(
                        MediaType.APPLICATION_FORM_URLENCODED)
                .body(formData)
                .retrieve()
                .body(ServiceTokenResponse.class);

        if (response == null
                || response.getAccessToken() == null) {

            throw new IllegalStateException(
                    "Auth Service không trả về service access token");
        }

        serviceTokenCache.put(
                CACHE_KEY,
                response.getAccessToken());

        System.out.println(
                ">>> [OAUTH2 CACHE] Đã lưu service token vào Caffeine"
                        + " | expires_in="
                        + response.getExpiresIn()
                        + "s");

        return response.getAccessToken();
    }

    public static class ServiceTokenResponse {

        private String access_token;
        private String token_type;
        private String scope;
        private long expires_in;

        public String getAccessToken() {
            return access_token;
        }

        public void setAccessToken(String accessToken) {
            this.access_token = accessToken;
        }

        public String getTokenType() {
            return token_type;
        }

        public void setTokenType(String tokenType) {
            this.token_type = tokenType;
        }

        public String getScope() {
            return scope;
        }

        public void setScope(String scope) {
            this.scope = scope;
        }

        public long getExpiresIn() {
            return expires_in;
        }

        public void setExpiresIn(long expiresIn) {
            this.expires_in = expiresIn;
        }
    }
}