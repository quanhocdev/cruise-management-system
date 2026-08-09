package com.project.auth.security;

import java.util.Set;

import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class CookieOrHeaderBearerTokenResolver implements BearerTokenResolver {

    private static final String ACCESS_TOKEN_COOKIE = "accessToken";
    private static final Set<String> TOKEN_FREE_ENDPOINTS = Set.of(
            "/api/auth/login",
            "/api/auth/register",
            "/api/auth/verify-email",
            "/api/auth/refresh",
            "/api/auth/logout"
    );

    private final DefaultBearerTokenResolver headerResolver = new DefaultBearerTokenResolver();

    @Override
    public String resolve(HttpServletRequest request) {
        // Public auth flows must still work when the browser sends a stale
        // accessToken cookie. /refresh authenticates its refreshToken itself.
        if (TOKEN_FREE_ENDPOINTS.contains(request.getRequestURI())) {
            return null;
        }

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (ACCESS_TOKEN_COOKIE.equals(cookie.getName())
                        && cookie.getValue() != null
                        && !cookie.getValue().isBlank()) {
                    return cookie.getValue();
                }
            }
        }

        return headerResolver.resolve(request);
    }
}
