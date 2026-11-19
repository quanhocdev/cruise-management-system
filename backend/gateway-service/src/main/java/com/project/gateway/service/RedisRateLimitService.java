
package com.project.gateway.service;

import java.time.Duration;
import java.util.List;

import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

@Service
public class RedisRateLimitService {

    private static final String KEY_PREFIX = "gateway:rate-limit:";

    /**
     * Lua Script:
     * - Tăng counter nguyên tử.
     * - Chỉ thiết lập TTL khi key vừa được tạo.
     * - Trả về số request hiện tại.
     */
    private static final RedisScript<Long> INCREMENT_SCRIPT = RedisScript.of(
            """
                    local current = redis.call('INCR', KEYS[1])

                    if current == 1 then
                        redis.call('PEXPIRE', KEYS[1], ARGV[1])
                    end

                    return current
                    """,
            Long.class);

    private final ReactiveStringRedisTemplate redisTemplate;

    public RedisRateLimitService(
            ReactiveStringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * Kiểm tra giới hạn request theo một đối tượng.
     *
     * @param ruleId ID của rule (create-booking, cancel-booking)
     * @param type   user hoặc ip
     * @param value  userId hoặc clientIp
     * @param limit  số request tối đa
     * @param window cửa sổ thời gian
     *
     * @return true nếu còn quota, false nếu vượt quota
     */
    public Mono<Boolean> isAllowed(
            String ruleId,
            String type,
            String value,
            int limit,
            Duration window) {

        if (value == null || value.isBlank()) {
            return Mono.error(
                    new IllegalArgumentException(
                            "Rate limit identity must not be empty"));
        }

        String key = buildKey(ruleId, type, value);

        long windowMillis = window.toMillis();

        return redisTemplate.execute(
                INCREMENT_SCRIPT,
                List.of(key),
                String.valueOf(windowMillis))
                .next()
                .map(currentCount -> currentCount <= limit);
    }

    private String buildKey(
            String ruleId,
            String type,
            String value) {

        return KEY_PREFIX
                + ruleId + ":"
                + type + ":"
                + value;
    }
}
