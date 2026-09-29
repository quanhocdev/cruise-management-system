package com.project.tour.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class InternalApiKeyFilter extends OncePerRequestFilter {

    private static final String INTERNAL_API_KEY_HEADER = "X-Internal-API-Key";

    private final String internalApiKey;

    public InternalApiKeyFilter(
            @Value("${internal.api-key}") String internalApiKey) {

        this.internalApiKey = internalApiKey;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String requestUri = request.getRequestURI();

        // Chỉ kiểm tra API key cho endpoint nội bộ
        if (!requestUri.startsWith("/internal/")) {
            filterChain.doFilter(request, response);
            return;
        }

        String requestApiKey = request.getHeader(INTERNAL_API_KEY_HEADER);

        if (!StringUtils.hasText(requestApiKey)
                || !requestApiKey.equals(internalApiKey)) {

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write(
                    "{\"message\":\"Invalid or missing internal API key\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }
}