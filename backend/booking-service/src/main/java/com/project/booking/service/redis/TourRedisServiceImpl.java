package com.project.booking.service.redis;

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
    public Long reservePackageRooms(UUID tourPackageId, int requestedRooms) {
        String key = "tour:package:" + tourPackageId + ":available_rooms";
        System.out.println(">>> [REDIS] Đang check Key phòng: " + key + " với số phòng yêu cầu: " + requestedRooms);

        return redisTemplate.execute(
                reserveScript,
                Collections.singletonList(key),
                String.valueOf(requestedRooms));
    }
}