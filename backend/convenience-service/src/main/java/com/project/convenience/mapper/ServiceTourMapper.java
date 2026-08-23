package com.project.convenience.mapper;

import com.project.convenience.dto.service.convenience.ServiceTourResponse;
import com.project.convenience.model.Service;
import com.project.convenience.model.ServiceTour;
import org.springframework.stereotype.Component;

@Component
public class ServiceTourMapper {

        public ServiceTourResponse toResponse(ServiceTour entity) {
                if (entity == null) {
                        return null;
                }

                Service service = entity.getService();

                return new ServiceTourResponse(
                                entity.getId(),

                                // =====================================================
                                // TOUR INFO (Lưu UUID, thông tin text sẽ populate sau via Feign)
                                // =====================================================
                                entity.getTourId(),
                                null, // tourCode
                                null, // tourName

                                // =====================================================
                                // SERVICE INFO (Nội bộ convenience-service)
                                // =====================================================
                                service != null ? service.getId() : null,
                                service != null ? service.getName() : null,
                                service != null ? service.getDescription() : null,
                                service != null ? service.getPrice() : null,
                                service != null ? service.getImageUrl() : null,

                                // =====================================================
                                // CRUISE AREA INFO (Lưu UUID, thông tin text sẽ populate sau via Feign)
                                // =====================================================
                                entity.getCruiseAreaId(),
                                null, // cruiseAreaName

                                // =====================================================
                                // CRUISE DECK INFO (Populate sau via Feign)
                                // =====================================================
                                null, // cruiseDeckId
                                null, // deckNumber

                                // =====================================================
                                // CONFIGURATION
                                // =====================================================
                                entity.getMaxPassengers(),
                                entity.getDurationMinutes(),
                                entity.getStatus(),
                                entity.getCreatedAt(),
                                entity.getUpdatedAt());
        }
}