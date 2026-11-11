package com.project.tour.controller.helper;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import com.project.tour.dto.VesselPositionDto;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/vessel-tracking")
public class VesselTrackingController {

    private final VesselTrackingRedisService trackingService;

    public VesselTrackingController(VesselTrackingRedisService trackingService) {
        this.trackingService = trackingService;
    }

    // 1. Android gọi định kỳ 30s/lần để đẩy tọa độ lên
    @PostMapping("/tours/{tourId}/location")
    public ResponseEntity<Void> updateLocation(
            @PathVariable Long tourId,
            @RequestParam double latitude,
            @RequestParam double longitude) {

        trackingService.saveVesselPosition(tourId, latitude, longitude);
        return ResponseEntity.ok().build();
    }

    // 2. Web Dashboard của Scheduler gọi để lấy tọa độ mới nhất vẽ icon thuyền
    @GetMapping("/tours/{tourId}/location")
    public ResponseEntity<VesselPositionDto> getCurrentLocation(@PathVariable Long tourId) {
        VesselPositionDto position = trackingService.getVesselPosition(tourId);
        if (position == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(position);
    }

    // --- Service tích hợp trực tiếp không qua Impl rườm rà ---
    @Service
    public static class VesselTrackingRedisService {

        private final StringRedisTemplate redisTemplate;
        private final ObjectMapper objectMapper;
        private static final String KEY_PREFIX = "vessel:location:";

        public VesselTrackingRedisService(StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
            this.redisTemplate = redisTemplate;
            this.objectMapper = objectMapper;
        }

        public void saveVesselPosition(Long tourId, double latitude, double longitude) {
            String key = KEY_PREFIX + tourId;
            try {
                var position = new VesselPositionDto(latitude, longitude, LocalDateTime.now());
                String jsonValue = objectMapper.writeValueAsString(position);

                // Lệnh SET thuần túy không TTL -> Tồn tại mãi trên Redis cho tới khi bị ghi đè
                redisTemplate.opsForValue().set(key, jsonValue);
            } catch (Exception e) {
                throw new RuntimeException("Failed to serialize vessel position to Redis", e);
            }
        }

        public VesselPositionDto getVesselPosition(Long tourId) {
            String key = KEY_PREFIX + tourId;
            String jsonValue = redisTemplate.opsForValue().get(key);

            if (jsonValue == null) {
                return null;
            }

            try {
                return objectMapper.readValue(jsonValue, VesselPositionDto.class);
            } catch (Exception e) {
                throw new RuntimeException("Failed to deserialize vessel position from Redis", e);
            }
        }
    }
}