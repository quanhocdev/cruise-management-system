package com.project.tour.config;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            BearerTokenResolver bearerTokenResolver,
            JwtAuthenticationConverter jwtAuthenticationConverter)
            throws Exception {

        return http
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session -> session
                        .sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(authorize -> authorize

                        // CORS preflight
                        .requestMatchers(HttpMethod.OPTIONS, "/**")
                        .permitAll()

                        // Public endpoints
                        .requestMatchers(
                                "/actuator/health",
                                "/actuator/info",
                                "/api/public/**")
                        .permitAll()

                        // Admin
                        .requestMatchers("/api/admin/**")
                        .hasRole("ADMIN")

                        // Scheduler
                        .requestMatchers("/api/scheduler/**")
                        .hasRole("SCHEDULER")

                        // Convenience
                        .requestMatchers("/api/convenience/**")
                        .hasRole("CONVENIENCE")

                        // Operation
                        .requestMatchers("/api/operation/**")
                        .hasRole("OPERATION")

                        // Finance
                        .requestMatchers("/api/finance/**")
                        .hasRole("FINANCE")

                        // Onboard
                        .requestMatchers("/api/onboard/**")
                        .hasRole("ONBOARD")

                        // Shore
                        .requestMatchers("/api/shore/**")
                        .hasRole("SHORE")

                        // Passenger
                        .requestMatchers("/api/passenger/**")
                        .hasRole("PASSENGER")

                        // Internal service-to-service endpoints
                        .requestMatchers("/internal/**")
                        .hasAuthority("SCOPE_tour.read")

                        // Everything else
                        .anyRequest()
                        .authenticated())

                .oauth2ResourceServer(resourceServer -> resourceServer
                        .bearerTokenResolver(bearerTokenResolver)
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(
                                        jwtAuthenticationConverter)))

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

        JwtAuthenticationConverter converter =
                new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(
                new MixedScopeAuthoritiesConverter());

        return converter;
    }

    /**
     * Chuyển scope trong JWT thành authority phù hợp.
     *
     * User JWT:
     *     scope = ADMIN
     *     scope = PASSENGER
     *
     * -> ROLE_ADMIN
     * -> ROLE_PASSENGER
     *
     * Service JWT:
     *     scope = tour.read
     *
     * -> SCOPE_tour.read
     */
    private static class MixedScopeAuthoritiesConverter
            implements Converter<Jwt, Collection<GrantedAuthority>> {

        @Override
        public Collection<GrantedAuthority> convert(Jwt jwt) {

            Object scopeClaim = jwt.getClaims().get("scope");

            if (scopeClaim == null) {
                return List.of();
            }

            List<String> scopes = new ArrayList<>();

            if (scopeClaim instanceof String scopeString) {

                scopes.addAll(
                        Arrays.stream(scopeString.split(" "))
                                .filter(scope -> !scope.isBlank())
                                .toList()
                );

            } else if (scopeClaim instanceof Collection<?> collection) {

                for (Object value : collection) {

                    if (value != null) {
                        String scope = value.toString();

                        if (!scope.isBlank()) {
                            scopes.add(scope);
                        }
                    }
                }
            }

            List<GrantedAuthority> authorities = new ArrayList<>();

            for (String scope : scopes) {

                /*
                 * OAuth2 service scopes
                 *
                 * tour.read
                 * tour.write
                 *
                 * -> SCOPE_tour.read
                 */
                if (scope.contains(".")) {

                    authorities.add(
                            new SimpleGrantedAuthority(
                                    "SCOPE_" + scope));
                }

                /*
                 * Application roles
                 *
                 * ADMIN
                 * PASSENGER
                 * OPERATION
                 * ...
                 *
                 * -> ROLE_ADMIN
                 * -> ROLE_PASSENGER
                 * -> ROLE_OPERATION
                 */
                else {

                    authorities.add(
                            new SimpleGrantedAuthority(
                                    "ROLE_" + scope));
                }
            }

            return authorities;
        }
    }

    private String findAccessTokenCookie(
            HttpServletRequest request) {

        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            return null;
        }

        return Arrays.stream(cookies)
                .filter(cookie ->
                        "accessToken".equals(cookie.getName()))
                .map(Cookie::getValue)
                .filter(value -> !value.isBlank())
                .findFirst()
                .orElse(null);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }
}
