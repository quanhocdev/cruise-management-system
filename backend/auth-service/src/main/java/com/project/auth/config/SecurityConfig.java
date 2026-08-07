package com.project.auth.config;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
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
        List.of(
            "GET",
            "POST",
            "PUT",
            "DELETE",
            "OPTIONS"
        )
    );

    configuration.setAllowedHeaders(
        List.of("*")
    );

    configuration.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source =
            new UrlBasedCorsConfigurationSource();

    source.registerCorsConfiguration(
        "/**",
        configuration
    );

    return source;
}

    @Bean
public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
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
                "/favicon.ico"
        );
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())

            // 1. STATELESS HOÀN TOÀN (TẮT SESSION PHÍA SERVER)
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // 2. PHÂN QUYỀN TRUY CẬP THEO USERROLE
            .authorizeHttpRequests(auth -> auth
                // Public APIs & Static Page Routes
                .requestMatchers(
    "/api/auth/register",
    "/api/auth/login",
    "/api/auth/verify-email",
    "/api/auth/refresh",
    "/api/auth/me"
).permitAll()

.requestMatchers(
    "/api/auth/logout"
)
.authenticated()

                // Web Pages phân quyền theo Role
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/shore/**").hasRole("SHORE")
                .requestMatchers("/onboard/**").hasRole("ONBOARD")
                .requestMatchers("/operation/**").hasRole("OPERATION")
                .requestMatchers("/finance/**").hasRole("FINANCE")
                .requestMatchers("/passenger/**").hasRole("PASSENGER")

                // REST APIs phân quyền theo SCOPE (phục vụ Android / Fetch API)
                .requestMatchers("/api/admin/**").hasAuthority("SCOPE_ADMIN")
                .requestMatchers("/api/shore/**").hasAuthority("SCOPE_SHORE")
                .requestMatchers("/api/onboard/**").hasAuthority("SCOPE_ONBOARD")
                .requestMatchers("/api/operation/**").hasAuthority("SCOPE_OPERATION")
                .requestMatchers("/api/finance/**").hasAuthority("SCOPE_FINANCE")
                .requestMatchers("/api/passenger/**").hasAuthority("SCOPE_PASSENGER")

                .anyRequest().authenticated()
            )

            // 3. OAUTH2 RESOURCE SERVER (TỰ ĐỘNG ĐỌC TOKEN TỪ COOKIE BÀO HOẶC HEADER)
            .oauth2ResourceServer(oauth2 -> oauth2
                .bearerTokenResolver(request -> {
                    // Ưu tiên 1: Đọc từ Cookie "accessToken" (phục vụ Web Browser)
                    if (request.getCookies() != null) {
                        for (Cookie cookie : request.getCookies()) {
                            if ("accessToken".equals(cookie.getName())) {
                                return cookie.getValue();
                            }
                        }
                    }
                    // Ưu tiên 2: Đọc từ Header "Authorization: Bearer <token>" (phục vụ App Android)
                    return new DefaultBearerTokenResolver().resolve(request);
                })
                
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))

                // Xử lý khi chưa đăng nhập
                .authenticationEntryPoint(
                    new BearerTokenAuthenticationEntryPoint()
                )
            )

            .exceptionHandling(ex -> ex.accessDeniedHandler(new BearerTokenAccessDeniedHandler()))

            // 4. LOGOUT (XÓA COOKIES & TRẢ VỀ JSON 200 OK)
            .logout(logout -> logout
                .logoutUrl("/api/auth/logout")
                .deleteCookies("accessToken", "refreshToken")
                .clearAuthentication(true)
                .logoutSuccessHandler((request, response, authentication) -> {
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"message\":\"Logout successful\"}");
                })
            );

        return http.build();
    }

    // Convert claim 'scope' trong JWT thành cả ROLE_ và SCOPE_
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
            authorities.addAll(roleConverter.convert(jwt));
            return authorities;
        });

        return converter;
    }
}