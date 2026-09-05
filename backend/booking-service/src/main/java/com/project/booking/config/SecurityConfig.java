package com.project.booking.config;

import jakarta.servlet.http.Cookie;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.*;
import org.springframework.security.oauth2.server.resource.web.*;
import org.springframework.security.web.SecurityFilterChain;
import java.util.Arrays;

@Configuration
public class SecurityConfig {
    @Bean SecurityFilterChain securityFilterChain(org.springframework.security.config.annotation.web.builders.HttpSecurity http,
        BearerTokenResolver resolver, JwtAuthenticationConverter converter) throws Exception {
        return http.csrf(csrf -> csrf.disable())
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(a -> a
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/actuator/health", "/actuator/info", "/internal/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v1/pos/transactions/sync", "/api/v1/pos/identify", "/api/v1/pos/check-in").permitAll()
                .requestMatchers("/api/admin/pos-terminals/**").hasRole("ADMIN")
                .anyRequest().authenticated())
            .oauth2ResourceServer(o -> o.bearerTokenResolver(resolver).jwt(j -> j.jwtAuthenticationConverter(converter)))
            .build();
    }
    @Bean org.springframework.security.crypto.password.PasswordEncoder passwordEncoder() {
        return new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
    }
    @Bean BearerTokenResolver bearerTokenResolver() {
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
    @Bean JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter roles = new JwtGrantedAuthoritiesConverter();
        roles.setAuthoritiesClaimName("scope"); roles.setAuthorityPrefix("ROLE_");
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(roles); return converter;
    }
}
