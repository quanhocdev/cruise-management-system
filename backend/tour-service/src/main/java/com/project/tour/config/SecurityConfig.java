package com.project.tour.config;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Arrays;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
        org.springframework.security.config.annotation.web.builders.HttpSecurity http,
        BearerTokenResolver bearerTokenResolver,
        JwtAuthenticationConverter jwtAuthenticationConverter
    ) throws Exception {

        return http
            .csrf(csrf -> csrf.disable())

            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers(HttpMethod.OPTIONS, "/**")
                .permitAll()

                .requestMatchers(
                    "/actuator/health",
                    "/actuator/info"
                )
                .permitAll()

                // Port management
                .requestMatchers(HttpMethod.POST, "/api/v1/ports")
                .hasAnyRole("ADMIN", "SCHEDULER")

                .requestMatchers(HttpMethod.PUT, "/api/v1/ports/**")
                .hasAnyRole("ADMIN", "SCHEDULER")

                .requestMatchers(HttpMethod.PATCH, "/api/v1/ports/**")
                .hasAnyRole("ADMIN", "SCHEDULER")

                .requestMatchers(HttpMethod.GET, "/api/v1/ports/**")
                .authenticated()

                // Cruise management
                .requestMatchers(HttpMethod.POST, "/api/v1/cruises")
                .hasRole("ADMIN")

                // Cruise deck management
                .requestMatchers(
                    HttpMethod.POST,
                    "/api/v1/cruises/*/decks"
                )
                .hasRole("ADMIN")

                .requestMatchers(
                    HttpMethod.PUT,
                    "/api/v1/cruises/*/decks/**"
                )
                .hasRole("ADMIN")

                .requestMatchers(
                    HttpMethod.PATCH,
                    "/api/v1/cruises/*/decks/**"
                )
                .hasRole("ADMIN")

                .requestMatchers(
                    HttpMethod.GET,
                    "/api/v1/cruises/*/decks/**"
                )
                .authenticated()

                .requestMatchers(HttpMethod.PUT, "/api/v1/cruises/**")
                .hasRole("ADMIN")

                .requestMatchers(HttpMethod.PATCH, "/api/v1/cruises/**")
                .hasRole("ADMIN")

                .requestMatchers(HttpMethod.GET, "/api/v1/cruises/**")
                .authenticated()

                // Room type management
                .requestMatchers(HttpMethod.POST, "/api/v1/room-types")
                .hasRole("ADMIN")

                .requestMatchers(
                    HttpMethod.PUT,
                    "/api/v1/room-types/**"
                )
                .hasRole("ADMIN")

                .requestMatchers(
                    HttpMethod.DELETE,
                    "/api/v1/room-types/**"
                )
                .hasRole("ADMIN")

                .requestMatchers(HttpMethod.GET, "/api/v1/room-types/**")
                .authenticated()

                // Room management
                .requestMatchers(HttpMethod.POST, "/api/v1/decks/*/rooms")
                .hasRole("ADMIN")

                .requestMatchers(HttpMethod.PUT, "/api/v1/decks/*/rooms/**")
                .hasRole("ADMIN")

                .requestMatchers(HttpMethod.PATCH, "/api/v1/decks/*/rooms/**")
                .hasRole("ADMIN")

                .requestMatchers(HttpMethod.GET, "/api/v1/decks/*/rooms/**")
                .authenticated()

                .anyRequest()
                .authenticated()
            )
            .oauth2ResourceServer(resourceServer -> resourceServer
                .bearerTokenResolver(bearerTokenResolver)
                .jwt(jwt -> jwt
                    .jwtAuthenticationConverter(jwtAuthenticationConverter)
                )
            )

            .build();
    }

    @Bean
    public BearerTokenResolver bearerTokenResolver() {
        DefaultBearerTokenResolver headerResolver =
            new DefaultBearerTokenResolver();

        return request -> {
            String cookieToken = findAccessTokenCookie(request);

            if (cookieToken != null) {
                return cookieToken;
            }

            return headerResolver.resolve(request);
        };
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter authoritiesConverter =
            new JwtGrantedAuthoritiesConverter();

        authoritiesConverter.setAuthoritiesClaimName("scope");
        authoritiesConverter.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter authenticationConverter =
            new JwtAuthenticationConverter();

        authenticationConverter.setJwtGrantedAuthoritiesConverter(
            authoritiesConverter
        );

        return authenticationConverter;
    }

    private String findAccessTokenCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            return null;
        }

        return Arrays.stream(cookies)
            .filter(cookie -> "accessToken".equals(cookie.getName()))
            .map(Cookie::getValue)
            .filter(value -> !value.isBlank())
            .findFirst()
            .orElse(null);
    }
}
