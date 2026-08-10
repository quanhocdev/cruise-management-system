package com.project.gateway.config;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.server.resource.web.server.authentication.ServerBearerTokenAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import com.project.gateway.security.CookieOrHeaderBearerTokenConverter;
import com.project.gateway.security.JsonAccessDeniedHandler;
import com.project.gateway.security.JsonAuthenticationEntryPoint;

@Configuration
public class SecurityConfig {

        // =====================================================
        // PUBLIC ENDPOINTS
        // =====================================================

        private static final String[] PUBLIC_ENDPOINTS = {
                        "/api/auth/register",
                        "/api/auth/login",
                        "/api/auth/refresh",
                        "/api/auth/verify-email",
                        "/api/auth/logout",

                        // Staff activation
                        "/api/auth/activate/verify",
                        "/api/auth/activate/set-password",

                        "/actuator/health",
                        "/actuator/info"
        };

        // =====================================================
        // CORS
        // =====================================================

        @Bean
        public CorsConfigurationSource corsConfigurationSource(
                        @Value("${cors.allowed-origins:http://localhost:5173}") String allowedOrigins) {

                CorsConfiguration configuration = new CorsConfiguration();

                configuration.setAllowedOrigins(
                                List.of(allowedOrigins.split(",")));

                configuration.setAllowedMethods(
                                List.of(
                                                "GET",
                                                "POST",
                                                "PUT",
                                                "DELETE",
                                                "PATCH",
                                                "OPTIONS"));

                configuration.setAllowedHeaders(
                                List.of("*"));

                configuration.setAllowCredentials(true);

                configuration.setExposedHeaders(
                                List.of("Authorization", "Set-Cookie"));

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

                source.registerCorsConfiguration(
                                "/**",
                                configuration);

                return source;
        }

        // =====================================================
        // SECURITY
        // =====================================================

        @Bean
        public SecurityWebFilterChain securityWebFilterChain(
                        ServerHttpSecurity http,
                        CookieOrHeaderBearerTokenConverter bearerTokenConverter,
                        ServerAuthenticationEntryPoint authenticationEntryPoint,
                        ServerAccessDeniedHandler accessDeniedHandler) {

                return http

                                // =================================================
                                // CORS
                                // =================================================

                                .cors(Customizer.withDefaults())

                                // =================================================
                                // CSRF
                                // =================================================

                                .csrf(ServerHttpSecurity.CsrfSpec::disable)

                                // =================================================
                                // AUTHORIZATION
                                // =================================================

                                .authorizeExchange(exchange -> exchange

                                                // CORS preflight
                                                .pathMatchers(
                                                                HttpMethod.OPTIONS)
                                                .permitAll()

                                                // Public Auth APIs
                                                .pathMatchers(
                                                                PUBLIC_ENDPOINTS)
                                                .permitAll()

                                                // Các API còn lại yêu cầu JWT
                                                .anyExchange().authenticated())

                                // =================================================
                                // OAUTH2 RESOURCE SERVER
                                // =================================================

                                .oauth2ResourceServer(resourceServer -> resourceServer

                                                // Web:
                                                // Cookie: accessToken
                                                //
                                                // Android:
                                                // Authorization: Bearer <token>
                                                .bearerTokenConverter(
                                                                bearerTokenConverter)

                                                .authenticationEntryPoint(
                                                                authenticationEntryPoint)

                                                .accessDeniedHandler(
                                                                accessDeniedHandler)

                                                // JWT verification
                                                .jwt(Customizer.withDefaults()))

                                // =================================================
                                // EXCEPTION HANDLING
                                // =================================================

                                .exceptionHandling(exception -> exception

                                                .authenticationEntryPoint(
                                                                authenticationEntryPoint)

                                                .accessDeniedHandler(
                                                                accessDeniedHandler))

                                .build();
        }

        // =====================================================
        // BEARER TOKEN CONVERTER
        // =====================================================

        @Bean
        public CookieOrHeaderBearerTokenConverter bearerTokenConverter() {

                return new CookieOrHeaderBearerTokenConverter(
                                new ServerBearerTokenAuthenticationConverter());
        }

        // =====================================================
        // AUTHENTICATION ENTRY POINT
        // =====================================================

        @Bean
        public ServerAuthenticationEntryPoint authenticationEntryPoint() {

                return new JsonAuthenticationEntryPoint();
        }

        // =====================================================
        // ACCESS DENIED HANDLER
        // =====================================================

        @Bean
        public ServerAccessDeniedHandler accessDeniedHandler() {

                return new JsonAccessDeniedHandler();
        }
}