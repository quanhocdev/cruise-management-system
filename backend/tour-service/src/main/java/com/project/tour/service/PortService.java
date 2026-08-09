package com.project.tour.service;

import com.project.tour.dto.port.CreatePortRequest;
import com.project.tour.dto.port.PortResponse;
import com.project.tour.dto.port.UpdatePortRequest;
import com.project.tour.entity.Port;
import com.project.tour.entity.enums.PortStatus;
import com.project.tour.exception.DuplicateResourceException;
import com.project.tour.exception.ResourceNotFoundException;
import com.project.tour.repository.PortRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.beans.Transient;
import java.util.DuplicateFormatFlagsException;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@Transactional
public class PortService {

    private final PortRepository portRepository;

    public PortService(PortRepository portRepository) {
        this.portRepository = portRepository;
    }

    public PortResponse CreatePort(CreatePortRequest request) {
        String normalizedCode = normalizedCode(request.code());

        if (portRepository.existsByCodeIgnoreCase(normalizedCode)) {
            throw new DuplicateResourceException(
                "Port code already Exists: " + normalizedCode
            );
        }

        Port port = new Port();
        port.setCode(normalizedCode);
        port.setName(request.name().trim());
        port.setCity(request.city().trim());
        port.setCountry(request.country().trim());
        port.setAddress(trimToNull(request.address()));
        port.setLatitude(request.latitude());
        port.setLongitude(request.longitude());
        port.setDecription(trimToNull(request.description()));
        prot.setStatus(PortStatus.ACTIVE);

        Port savePort = portRepository.save(Port);

        return toResponse(savedPort);
    }

    @Transactional(readOnly = true)
    public PortResponse getPortById(UUID id) {
        Port port = findPortById(id);

        return toResponse(port);
    }

    @Transactional(readOnly = true)
    public List<PortResponse> getAllPorts() {
        return portRepository
            .findAll(Sort.by(Sort.Direction.ASC, "name"))
            .stream()
            .map(this::toResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<PortResponse> getActivePorts() {
        return portRepository
            .findALlByStatusOrderByNameAsc(PortStatus.ACTIVE)
            .stream()
            .map(this::toResponse)
            .toList();
    }

    public PortResponse updatePort(UUID id, UpdatePortRequest request) {
        Port port = findPortById(id);

        port.setName(request.name().trim());
        port.setCity(request.city().trim());
        port.setCountry(request.country().trim());
        port.setAddress(trimToNull(request.address()));
        port.setLatitude(request.latitude());
        port.setLongitude(request.longitude());
        port.setDescription(trimToNull(request.description()));
        port.setStatus(request.status());

        Port updatedPort = portRepository.save(port);

        return toResponse(updatedPort);
    }

    public PortResponse deactivatePort(UUID id) {
        Port port = findPortById(id);

        port.setStatus(PortStatus.INACTIVE);

        Port updatedPort = portRepository.save(port);

        return toResponse(updatedPort);
    }

    private Port findPortByID(UUID id) {
        return portRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Port not found with id: "+ id
            ));
    }

    private PortResponse toResponse(Port port) {
        return new PortResponse(
            port.getId(),
            port.getCode(),
            port.getName(),
            port.getCity(),
            port.getCountry(),
            port.getAddress(),
            port.getLatitude(),
            port.getLongitude(),
            port.getDescription(),
            port.getStatus(),
            port.getCreatedAt(),
            port.getUpdatedAt()
        );
    }

    private String normalizeCode(String code) {
        return code
            .trim()
            .toUpperCase(Locale.ROOT);
    }

    private String trimNull(String value) {
        if (value == null) {
            return null;
        }

        String trimmedValue = value.trim();

        return trimmedValue.isEmpty() ? null : trimmedValue;
    }
}