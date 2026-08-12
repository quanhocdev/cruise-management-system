package com.project.tour.service.port;

import com.project.common.dto.location.AddressResponse;
import com.project.common.service.location.GeocodingService;
import com.project.tour.dto.port.CreatePortRequest;
import com.project.tour.dto.port.PortResponse;
import com.project.tour.dto.port.UpdatePortRequest;
import com.project.tour.exception.AppException;
import com.project.tour.mapper.port.PortMapper;
import com.project.tour.model.Port;
import com.project.tour.model.enums.PortStatus;
import com.project.tour.repository.PortRepository;

import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class PortService {

    private final PortRepository portRepository;
    private final PortMapper portMapper;
    private final GeocodingService geocodingService;

    public PortService(
            PortRepository portRepository,
            PortMapper portMapper,
            GeocodingService geocodingService) {

        this.portRepository = portRepository;
        this.portMapper = portMapper;
        this.geocodingService = geocodingService;
    }

    // CREATE
    public PortResponse createPort(CreatePortRequest request) {

        Port port = portMapper.toEntity(request);

        AddressResponse address = geocodingService.getAddress(
                request.getLatitude(),
                request.getLongitude());

        if (address != null) {
            port.setAddress(address.getFullAddress());
            port.setCity(address.getCity());
            port.setCountry(address.getCountry());
        }

        Port savedPort = portRepository.save(port);

        return portMapper.toResponse(savedPort);
    }

    // GET ALL
    @Transactional(readOnly = true)
    public List<PortResponse> getAllPorts() {

        return portRepository
                .findAll(Sort.by(Sort.Direction.ASC, "name"))
                .stream()
                .map(portMapper::toResponse)
                .toList();
    }

    // GET ACTIVE
    @Transactional(readOnly = true)
    public List<PortResponse> getActivePorts() {

        return portRepository
                .findAllByStatusOrderByNameAsc(PortStatus.ACTIVE)
                .stream()
                .map(portMapper::toResponse)
                .toList();
    }

    // GET BY ID
    @Transactional(readOnly = true)
    public PortResponse getPortById(UUID id) {

        Port port = findPortById(id);

        return portMapper.toResponse(port);
    }

    // UPDATE
    public PortResponse updatePort(
            UUID id,
            UpdatePortRequest request) {

        Port port = findPortById(id);

        portMapper.updateEntity(port, request);

        AddressResponse address = geocodingService.getAddress(
                request.getLatitude(),
                request.getLongitude());

        if (address != null) {
            port.setAddress(address.getFullAddress());
            port.setCity(address.getCity());
            port.setCountry(address.getCountry());
        }

        Port updatedPort = portRepository.save(port);

        return portMapper.toResponse(updatedPort);
    }

    // SOFT DELETE
    public void deactivatePort(UUID id) {

        Port port = findPortById(id);

        port.setStatus(PortStatus.INACTIVE);

        portRepository.save(port);
    }

    // FIND ENTITY
    private Port findPortById(UUID id) {

        return portRepository.findById(id)
                .orElseThrow(() -> new AppException(
                        "Port not found",
                        HttpStatus.NOT_FOUND));
    }
}