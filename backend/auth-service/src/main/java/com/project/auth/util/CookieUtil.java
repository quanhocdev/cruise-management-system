package com.project.auth.util;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CookieUtil {

    public static final String ACCESS_TOKEN_COOKIE_NAME = "accessToken";
    public static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";

    /**
     * Tạo và đính kèm Cookie vào Response
     */
    public static void addCookie(HttpServletResponse response, String name, String value, long maxAgeInSeconds) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(false) // Đổi thành true khi deploy HTTPS
                .path("/")
                .maxAge(maxAgeInSeconds)
                .sameSite("Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    /**
     * Xóa Cookie bằng cách set Max-Age = 0
     */
    public static void clearCookie(HttpServletResponse response, String name) {
        ResponseCookie cookie = ResponseCookie.from(name, "")
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    /**
     * Xóa sạch cả Access Token và Refresh Token Cookie
     */
    public static void clearAuthCookies(HttpServletResponse response) {
        clearCookie(response, ACCESS_TOKEN_COOKIE_NAME);
        clearCookie(response, REFRESH_TOKEN_COOKIE_NAME);
    }

    /**
     * Lấy giá trị của một Cookie từ Request
     */
    public static String getCookieValue(HttpServletRequest request, String name) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (name.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}