package com.project.auth.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;

import jakarta.servlet.http.Cookie;

class CookieOrHeaderBearerTokenResolverTests {

    private final CookieOrHeaderBearerTokenResolver resolver =
            new CookieOrHeaderBearerTokenResolver();

    @Test
    void ignoresExpiredAccessCookieOnRefreshEndpoint() {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/auth/refresh");
        request.setCookies(
                new Cookie("accessToken", "expired-access-token"),
                new Cookie("refreshToken", "valid-refresh-token"));

        assertThat(resolver.resolve(request)).isNull();
    }

    @Test
    void readsAccessCookieOnPrivateEndpoint() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/auth/me");
        request.setCookies(new Cookie("accessToken", "access-token"));

        assertThat(resolver.resolve(request)).isEqualTo("access-token");
    }

    @Test
    void fallsBackToAuthorizationHeaderOnPrivateEndpoint() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/auth/me");
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer header-token");

        assertThat(resolver.resolve(request)).isEqualTo("header-token");
    }

    @Test
    void ignoresExpiredAccessCookieOnLogoutEndpoint() {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/auth/logout");
        request.setCookies(new Cookie("accessToken", "expired-access-token"));

        assertThat(resolver.resolve(request)).isNull();
    }
}
