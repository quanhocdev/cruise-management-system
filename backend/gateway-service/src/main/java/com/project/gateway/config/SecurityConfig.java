
package com.project.gateway.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.server.resource.web.server.authentication.ServerBearerTokenAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.security.web.server.util.matcher.PathPatternParserServerWebExchangeMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import com.project.gateway.security.CookieOrHeaderBearerTokenConverter;
import com.project.gateway.security.JsonAccessDeniedHandler;
import com.project.gateway.security.JsonAuthenticationEntryPoint;

@Configuration
public class SecurityConfig {

        private static final String[] PUBLIC_ENDPOINTS = {
                        "/api/auth/register",
                        "/api/auth/login",
                        "/api/auth/refresh",
                        "/api/auth/verify-email",
                        "/api/auth/logout",

                        "/api/webhook/**",
                        "/api/chat/**",

                        "/api/auth/google/login",
                        "/api/auth/google/verify-otp",

                        "/api/v1/payments/vnpay/return",
                        "/api/v1/payments/vnpay/ipn",

                        "/api/v1/pos/transactions/sync",
                        "/api/v1/pos/identify",

                        "/api/auth/activate/verify",
                        "/api/auth/activate/set-password",

                        "/ws-booking/**",
                        "/api/public/**",

                        "/actuator/health",
                        "/actuator/info"
        };

        @Bean
        public CorsConfigurationSource corsConfigurationSource(
                        @Value("${cors.allowed-origins:http://localhost:5173}") String allowedOrigins) {

                CorsConfiguration configuration = new CorsConfiguration();

                configuration.setAllowedOrigins(
                                List.of(allowedOrigins.split(",")));

                configuration.setAllowedMethods(
                                List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));

                configuration.setAllowedHeaders(List.of("*"));
                configuration.setAllowCredentials(true);
                configuration.setExposedHeaders(
                                List.of("Authorization", "Set-Cookie"));

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

                source.registerCorsConfiguration("/api/**", configuration);

                return source;
        }

        /**
         * Public Dialogflow webhook.
         * This chain does not enable OAuth2 Resource Server/JWT authentication.
         */
        @Bean
        @Order(0)
        public SecurityWebFilterChain webhookSecurityWebFilterChain(
                        ServerHttpSecurity http) {

                return http
                                .securityMatcher(
                                                new PathPatternParserServerWebExchangeMatcher(
                                                                "/api/webhook/**"))
                                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                                .authorizeExchange(exchange -> exchange
                                                .anyExchange().permitAll())
                                .build();
        }

        /**
         * Security for all other Gateway endpoints.
         */
        @Bean
        @Order(1)
        public SecurityWebFilterChain securityWebFilterChain(
                        ServerHttpSecurity http,
                        CookieOrHeaderBearerTokenConverter bearerTokenConverter,
                        ServerAuthenticationEntryPoint authenticationEntryPoint,
                        ServerAccessDeniedHandler accessDeniedHandler) {

                return http
                                .cors(Customizer.withDefaults())
                                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                                .authorizeExchange(exchange -> exchange
                                                .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                                                .pathMatchers("/ws-booking/**").permitAll()
                                                .pathMatchers(PUBLIC_ENDPOINTS).permitAll()
                                                .anyExchange().authenticated())
                                .oauth2ResourceServer(resourceServer -> resourceServer
                                                .bearerTokenConverter(bearerTokenConverter)
                                                .authenticationEntryPoint(authenticationEntryPoint)
                                                .accessDeniedHandler(accessDeniedHandler)
                                                .jwt(Customizer.withDefaults()))
                                .exceptionHandling(exception -> exception
                                                .authenticationEntryPoint(authenticationEntryPoint)
                                                .accessDeniedHandler(accessDeniedHandler))
                                .build();
        }

        @Bean
        public CookieOrHeaderBearerTokenConverter bearerTokenConverter() {
                return new CookieOrHeaderBearerTokenConverter(
                                new ServerBearerTokenAuthenticationConverter());
        }

        @Bean
        public ServerAuthenticationEntryPoint authenticationEntryPoint() {
                return new JsonAuthenticationEntryPoint();
        }

        @Bean
        public ServerAccessDeniedHandler accessDeniedHandler() {
                return new JsonAccessDeniedHandler();
        }
}
