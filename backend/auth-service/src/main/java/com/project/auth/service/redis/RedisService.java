
package com.project.auth.service.redis;

public interface RedisService {

    // OTP đăng ký LOCAL
    void saveOtp(Long userId, String otp);

    String getOtp(Long userId);

    void deleteOtp(Long userId);

    // OTP dành riêng cho Google Login lần đầu
    void saveGoogleLoginOtp(Long userId, String otp);

    String getGoogleLoginOtp(Long userId);

    void deleteGoogleLoginOtp(Long userId);
}
