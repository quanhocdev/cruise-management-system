package com.project.tour.service.port;

import com.project.common.dto.location.AddressResponse;
import com.project.common.service.location.GeocodingService;
import com.project.tour.dto.port.CreatePortRequest;
import com.project.tour.dto.port.PortResponse;
import com.project.tour.dto.port.UpdatePortRequest;
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

}