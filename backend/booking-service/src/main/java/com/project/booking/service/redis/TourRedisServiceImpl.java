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
    private final DefaultRedisScript<Long> releaseScript;

    public TourRedisServiceImpl(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;

        this.reserveScript = new DefaultRedisScript<>();
        this.reserveScript.setScriptText(
                "local remaining = tonumber(redis.call('get', KEYS[1])); " +
                        "if remaining == nil then return -1; end; " +
                        "local requested = tonumber(ARGV[1]); " +
                        "if remaining >= requested then " +
                        "   redis.call('set', KEYS[1], remaining - requested); " +
                        "   return remaining - requested; " +
                        "else return -2; end;");
        this.reserveScript.setResultType(Long.class);

        // Chỉ cộng phòng nếu refund này chưa từng được xử lý.
        this.releaseScript = new DefaultRedisScript<>();
        this.releaseScript.setScriptText(
                "local alreadyReleased = redis.call('exists', KEYS[2]); " +
                        "if alreadyReleased == 1 then return 0; end; " +
                        "redis.call('incrby', KEYS[1], ARGV[1]); " +
                        "redis.call('set', KEYS[2], '1'); " +
                        "return 1;");
        this.releaseScript.setResultType(Long.class);
    }

    @Override
    public Long reservePackageRooms(UUID tourPackageId, int requestedRooms) {
        String key = "tour:package:" + tourPackageId + ":available_rooms";

        return redisTemplate.execute(
                reserveScript,
                Collections.singletonList(key),
                String.valueOf(requestedRooms));
    }

    @Override
    public Long releasePackageRooms(
            UUID tourPackageId,
            int releasedRooms,
            UUID refundRequestId) {

        String inventoryKey = "tour:package:" + tourPackageId + ":available_rooms";

        // Mỗi refund chỉ được hoàn lại số phòng đúng một lần.
        String idempotencyKey = "tour:refund:" + refundRequestId + ":inventory_released";

        return redisTemplate.execute(
                releaseScript,
                java.util.Arrays.asList(inventoryKey, idempotencyKey),
                String.valueOf(releasedRooms));
    }
}