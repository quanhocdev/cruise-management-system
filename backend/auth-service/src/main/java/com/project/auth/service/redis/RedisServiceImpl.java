
package com.project.auth.service.redis;

import java.time.Duration;
import java.util.UUID;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisServiceImpl implements RedisService {

    private final StringRedisTemplate redisTemplate;

    public RedisServiceImpl(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // =========================
    // LOCAL REGISTER OTP
    // =========================

    @Override
    public void saveOtp(UUID userId, String otp) {
        String key = "otp:register:" + userId;

        redisTemplate.opsForValue()
                .set(key, otp, Duration.ofMinutes(5));
    }

    @Override
    public String getOtp(UUID userId) {
        return redisTemplate.opsForValue()
                .get("otp:register:" + userId);
    }

    @Override
    public void deleteOtp(UUID userId) {
        redisTemplate.delete("otp:register:" + userId);
    }

    // =========================
    // GOOGLE LOGIN OTP
    // =========================

    @Override
    public void saveGoogleLoginOtp(UUID userId, String otp) {
        String key = "otp:google-login:" + userId;

        redisTemplate.opsForValue()
                .set(key, otp, Duration.ofMinutes(5));
    }

    @Override
    public String getGoogleLoginOtp(UUID userId) {
        return redisTemplate.opsForValue()
                .get("otp:google-login:" + userId);
    }

    @Override
    public void deleteGoogleLoginOtp(UUID userId) {
        redisTemplate.delete("otp:google-login:" + userId);
    }
}
