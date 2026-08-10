package com.project.auth.service.redis;

import java.time.Duration;

public interface TokenRedisService {

    void saveAccessToken(String jti, Long userId, Duration ttl);

    void saveRefreshToken(String jti, Long userId, Duration ttl);

    boolean existsAccessToken(String jti);

    boolean existsRefreshToken(String jti);

    void deleteAccessToken(String jti);

    void deleteRefreshToken(String jti);

    void saveActivationToken(String token, Long userId, Duration ttl);

    Long getActivationUserId(String token);

    void deleteActivationToken(String token);

}