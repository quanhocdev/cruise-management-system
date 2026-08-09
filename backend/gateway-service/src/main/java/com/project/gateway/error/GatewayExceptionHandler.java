package com.project.gateway.error;

import java.net.ConnectException;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.webflux.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
@Order(-2)
public class GatewayExceptionHandler implements ErrorWebExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GatewayExceptionHandler.class);

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable exception) {
        HttpStatus status = hasCause(exception, ConnectException.class)
                ? HttpStatus.SERVICE_UNAVAILABLE
                : HttpStatus.INTERNAL_SERVER_ERROR;

        String message = status == HttpStatus.SERVICE_UNAVAILABLE
                ? "Downstream service unavailable"
                : "Internal gateway error";

        log.error("Gateway request failed: method={}, path={}, status={}",
                exchange.getRequest().getMethod(),
                exchange.getRequest().getPath(),
                status.value(),
                exception);

        if (exchange.getResponse().isCommitted()) {
            return Mono.error(exception);
        }

        byte[] body = ("{\"status\":" + status.value()
                + ",\"message\":\"" + message + "\"}")
                .getBytes(StandardCharsets.UTF_8);

        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(body);
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }

    private boolean hasCause(Throwable exception, Class<? extends Throwable> type) {
        Throwable current = exception;
        while (current != null) {
            if (type.isInstance(current)) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }
}
