
package com.project.gateway.filter;

import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.PathContainer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

import com.project.gateway.config.RateLimitProperties;
import com.project.gateway.config.RateLimitProperties.Rule;
import com.project.gateway.service.RedisRateLimitService;

import reactor.core.publisher.Mono;

@Component
public class GlobalRateLimitFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(GlobalRateLimitFilter.class);

    private static final String CLIENT_IP_HEADER = "X-Client-IP";

    private final RateLimitProperties rateLimitProperties;
    private final RedisRateLimitService redisRateLimitService;
    private final PathPatternParser pathPatternParser = new PathPatternParser();

    public GlobalRateLimitFilter(
            RateLimitProperties rateLimitProperties,
            RedisRateLimitService redisRateLimitService) {

        this.rateLimitProperties = rateLimitProperties;
        this.redisRateLimitService = redisRateLimitService;
    }

    @Override
    public Mono<Void> filter(
            ServerWebExchange exchange,
            GatewayFilterChain chain) {

        if (!rateLimitProperties.isEnabled()) {
            return chain.filter(exchange);
        }

        Rule matchedRule = findMatchedRule(exchange);

        // Request không thuộc rule nào thì không giới hạn.
        if (matchedRule == null) {
            return chain.filter(exchange);
        }

        String clientIp = exchange.getRequest()
                .getHeaders()
                .getFirst(CLIENT_IP_HEADER);

        if (clientIp == null
                || clientIp.isBlank()
                || "unknown".equalsIgnoreCase(clientIp)) {

            log.warn(
                    "[RATE LIMIT] Cannot determine client IP. Path: {}",
                    exchange.getRequest().getURI().getPath());

            return writeError(
                    exchange,
                    HttpStatus.BAD_REQUEST,
                    "Cannot determine client IP");
        }

        // Lấy Authentication từ Reactive Security Context.
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .flatMap(authentication -> processAuthenticatedRequest(
                        exchange,
                        chain,
                        matchedRule,
                        clientIp,
                        authentication))
                .switchIfEmpty(writeError(
                        exchange,
                        HttpStatus.UNAUTHORIZED,
                        "Authentication required"));
    }

    private Mono<Void> processAuthenticatedRequest(
            ServerWebExchange exchange,
            GatewayFilterChain chain,
            Rule rule,
            String clientIp,
            Authentication authentication) {

        if (!(authentication instanceof JwtAuthenticationToken jwtAuthentication)
                || !authentication.isAuthenticated()) {

            return writeError(
                    exchange,
                    HttpStatus.UNAUTHORIZED,
                    "Invalid authentication");
        }

        String userId = jwtAuthentication.getToken()
                .getClaimAsString("userId");

        String tokenType = jwtAuthentication.getToken()
                .getClaimAsString("tokenType");

        // Chỉ chấp nhận Access Token có userId.
        if (userId == null
                || userId.isBlank()
                || !"ACCESS".equals(tokenType)) {

            return writeError(
                    exchange,
                    HttpStatus.UNAUTHORIZED,
                    "Invalid access token");
        }

        log.debug(
                "[RATE LIMIT] Checking rule: {}, User: {}, IP: {}",
                rule.getId(),
                userId,
                clientIp);

        /*
         * Hai bộ đếm được kiểm tra độc lập.
         * Mỗi request đều tăng cả counter User ID và IP.
         */
        return Mono.zip(
                redisRateLimitService.isAllowed(
                        rule.getId(),
                        "user",
                        userId,
                        rule.getUserLimit(),
                        rule.getWindow()),

                redisRateLimitService.isAllowed(
                        rule.getId(),
                        "ip",
                        clientIp,
                        rule.getIpLimit(),
                        rule.getWindow()))
                .map(result -> new RateLimitCheck(
                        true,
                        result.getT1() && result.getT2()))
                .onErrorResume(exception -> {
                    log.error(
                            "[RATE LIMIT] Redis error. Rule: {}, User: {}, IP: {}",
                            rule.getId(),
                            userId,
                            clientIp,
                            exception);

                    return Mono.just(new RateLimitCheck(false, false));
                })
                .flatMap(result -> {

                    // Redis lỗi thì không bỏ qua kiểm tra Rate Limit.
                    if (!result.available()) {
                        return writeError(
                                exchange,
                                HttpStatus.SERVICE_UNAVAILABLE,
                                "Rate limit service temporarily unavailable");
                    }

                    // Một trong hai quota vượt giới hạn.
                    if (!result.allowed()) {

                        log.warn(
                                "[RATE LIMIT] BLOCKED - Rule: {}, User: {}, IP: {}",
                                rule.getId(),
                                userId,
                                clientIp);

                        return writeError(
                                exchange,
                                HttpStatus.TOO_MANY_REQUESTS,
                                "Too many requests. Please try again later.");
                    }

                    // Cả hai quota còn hợp lệ.
                    return chain.filter(exchange);
                });
    }

    private Rule findMatchedRule(ServerWebExchange exchange) {

        String requestMethod = exchange.getRequest()
                .getMethod()
                .name();

        PathContainer requestPath = PathContainer.parsePath(
                exchange.getRequest().getURI().getPath());

        for (Rule rule : rateLimitProperties.getRules()) {

            if (!rule.getMethod().equalsIgnoreCase(requestMethod)) {
                continue;
            }

            PathPattern pattern = pathPatternParser.parse(rule.getPath());

            if (pattern.matches(requestPath)) {
                return rule;
            }
        }

        return null;
    }

    private Mono<Void> writeError(
            ServerWebExchange exchange,
            HttpStatus status,
            String message) {

        exchange.getResponse().setStatusCode(status);

        exchange.getResponse()
                .getHeaders()
                .setContentType(MediaType.APPLICATION_JSON);

        String body = """
                {
                    "status": %d,
                    "message": "%s"
                }
                """.formatted(status.value(), message);

        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);

        return exchange.getResponse().writeWith(
                Mono.just(
                        exchange.getResponse()
                                .bufferFactory()
                                .wrap(bytes)));
    }

    private record RateLimitCheck(
            boolean available,
            boolean allowed) {
    }

    @Override
    public int getOrder() {

        // Chạy sau ClientIpGlobalFilter.
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }
}
