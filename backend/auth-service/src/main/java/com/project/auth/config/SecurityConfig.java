package com.project.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

        @Bean
        public AuthenticationManager authenticationManager(
                        AuthenticationConfiguration authConfig) throws Exception {
                return authConfig.getAuthenticationManager();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public WebSecurityCustomizer webSecurityCustomizer() {
                return (web) -> web.ignoring().requestMatchers(
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/favicon.ico");
        }

        @Bean
        public SecurityFilterChain securityFilterChain(
                        HttpSecurity http) throws Exception {

                http
                                // =====================================================
                                // CORS
                                // =====================================================
                                // Không xử lý CORS tại Auth Service.
                                // CORS được Gateway xử lý.
                                .csrf(csrf -> csrf.disable())

                                // =====================================================
                                // STATELESS
                                // =====================================================
                                .sessionManagement(session -> session.sessionCreationPolicy(
                                                SessionCreationPolicy.STATELESS))

                                // =====================================================
                                // AUTHORIZATION
                                // =====================================================
                                .authorizeHttpRequests(auth -> auth

                                                // -------------------------------------------------
                                                // Public Auth APIs
                                                // -------------------------------------------------
                                                .requestMatchers(
                                                                "/api/auth/register",
                                                                "/api/auth/login",
                                                                "/api/auth/verify-email",
                                                                "/api/auth/refresh",

                                                                // Staff activation
                                                                "/api/auth/activate/verify",
                                                                "/api/auth/activate/set-password")
                                                .permitAll()

                                                // -------------------------------------------------
                                                // Authenticated APIs
                                                // -------------------------------------------------
                                                .requestMatchers(
                                                                "/api/auth/logout",
                                                                "/api/auth/me")
                                                .authenticated()

                                                // -------------------------------------------------
                                                // Web Pages
                                                // -------------------------------------------------
                                                .requestMatchers("/admin/**")
                                                .hasRole("ADMIN")

                                                .requestMatchers("/shore/**")
                                                .hasRole("SHORE")

                                                .requestMatchers("/onboard/**")
                                                .hasRole("ONBOARD")

                                                .requestMatchers("/operation/**")
                                                .hasRole("OPERATION")

                                                .requestMatchers("/finance/**")
                                                .hasRole("FINANCE")

                                                .requestMatchers("/passenger/**")
                                                .hasRole("PASSENGER")

                                                // -------------------------------------------------
                                                // REST APIs
                                                // -------------------------------------------------
                                                .requestMatchers("/admin/**")
                                                .hasAuthority("SCOPE_ADMIN")

                                                .requestMatchers("/shore/**")
                                                .hasAuthority("SCOPE_SHORE")

                                                .requestMatchers("/onboard/**")
                                                .hasAuthority("SCOPE_ONBOARD")

                                                .requestMatchers("/operation/**")
                                                .hasAuthority("SCOPE_OPERATION")

                                                .requestMatchers("/finance/**")
                                                .hasAuthority("SCOPE_FINANCE")

                                                .requestMatchers("/api/passenger/**")
                                                .hasAuthority("SCOPE_PASSENGER")

                                                .anyRequest().authenticated())

                                // =====================================================
                                // OAUTH2 RESOURCE SERVER
                                // =====================================================
                                .oauth2ResourceServer(oauth2 -> oauth2

                                                /*
                                                 * Auth Service vẫn có thể nhận:
                                                 *
                                                 * Web:
                                                 * Cookie accessToken
                                                 *
                                                 * Android:
                                                 * Authorization: Bearer <token>
                                                 */
                                                .bearerTokenResolver(request -> {

                                                        // Web - Cookie
                                                        if (request.getCookies() != null) {

                                                                for (var cookie : request.getCookies()) {

                                                                        if ("accessToken".equals(cookie.getName())) {
                                                                                return cookie.getValue();
                                                                        }
                                                                }
                                                        }

                                                        // Android - Authorization Header
                                                        return new DefaultBearerTokenResolver()
                                                                        .resolve(request);
                                                })

                                                .jwt(jwt -> jwt.jwtAuthenticationConverter(
                                                                jwtAuthenticationConverter()))

                                                .authenticationEntryPoint(
                                                                new BearerTokenAuthenticationEntryPoint()))

                                .exceptionHandling(ex -> ex.accessDeniedHandler(
                                                new BearerTokenAccessDeniedHandler()));

                return http.build();
        }

        @Bean
        public JwtAuthenticationConverter jwtAuthenticationConverter() {

                JwtGrantedAuthoritiesConverter scopeConverter = new JwtGrantedAuthoritiesConverter();

                scopeConverter.setAuthoritiesClaimName("scope");
                scopeConverter.setAuthorityPrefix("SCOPE_");

                JwtGrantedAuthoritiesConverter roleConverter = new JwtGrantedAuthoritiesConverter();

                roleConverter.setAuthoritiesClaimName("scope");
                roleConverter.setAuthorityPrefix("ROLE_");

                JwtAuthenticationConverter converter = new JwtAuthenticationConverter();

                converter.setJwtGrantedAuthoritiesConverter(jwt -> {

                        var authorities = scopeConverter.convert(jwt);

                        if (authorities != null) {

                                var roleAuthorities = roleConverter.convert(jwt);

                                if (roleAuthorities != null) {
                                        authorities.addAll(roleAuthorities);
                                }

                        }

                        return authorities;
                });

                return converter;
        }
}
