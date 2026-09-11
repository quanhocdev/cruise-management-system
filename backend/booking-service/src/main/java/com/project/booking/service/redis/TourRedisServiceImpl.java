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
    public boolean tryReserveSeats(UUID tourId, int requestedSeats) {
        String key = "tour:" + tourId + ":remaining";
        System.out.println(">>> [REDIS] Đang check Key: " + key + " với số ghế yêu cầu: " + requestedSeats);

        Long result = redisTemplate.execute(
                reserveScript,
                Collections.singletonList(key),
                String.valueOf(requestedSeats));

        System.out.println(">>> [REDIS] Kết quả từ Lua Script trả về: " + result);
        // Ý nghĩa các mã: >= 0 (Thành công, còn dư từng này ghế), -1 (Key không tồn
        // tại/chưa set), -2 (Không đủ ghế)

        if (result != null && result >= 0) {
            return true;
        }

        return false;
    }
}