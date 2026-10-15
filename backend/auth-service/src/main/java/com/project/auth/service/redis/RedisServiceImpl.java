
package com.project.auth.service.redis;

import java.time.Duration;

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
    public void saveOtp(Long userId, String otp) {
        String key = "otp:register:" + userId;

        redisTemplate.opsForValue()
                .set(key, otp, Duration.ofMinutes(5));
    }

    @Override
    public String getOtp(Long userId) {
        return redisTemplate.opsForValue()
                .get("otp:register:" + userId);
    }

    @Override
    public void deleteOtp(Long userId) {
        redisTemplate.delete("otp:register:" + userId);
    }

    // =========================
    // GOOGLE LOGIN OTP
    // =========================

    @Override
    public void saveGoogleLoginOtp(Long userId, String otp) {
        String key = "otp:google-login:" + userId;

        redisTemplate.opsForValue()
                .set(key, otp, Duration.ofMinutes(5));
    }

    @Override
    public String getGoogleLoginOtp(Long userId) {
        return redisTemplate.opsForValue()
                .get("otp:google-login:" + userId);
    }

    @Override
    public void deleteGoogleLoginOtp(Long userId) {
        redisTemplate.delete("otp:google-login:" + userId);
    }
}
