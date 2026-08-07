package com.project.gateway.security;

import java.nio.charset.StandardCharsets;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

public class JsonAuthenticationEntryPoint implements ServerAuthenticationEntryPoint {

    private static final byte[] BODY = "{\"status\":401,\"message\":\"Authentication required\"}"
            .getBytes(StandardCharsets.UTF_8);

    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException exception) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(BODY);
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }
}
