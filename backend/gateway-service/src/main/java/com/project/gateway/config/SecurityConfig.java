package com.project.gateway.config;

import com.project.gateway.security.CookieOrHeaderBearerTokenResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    @Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();

    configuration.setAllowedOrigins(
            List.of("http://localhost:5173")
    );

    configuration.setAllowedMethods(
            List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")
    );

    configuration.setAllowedHeaders(
            List.of("*")
    );

    configuration.setAllowCredentials(true);

    configuration.setExposedHeaders(
            List.of("Authorization", "Set-Cookie")
    );

    UrlBasedCorsConfigurationSource source =
            new UrlBasedCorsConfigurationSource();

    source.registerCorsConfiguration("/**", configuration);

    return source;
}

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {

        JwtGrantedAuthoritiesConverter scopeConverter =
                new JwtGrantedAuthoritiesConverter();

        scopeConverter.setAuthoritiesClaimName("scope");
        scopeConverter.setAuthorityPrefix("SCOPE_");

        JwtGrantedAuthoritiesConverter roleConverter =
                new JwtGrantedAuthoritiesConverter();

        roleConverter.setAuthoritiesClaimName("scope");
        roleConverter.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter converter =
                new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(jwt -> {

            var authorities = scopeConverter.convert(jwt);

            if (authorities == null) {
                authorities = new java.util.ArrayList<>();
            }

            var roleAuthorities = roleConverter.convert(jwt);

            if (roleAuthorities != null) {
                authorities.addAll(roleAuthorities);
            }

            return authorities;
        });

        return converter;
    }

    /**
     * Spring Security Filter Chain của Gateway.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            CookieOrHeaderBearerTokenResolver bearerTokenResolver,
            JwtAuthenticationConverter jwtAuthenticationConverter
    ) throws Exception {

        http

            // =====================================================
            // CORS
            // =====================================================
            // CORS được cấu hình trong application.yml
            // của Gateway.
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            // =====================================================
            // CSRF
            // =====================================================
            // API Stateless + JWT.
            .csrf(csrf -> csrf.disable())

            // =====================================================
            // SESSION
            // =====================================================
            .sessionManagement(session ->
                    session.sessionCreationPolicy(
                            SessionCreationPolicy.STATELESS
                    )
            )

            // =====================================================
            // AUTHORIZATION
            // =====================================================
            .authorizeHttpRequests(auth -> auth

                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                    // -------------------------------------------------
                    // Public Auth APIs
                    // -------------------------------------------------
                    .requestMatchers(
                            "/api/auth/register",
                            "/api/auth/login",
                            "/api/auth/refresh",
                            "/api/auth/verify-email"
                    ).permitAll()

                    // -------------------------------------------------
                    // Tất cả API còn lại yêu cầu JWT
                    // -------------------------------------------------
                    .anyRequest().authenticated()
            )

            // =====================================================
            // OAUTH2 RESOURCE SERVER
            // =====================================================
            .oauth2ResourceServer(oauth2 -> oauth2

                    // Web:
                    //     Cookie accessToken
                    //
                    // Android:
                    //     Authorization: Bearer <token>
                    .bearerTokenResolver(bearerTokenResolver)

                    // JWT verification
                    .jwt(jwt ->
                            jwt.jwtAuthenticationConverter(
                                    jwtAuthenticationConverter
                            )
                    )
            );

        return http.build();
    }
}
