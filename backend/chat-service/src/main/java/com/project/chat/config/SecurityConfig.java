
package com.project.chat.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jakarta.servlet.http.Cookie;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            BearerTokenResolver bearerTokenResolver,
            JwtAuthenticationConverter jwtAuthenticationConverter) throws Exception {

        return http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(
                                "/actuator/health",
                                "/actuator/info")
                        .permitAll()

                        .requestMatchers("/ws-chat-native/**").authenticated()
                        .requestMatchers("/ws-chat/**").permitAll()

                        .requestMatchers("/api/chat/**").authenticated()

                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth -> oauth
                        .bearerTokenResolver(bearerTokenResolver)
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(jwtAuthenticationConverter)))
                .build();
    }

    /**
     * Ưu tiên Bearer token, sau đó mới đọc accessToken cookie.
     */
    @Bean
    public BearerTokenResolver bearerTokenResolver() {

        return request -> {

            String authorization = request.getHeader("Authorization");

            if (authorization != null
                    && authorization.startsWith("Bearer ")) {
                return authorization.substring(7);
            }

            Cookie[] cookies = request.getCookies();

            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("accessToken".equals(cookie.getName())
                            && cookie.getValue() != null
                            && !cookie.getValue().isBlank()) {
                        return cookie.getValue();
                    }
                }
            }

            return null;
        };
    }

    /**
     * Chuyển scope trong JWT thành Spring Security authorities.
     *
     * Ví dụ:
     * PASSENGER -> ROLE_PASSENGER
     * ONBOARD -> ROLE_ONBOARD
     * tour.read -> SCOPE_tour.read
     */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(jwt -> {

            List<GrantedAuthority> authorities = new ArrayList<>();

            Object rawScope = jwt.getClaims().get("scope");

            Collection<?> scopes;

            if (rawScope instanceof Collection<?> collection) {
                scopes = collection;
            } else if (rawScope instanceof String scopeString) {
                scopes = List.of(scopeString.trim().split("\\s+"));
            } else {
                scopes = List.of();
            }

            for (Object item : scopes) {

                String scope = String.valueOf(item).trim();

                if (scope.isBlank()) {
                    continue;
                }

                String authority = scope.contains(".")
                        ? "SCOPE_" + scope
                        : "ROLE_" + scope;

                authorities.add(
                        new SimpleGrantedAuthority(authority));
            }

            return authorities;
        });

        return converter;
    }

}
