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

    @Override
    public void saveOtp(Long userId, String otp) {
        String key = "otp:register:" + userId;

        redisTemplate.opsForValue()
                .set(key, otp, Duration.ofMinutes(5));
    }

    @Override
    public String getOtp(Long userId) {
        String key = "otp:register:" + userId;

        return redisTemplate.opsForValue()
                .get(key);
    }

    @Override
    public void deleteOtp(Long userId) {
        String key = "otp:register:" + userId;

        redisTemplate.delete(key);
    }
}