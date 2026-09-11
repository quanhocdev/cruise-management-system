package com.project.tour.service.redis;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.UUID;

@Service
public class TourRedisServiceImpl implements TourRedisService {

    private final StringRedisTemplate redisTemplate;
    private final DefaultRedisScript<Long> reserveScript;

    public TourRedisServiceImpl(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;

        // Định nghĩa Lua script chuẩn cú pháp Redis
        this.reserveScript = new DefaultRedisScript<>();
        this.reserveScript.setScriptText(
                "local remaining = tonumber(redis.call('get', KEYS[1])); " +
                        "if remaining == nil then " +
                        "    return -1; " +
                        "end; " +
                        "local requested = tonumber(ARGV[1]); " +
                        "if remaining >= requested then " +
                        "    redis.call('set', KEYS[1], remaining - requested); " + // Sửa redis.set thành
                                                                                    // redis.call('set', ...)
                        "    return remaining - requested; " + // Trả về số ghế còn lại sau khi trừ
                        "else " +
                        "    return -2; " + // Không đủ ghế
                        "end;");
        this.reserveScript.setResultType(Long.class);
    }

    @Override
    public void saveRemainingSeats(UUID tourId, Integer maxPassengers) {
        String key = "tour:" + tourId + ":remaining";
        redisTemplate.opsForValue().set(key, String.valueOf(maxPassengers));
    }

    @Override
    public Long getRemainingSeats(UUID tourId) {
        String key = "tour:" + tourId + ":remaining";
        String value = redisTemplate.opsForValue().get(key);
        return value != null ? Long.valueOf(value) : null;
    }

    @Override
    public void deleteRemainingSeats(UUID tourId) {
        String key = "tour:" + tourId + ":remaining";
        redisTemplate.delete(key);
    }

}