package com.project.gateway.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;

@Component
public class CookieOrHeaderBearerTokenResolver
        implements org.springframework.security.oauth2.server.resource.web.BearerTokenResolver {

    private static final String ACCESS_TOKEN_COOKIE = "accessToken";

    private final DefaultBearerTokenResolver defaultBearerTokenResolver =
            new DefaultBearerTokenResolver();

    @Override
    public String resolve(HttpServletRequest request) {

        // =====================================================
        // 1. Web - đọc accessToken từ Cookie
        // =====================================================
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {

                if (ACCESS_TOKEN_COOKIE.equals(cookie.getName())) {
                    String token = cookie.getValue();

                    if (token != null && !token.isBlank()) {
                        return token;
                    }
                }
            }
        }

        // =====================================================
        // 2. Android - đọc Authorization: Bearer <token>
        // =====================================================
        return defaultBearerTokenResolver.resolve(request);
    }
}
