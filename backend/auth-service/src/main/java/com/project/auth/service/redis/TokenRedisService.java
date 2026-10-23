
package com.project.auth.service.redis;

import java.time.Duration;
import java.util.UUID;

public interface TokenRedisService {

    void saveAccessToken(String jti, UUID userId, Duration ttl);

    void saveRefreshToken(String jti, UUID userId, Duration ttl);

    boolean existsAccessToken(String jti);

    boolean existsRefreshToken(String jti);

    void deleteAccessToken(String jti);

    void deleteRefreshToken(String jti);

    void saveActivationToken(String token, UUID userId, Duration ttl);

    UUID getActivationUserId(String token);

    void deleteActivationToken(String token);
}
