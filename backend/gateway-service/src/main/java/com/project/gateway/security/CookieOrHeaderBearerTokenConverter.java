package com.project.gateway.security;

import org.springframework.http.HttpCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.web.server.authentication.ServerBearerTokenAuthenticationConverter;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

public class CookieOrHeaderBearerTokenConverter
        implements ServerAuthenticationConverter {

    private static final String ACCESS_TOKEN_COOKIE = "accessToken";

    private final ServerBearerTokenAuthenticationConverter headerConverter;

    public CookieOrHeaderBearerTokenConverter(
            ServerBearerTokenAuthenticationConverter headerConverter) {

        this.headerConverter = headerConverter;
    }

    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {

        // =====================================================
        // 1. Android
        // Authorization: Bearer <token>
        // =====================================================

        return headerConverter.convert(exchange)

                // =================================================
                // 2. Web
                // Cookie: accessToken
                // =================================================

                .switchIfEmpty(
                        Mono.defer(
                                () -> tokenFromCookie(exchange)
                        )
                );
    }

    private Mono<Authentication> tokenFromCookie(
            ServerWebExchange exchange) {

        HttpCookie cookie =
                exchange.getRequest()
                        .getCookies()
                        .getFirst(ACCESS_TOKEN_COOKIE);

        if (cookie == null || cookie.getValue().isBlank()) {
            return Mono.empty();
        }

        return Mono.just(
                new BearerTokenAuthenticationToken(
                        cookie.getValue()
                )
        );
    }
}