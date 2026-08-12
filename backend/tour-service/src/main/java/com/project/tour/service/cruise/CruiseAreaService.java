package com.project.tour.service.cruise;

import com.project.tour.dto.cruise.area.CreateCruiseAreaRequest;
import com.project.tour.dto.cruise.area.CruiseAreaResponse;
import com.project.tour.dto.cruise.area.UpdateCruiseAreaRequest;

import com.project.tour.model.CruiseArea;
import com.project.tour.model.CruiseDeck;
import com.project.tour.model.enums.CruiseAreaStatus;
import com.project.tour.repository.cruise.CruiseAreaRepository;
import com.project.tour.repository.cruise.CruiseDeckRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class CruiseAreaService {
}
