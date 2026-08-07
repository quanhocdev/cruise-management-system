package com.project.gateway.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.web.server.authentication.ServerBearerTokenAuthenticationConverter;

class CookieOrHeaderBearerTokenConverterTests {

    private final CookieOrHeaderBearerTokenConverter converter =
            new CookieOrHeaderBearerTokenConverter(new ServerBearerTokenAuthenticationConverter());

    @Test
    void readsTokenFromAuthorizationHeaderFirst() {
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/tours")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer header-token")
                        .cookie(new HttpCookie("accessToken", "cookie-token")));

        BearerTokenAuthenticationToken authentication =
                (BearerTokenAuthenticationToken) converter.convert(exchange).block();

        assertThat(authentication).isNotNull();
        assertThat(authentication.getToken()).isEqualTo("header-token");
    }

    @Test
    void fallsBackToAccessTokenCookie() {
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/tours")
                        .cookie(new HttpCookie("accessToken", "cookie-token")));

        BearerTokenAuthenticationToken authentication =
                (BearerTokenAuthenticationToken) converter.convert(exchange).block();

        assertThat(authentication).isNotNull();
        assertThat(authentication.getToken()).isEqualTo("cookie-token");
    }

    @Test
    void returnsEmptyWhenRequestHasNoToken() {
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/tours"));

        assertThat(converter.convert(exchange).block()).isNull();
    }
}
