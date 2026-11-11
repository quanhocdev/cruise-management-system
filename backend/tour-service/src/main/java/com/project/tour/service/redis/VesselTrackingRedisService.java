package com.project.tour.service.redis;

import com.project.tour.dto.VesselPositionDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class VesselTrackingRedisService {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private static final String KEY_PREFIX = "vessel:location:";

    public VesselTrackingRedisService(StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    // 1. Cập nhật vị trí từ Android (Ghi đè liên tục, KHÔNG set TTL)
    public void saveVesselPosition(Long tourId, double latitude, double longitude) {
        String key = KEY_PREFIX + tourId;
        try {
            var position = new VesselPositionDto(latitude, longitude, LocalDateTime.now());
            String jsonValue = objectMapper.writeValueAsString(position);

            // Lệnh SET thuần túy không timeout -> Tồn tại mãi trên Redis cho tới khi bị ghi
            // đè
            redisTemplate.opsForValue().set(key, jsonValue);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize vessel position to Redis", e);
        }
    }

    // 2. Lấy vị trí mới nhất phục vụ Web Dashboard của Scheduler
    public VesselPositionDto getVesselPosition(Long tourId) {
        String key = KEY_PREFIX + tourId;
        String jsonValue = redisTemplate.opsForValue().get(key);

        if (jsonValue == null) {
            return null; // Chưa có tín hiệu GPS
        }

        try {
            return objectMapper.readValue(jsonValue, VesselPositionDto.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize vessel position from Redis", e);
        }
    }
}