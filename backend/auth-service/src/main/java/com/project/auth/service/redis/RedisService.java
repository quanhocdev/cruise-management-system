
package com.project.auth.service.redis;

import java.util.UUID;

public interface RedisService {

    // OTP đăng ký LOCAL
    void saveOtp(UUID userId, String otp);

    String getOtp(UUID userId);

    void deleteOtp(UUID userId);

    // OTP dành riêng cho Google Login lần đầu
    void saveGoogleLoginOtp(UUID userId, String otp);

    String getGoogleLoginOtp(UUID userId);

    void deleteGoogleLoginOtp(UUID userId);
}
