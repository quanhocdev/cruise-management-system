package com.project.tour.service;

import com.project.common.dto.location.AddressResponse;
import com.project.common.service.location.GeocodingService;
import com.project.tour.dto.port.CreatePortRequest;
import com.project.tour.dto.port.PortResponse;
import com.project.tour.dto.port.UpdatePortRequest;
import com.project.tour.exception.ResourceNotFoundException;
import com.project.tour.mapper.port.PortMapper;
import com.project.tour.model.Port;
import com.project.tour.model.enums.PortStatus;
import com.project.tour.repository.PortRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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

    /**
     * CREATE PORT
     *
     * Flow:
     * CreateRequest
     * -> Mapper
     * -> Nominatim
     * -> Port
     * -> Repository
     */
    public PortResponse createPort(CreatePortRequest request) {

        Port port = portMapper.toEntity(request);

        fillAddressFromCoordinates(
                port,
                request.getLatitude(),
                request.getLongitude());

        port.setStatus(PortStatus.ACTIVE);

        Port savedPort = portRepository.save(port);

        return portMapper.toResponse(savedPort);
    }

    /**
     * GET PORT BY ID
     */
    @Transactional(readOnly = true)
    public PortResponse getPortById(UUID id) {

        Port port = findPortById(id);

        return portMapper.toResponse(port);
    }

    /**
     * GET ALL PORTS
     */
    @Transactional(readOnly = true)
    public List<PortResponse> getAllPorts() {

        return portRepository
                .findAll(
                        Sort.by(
                                Sort.Direction.ASC,
                                "name"))
                .stream()
                .map(portMapper::toResponse)
                .toList();
    }

    /**
     * GET ACTIVE PORTS
     */
    @Transactional(readOnly = true)
    public List<PortResponse> getActivePorts() {

        return portRepository
                .findAllByStatusOrderByNameAsc(
                        PortStatus.ACTIVE)
                .stream()
                .map(portMapper::toResponse)
                .toList();
    }

    /**
     * UPDATE PORT
     *
     * Nếu latitude hoặc longitude thay đổi
     * thì gọi lại Nominatim để cập nhật:
     *
     * address
     * city
     * country
     */
    public PortResponse updatePort(
            UUID id,
            UpdatePortRequest request) {

        Port port = findPortById(id);

        boolean coordinatesChanged = isCoordinateChanged(
                port.getLatitude(),
                request.getLatitude())
                ||
                isCoordinateChanged(
                        port.getLongitude(),
                        request.getLongitude());

        portMapper.updateEntity(
                port,
                request);

        if (coordinatesChanged) {

            fillAddressFromCoordinates(
                    port,
                    request.getLatitude(),
                    request.getLongitude());
        }

        Port updatedPort = portRepository.save(port);

        return portMapper.toResponse(updatedPort);
    }

    /**
     * DEACTIVATE PORT
     *
     * Không xóa vật lý.
     * Chuyển trạng thái thành INACTIVE.
     */
    public PortResponse deactivatePort(UUID id) {

        Port port = findPortById(id);

        port.setStatus(PortStatus.INACTIVE);

        Port updatedPort = portRepository.save(port);

        return portMapper.toResponse(updatedPort);
    }

    /**
     * FIND PORT
     */
    private Port findPortById(UUID id) {

        return portRepository
                .findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Port not found with id: " + id));
    }

    /**
     * CALL NOMINATIM
     *
     * latitude + longitude
     * -> address
     * -> city
     * -> country
     */
    private void fillAddressFromCoordinates(
            Port port,
            BigDecimal latitude,
            BigDecimal longitude) {

        AddressResponse addressResponse = geocodingService.getAddress(
                latitude,
                longitude);

        if (addressResponse == null) {
            return;
        }

        port.setAddress(
                trimToNull(
                        addressResponse.getFullAddress()));

        port.setCity(
                trimToNull(
                        addressResponse.getCity()));

        port.setCountry(
                trimToNull(
                        addressResponse.getCountry()));
    }

    /**
     * CHECK COORDINATE CHANGED
     */
    private boolean isCoordinateChanged(
            BigDecimal oldValue,
            BigDecimal newValue) {

        if (oldValue == null && newValue == null) {
            return false;
        }

        if (oldValue == null || newValue == null) {
            return true;
        }

        return oldValue.compareTo(newValue) != 0;
    }

    /**
     * TRIM STRING
     */
    private String trimToNull(String value) {

        if (value == null) {
            return null;
        }

        String trimmedValue = value.trim();

        return trimmedValue.isEmpty()
                ? null
                : trimmedValue;
    }
}