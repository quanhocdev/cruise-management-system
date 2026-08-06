// package com.project.cruise.controller.auth;

// import com.project.cruise.service.auth.RefreshTokenService;
// import jakarta.servlet.http.HttpServletResponse;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.http.HttpHeaders;
// import org.springframework.http.ResponseCookie;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// import java.io.IOException;
// import java.util.Map;

// @RestController
// @RequestMapping("/api/auth")
// public class RefreshTokenController {

//     private final RefreshTokenService refreshTokenService;

//     @Value("${jwt.access-expiration}")
//     private long accessExpirationMs;

//     public RefreshTokenController(RefreshTokenService refreshTokenService) {
//         this.refreshTokenService = refreshTokenService;
//     }

//     /**
//      * Dành cho Web Client: Trực tiếp set Cookie mới và Redirect người dùng lại trang cũ
//      */
//     @GetMapping("/refresh")
//     public void refreshPage(
//             @CookieValue(name = "refreshToken", required = false) String refreshToken,
//             @RequestParam(name = "redirect", defaultValue = "/") String redirect,
//             HttpServletResponse response
//     ) throws IOException {

//         if (refreshToken == null) {
//             response.sendRedirect("/login");
//             return;
//         }

//         try {
//             String newAccessToken = refreshTokenService.refreshAccessToken(refreshToken);

//             ResponseCookie accessCookie = ResponseCookie.from("accessToken", newAccessToken)
//                     .httpOnly(true)
//                     .secure(false) // Đổi thành true trên Production (HTTPS)
//                     .path("/")
//                     .sameSite("Lax")
//                     .maxAge(accessExpirationMs / 1000)
//                     .build();

//             response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
//             response.sendRedirect(redirect);

//         } catch (Exception e) {
//             response.sendRedirect("/login");
//         }
//     }

//     /**
//      * Dành cho API Client (Android App hoặc Fetch API)
//      */
//     @PostMapping("/refresh")
//     public ResponseEntity<?> refresh(
//             @CookieValue(name = "refreshToken", required = false) String refreshToken
//     ) {
//         if (refreshToken == null) {
//             return ResponseEntity.status(401).body(Map.of("message", "Thiếu refresh token"));
//         }

//         try {
//             String newAccessToken = refreshTokenService.refreshAccessToken(refreshToken);

//             ResponseCookie accessCookie = ResponseCookie.from("accessToken", newAccessToken)
//                     .httpOnly(true)
//                     .secure(false)
//                     .path("/")
//                     .sameSite("Lax")
//                     .maxAge(accessExpirationMs / 1000)
//                     .build();

//             return ResponseEntity.ok()
//                     .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
//                     .body(Map.of(
//                             "message", "Refresh token thành công",
//                             "accessToken", newAccessToken // Đồng thời trả về body cho Android
//                     ));

//         } catch (Exception e) {
//             return ResponseEntity.status(401).body(Map.of("message", e.getMessage()));
//         }
//     }
// }