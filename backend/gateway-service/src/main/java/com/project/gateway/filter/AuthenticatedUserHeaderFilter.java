// package com.project.gateway.filter;

// import org.springframework.cloud.gateway.filter.GatewayFilterChain;
// import org.springframework.cloud.gateway.filter.GlobalFilter;
// import org.springframework.core.Ordered;
// import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
// import org.springframework.stereotype.Component;
// import org.springframework.web.server.ServerWebExchange;

// import reactor.core.publisher.Mono;

// @Component
// public class AuthenticatedUserHeaderFilter implements GlobalFilter, Ordered {

//     public static final String USER_ID_HEADER = "X-User-Id";
//     public static final String USER_ROLE_HEADER = "X-User-Role";

//     @Override
//     public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//         return exchange.getPrincipal()
//                 .filter(JwtAuthenticationToken.class::isInstance)
//                 .cast(JwtAuthenticationToken.class)
//                 .map(authentication -> withAuthenticatedUser(exchange, authentication))
//                 .defaultIfEmpty(withoutTrustedHeaders(exchange))
//                 .flatMap(chain::filter);
//     }

//     private ServerWebExchange withAuthenticatedUser(
//             ServerWebExchange exchange,
//             JwtAuthenticationToken authentication) {

//         String userId = authentication.getToken().getSubject();
//         String role = authentication.getToken().getClaimAsString("scope");

//         return exchange.mutate()
//                 .request(request -> request.headers(headers -> {
//                     headers.remove(USER_ID_HEADER);
//                     headers.remove(USER_ROLE_HEADER);
//                     headers.set(USER_ID_HEADER, userId);
//                     if (role != null && !role.isBlank()) {
//                         headers.set(USER_ROLE_HEADER, role);
//                     }
//                 }))
//                 .build();
//     }

//     private ServerWebExchange withoutTrustedHeaders(ServerWebExchange exchange) {
//         return exchange.mutate()
//                 .request(request -> request.headers(headers -> {
//                     headers.remove(USER_ID_HEADER);
//                     headers.remove(USER_ROLE_HEADER);
//                 }))
//                 .build();
//     }

//     @Override
//     public int getOrder() {
//         return -1;
//     }
// }
