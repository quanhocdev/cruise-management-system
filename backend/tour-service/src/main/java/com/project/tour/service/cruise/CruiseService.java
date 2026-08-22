package com.project.tour.service.cruise;

import com.project.common.dto.UploadResult;
import com.project.common.service.file.FileStorageService;
import com.project.tour.dto.cruise.CreateCruiseRequest;
import com.project.tour.dto.cruise.CruiseResponse;
import com.project.tour.dto.cruise.UpdateCruiseRequest;
import com.project.tour.exception.AppException;
import com.project.tour.mapper.cruise.CruiseMapper;
import com.project.tour.model.Cruise;
import com.project.tour.model.enums.cruise.CruiseStatus;
import com.project.tour.repository.cruise.CruiseRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class CruiseService {

        private final CruiseRepository cruiseRepository;
        private final FileStorageService fileStorageService;

        public CruiseService(
                        CruiseRepository cruiseRepository,
                        FileStorageService fileStorageService) {

                this.cruiseRepository = cruiseRepository;
                this.fileStorageService = fileStorageService;
        }

        public CruiseResponse createCruise(
                        CreateCruiseRequest request) {

                if (cruiseRepository.existsByCodeIgnoreCase(
                                request.getCode())) {

                        throw new AppException(
                                        "Cruise code already exists",
                                        HttpStatus.CONFLICT);
                }

                Cruise cruise = CruiseMapper.toEntity(request);

                if (request.getImage() != null
                                && !request.getImage().isEmpty()) {

                        UploadResult uploadResult = fileStorageService.saveMultipart(
                                        request.getImage(),
                                        "cruises");

                        cruise.setImageUrl(uploadResult.getUrl());
                        cruise.setImagePublicId(uploadResult.getPublicId());
                }

                Cruise savedCruise = cruiseRepository.save(cruise);

                return CruiseMapper.toResponse(savedCruise);
        }

        @Transactional(readOnly = true)
        public CruiseResponse getCruiseById(UUID id) {

                Cruise cruise = findById(id);

                return CruiseMapper.toResponse(cruise);
        }

        @Transactional(readOnly = true)
        public CruiseResponse getCruiseByCode(String code) {

                Cruise cruise = cruiseRepository
                                .findByCodeIgnoreCase(code)
                                .orElseThrow(() -> new AppException(
                                                "Cruise not found",
                                                HttpStatus.NOT_FOUND));

                return CruiseMapper.toResponse(cruise);
        }

        @Transactional(readOnly = true)
        public List<CruiseResponse> getAllCruises() {

                return cruiseRepository.findAll()
                                .stream()
                                .map(CruiseMapper::toResponse)
                                .toList();
        }

        @Transactional(readOnly = true)
        public List<CruiseResponse> getActiveCruises() {

                return cruiseRepository
                                .findAllByStatusOrderByNameAsc(
                                                CruiseStatus.ACTIVE)
                                .stream()
                                .map(CruiseMapper::toResponse)
                                .toList();
        }

        public CruiseResponse updateCruise(
                        UUID id,
                        UpdateCruiseRequest request) {

                Cruise cruise = findById(id);

                if (cruiseRepository
                                .existsByCodeIgnoreCaseAndIdNot(
                                                request.getCode(),
                                                id)) {

                        throw new AppException(
                                        "Cruise code already exists",
                                        HttpStatus.CONFLICT);
                }

                String oldPublicId = cruise.getImagePublicId();

                CruiseMapper.updateEntity(
                                cruise,
                                request);

                if (request.getImage() != null
                                && !request.getImage().isEmpty()) {

                        UploadResult uploadResult = fileStorageService.saveMultipart(
                                        request.getImage(),
                                        "cruises");

                        cruise.setImageUrl(
                                        uploadResult.getUrl());

                        cruise.setImagePublicId(
                                        uploadResult.getPublicId());

                        if (oldPublicId != null
                                        && !oldPublicId.isBlank()) {

                                fileStorageService.delete(
                                                oldPublicId);
                        }
                }

                Cruise updatedCruise = cruiseRepository.save(cruise);

                return CruiseMapper.toResponse(
                                updatedCruise);
        }

        public void deleteCruise(UUID id) {

                Cruise cruise = findById(id);

                if (cruise.getImagePublicId() != null
                                && !cruise.getImagePublicId().isBlank()) {

                        fileStorageService.delete(
                                        cruise.getImagePublicId());
                }

                cruiseRepository.delete(cruise);
        }

        private Cruise findById(UUID id) {

                return cruiseRepository
                                .findById(id)
                                .orElseThrow(() -> new AppException(
                                                "Cruise not found",
                                                HttpStatus.NOT_FOUND));
        }
}