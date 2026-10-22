
package com.project.auth.service.redis;

import java.time.Duration;
import java.util.UUID;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class TokenRedisServiceImpl implements TokenRedisService {

    private final StringRedisTemplate redisTemplate;

    public TokenRedisServiceImpl(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void saveAccessToken(String jti, UUID userId, Duration ttl) {
        String key = "auth:access:" + jti;

        redisTemplate.opsForValue().set(
                key,
                userId.toString(),
                ttl);
    }

    @Override
    public void saveRefreshToken(String jti, UUID userId, Duration ttl) {
        String key = "auth:refresh:" + jti;

        redisTemplate.opsForValue().set(
                key,
                userId.toString(),
                ttl);
    }

    @Override
    public boolean existsAccessToken(String jti) {
        return Boolean.TRUE.equals(
                redisTemplate.hasKey("auth:access:" + jti));
    }

    @Override
    public boolean existsRefreshToken(String jti) {
        return Boolean.TRUE.equals(
                redisTemplate.hasKey("auth:refresh:" + jti));
    }

    @Override
    public void deleteAccessToken(String jti) {
        redisTemplate.delete("auth:access:" + jti);
    }

    @Override
    public void deleteRefreshToken(String jti) {
        redisTemplate.delete("auth:refresh:" + jti);
    }

    @Override
    public void saveActivationToken(
            String token,
            UUID userId,
            Duration ttl) {
        String key = "auth:activation:" + token;

        redisTemplate.opsForValue().set(
                key,
                userId.toString(),
                ttl);
    }

    @Override
    public UUID getActivationUserId(String token) {
        String key = "auth:activation:" + token;

        String value = redisTemplate.opsForValue().get(key);

        if (value == null) {
            return null;
        }

        return UUID.fromString(value);
    }

    @Override
    public void deleteActivationToken(String token) {
        redisTemplate.delete("auth:activation:" + token);
    }
}
