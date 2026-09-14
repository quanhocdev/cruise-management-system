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

        // Lua script an toàn luồng để trừ số lượng phòng đồng thời
        this.reserveScript = new DefaultRedisScript<>();
        this.reserveScript.setScriptText(
                "local remaining = tonumber(redis.call('get', KEYS[1])); " +
                        "if remaining == nil then " +
                        "   return -1; " +
                        "end; " +
                        "local requested = tonumber(ARGV[1]); " +
                        "if remaining >= requested then " +
                        "   redis.call('set', KEYS[1], remaining - requested); " +
                        "   return remaining - requested; " +
                        "else " +
                        "   return -2; " + // Không đủ phòng trống
                        "end;");
        this.reserveScript.setResultType(Long.class);
    }

    @Override
    public void savePackageAvailableRooms(UUID tourPackageId, Integer totalRooms) {
        String key = "tour:package:" + tourPackageId + ":available_rooms";
        redisTemplate.opsForValue().set(key, String.valueOf(totalRooms != null ? totalRooms : 0));
    }

    @Override
    public Long getPackageAvailableRooms(UUID tourPackageId) {
        String key = "tour:package:" + tourPackageId + ":available_rooms";
        String value = redisTemplate.opsForValue().get(key);
        return value != null ? Long.valueOf(value) : null;
    }

    @Override
    public void deletePackageAvailableRooms(UUID tourPackageId) {
        String key = "tour:package:" + tourPackageId + ":available_rooms";
        redisTemplate.delete(key);
    }

    @Override
    public Long reservePackageRooms(UUID tourPackageId, int requestedRooms) {
        String key = "tour:package:" + tourPackageId + ":available_rooms";
        return redisTemplate.execute(
                reserveScript,
                Collections.singletonList(key),
                String.valueOf(requestedRooms));
    }
}