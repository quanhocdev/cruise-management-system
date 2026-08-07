package com.project.auth.service.redis;

public interface RedisService {

    void saveOtp(Long userId, String otp);

    String getOtp(Long userId);

    void deleteOtp(Long userId);

}