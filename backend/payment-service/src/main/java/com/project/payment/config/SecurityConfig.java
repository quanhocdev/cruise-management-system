package com.project.payment.config;

import jakarta.servlet.http.*;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.*;
import org.springframework.security.oauth2.server.resource.web.*;
import org.springframework.security.web.SecurityFilterChain;
import java.util.Arrays;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(
        org.springframework.security.config.annotation.web.builders.HttpSecurity http,
        BearerTokenResolver tokenResolver,
        JwtAuthenticationConverter authenticationConverter
    ) throws Exception {
        return http.csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                .requestMatchers(HttpMethod.GET,
                    "/api/v1/payments/vnpay/return",
                    "/api/v1/payments/vnpay/ipn").permitAll()
                .anyRequest().authenticated())
            .oauth2ResourceServer(resource -> resource
                .bearerTokenResolver(tokenResolver)
                .jwt(jwt -> jwt.jwtAuthenticationConverter(authenticationConverter)))
            .build();
    }

    @Bean
    public BearerTokenResolver bearerTokenResolver() {
        DefaultBearerTokenResolver header = new DefaultBearerTokenResolver();
        return request -> {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                String token = Arrays.stream(cookies).filter(c -> "accessToken".equals(c.getName()))
                    .map(Cookie::getValue).filter(v -> !v.isBlank()).findFirst().orElse(null);
                if (token != null) return token;
            }
            return header.resolve(request);
        };
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter roles = new JwtGrantedAuthoritiesConverter();
        roles.setAuthoritiesClaimName("scope"); roles.setAuthorityPrefix("ROLE_");
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(roles); return converter;
    }
}
