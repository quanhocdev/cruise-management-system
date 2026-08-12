package com.project.tour.mapper.port;

import com.project.tour.dto.port.CreatePortRequest;
import com.project.tour.dto.port.PortResponse;
import com.project.tour.dto.port.UpdatePortRequest;
import com.project.tour.model.Port;
import org.springframework.stereotype.Component;

@Component
public class PortMapper {

    public Port toEntity(CreatePortRequest request) {

        if (request == null) {
            return null;
        }

        Port port = new Port();

        port.setName(request.getName());
        port.setLatitude(request.getLatitude());
        port.setLongitude(request.getLongitude());
        port.setDescription(request.getDescription());

        return port;
    }

    public void updateEntity(
            Port port,
            UpdatePortRequest request) {

        if (port == null || request == null) {
            return;
        }

        port.setName(request.getName());
        port.setLatitude(request.getLatitude());
        port.setLongitude(request.getLongitude());
        port.setDescription(request.getDescription());
        port.setStatus(request.getStatus());
    }

    public PortResponse toResponse(Port port) {

        if (port == null) {
            return null;
        }

        return new PortResponse(
                port.getId(),
                port.getName(),
                port.getCity(),
                port.getCountry(),
                port.getAddress(),
                port.getLatitude(),
                port.getLongitude(),
                port.getDescription(),
                port.getStatus(),
                port.getCreatedAt(),
                port.getUpdatedAt());
    }
}