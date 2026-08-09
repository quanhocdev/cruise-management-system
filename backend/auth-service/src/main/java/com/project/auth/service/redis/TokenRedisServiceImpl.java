package com.project.auth.service.redis;

import java.time.Duration;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class TokenRedisServiceImpl implements TokenRedisService {

    private final StringRedisTemplate redisTemplate;

    public TokenRedisServiceImpl(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void saveAccessToken(
            String jti,
            Long userId,
            Duration ttl
    ) {
        String key = "auth:access:" + jti;

        redisTemplate.opsForValue().set(
                key,
                String.valueOf(userId),
                ttl
        );
    }

    @Override
    public void saveRefreshToken(
            String jti,
            Long userId,
            Duration ttl
    ) {
        String key = "auth:refresh:" + jti;

        redisTemplate.opsForValue().set(
                key,
                String.valueOf(userId),
                ttl
        );
    }

    @Override
    public boolean existsAccessToken(String jti) {
        String key = "auth:access:" + jti;

        return Boolean.TRUE.equals(
                redisTemplate.hasKey(key)
        );
    }

    @Override
    public boolean existsRefreshToken(String jti) {
        String key = "auth:refresh:" + jti;

        return Boolean.TRUE.equals(
                redisTemplate.hasKey(key)
        );
    }

    @Override
    public void deleteAccessToken(String jti) {
        String key = "auth:access:" + jti;

        redisTemplate.delete(key);
    }

    @Override
    public void deleteRefreshToken(String jti) {
        String key = "auth:refresh:" + jti;

        redisTemplate.delete(key);
    }
}