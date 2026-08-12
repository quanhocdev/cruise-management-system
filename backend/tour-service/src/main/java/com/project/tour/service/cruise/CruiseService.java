package com.project.tour.service.cruise;

import com.project.common.dto.UploadResult;
import com.project.common.service.file.FileStorageService;
import com.project.tour.dto.cruise.CreateCruiseRequest;
import com.project.tour.dto.cruise.CruiseResponse;
import com.project.tour.dto.cruise.UpdateCruiseRequest;
import com.project.tour.exception.AppException;
import com.project.tour.mapper.cruise.CruiseMapper;
import com.project.tour.model.Cruise;
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

    /*
     * =====================================================
     * CREATE
     * =====================================================
     */
    public CruiseResponse create(CreateCruiseRequest request) {

        if (cruiseRepository.existsByCodeIgnoreCase(request.getCode())) {
            throw new AppException(
                    "Cruise code already exists",
                    HttpStatus.CONFLICT);
        }

        Cruise cruise = CruiseMapper.toEntity(request);

        /*
         * Upload image
         */
        if (request.getImage() != null && !request.getImage().isEmpty()) {

            UploadResult uploadResult = fileStorageService.saveMultipart(
                    request.getImage(),
                    "cruises");

            cruise.setImageUrl(uploadResult.getUrl());
            cruise.setImagePublicId(uploadResult.getPublicId());
        }

        Cruise savedCruise = cruiseRepository.save(cruise);

        return CruiseMapper.toResponse(savedCruise);
    }

    /*
     * =====================================================
     * GET BY ID
     * =====================================================
     */
    @Transactional(readOnly = true)
    public CruiseResponse getById(UUID id) {

        Cruise cruise = findById(id);

        return CruiseMapper.toResponse(cruise);
    }

    /*
     * =====================================================
     * GET ALL
     * =====================================================
     */
    @Transactional(readOnly = true)
    public List<CruiseResponse> getAll() {

        return cruiseRepository.findAll()
                .stream()
                .map(CruiseMapper::toResponse)
                .toList();
    }

    /*
     * =====================================================
     * UPDATE
     * =====================================================
     */
    public CruiseResponse update(
            UUID id,
            UpdateCruiseRequest request) {

        Cruise cruise = findById(id);

        if (cruiseRepository.existsByCodeIgnoreCaseAndIdNot(
                request.getCode(),
                id)) {

            throw new AppException(
                    "Cruise code already exists",
                    HttpStatus.CONFLICT);
        }

        /*
         * Update thông tin cơ bản
         */
        CruiseMapper.updateEntity(cruise, request);

        /*
         * Nếu có ảnh mới:
         *
         * 1. Xóa ảnh cũ
         * 2. Upload ảnh mới
         * 3. Lưu URL + publicId mới
         */
        if (request.getImage() != null
                && !request.getImage().isEmpty()) {

            String oldPublicId = cruise.getImagePublicId();

            UploadResult uploadResult = fileStorageService.saveMultipart(
                    request.getImage(),
                    "cruises");

            cruise.setImageUrl(uploadResult.getUrl());
            cruise.setImagePublicId(uploadResult.getPublicId());

            /*
             * Xóa ảnh cũ sau khi upload ảnh mới thành công.
             */
            if (oldPublicId != null
                    && !oldPublicId.isBlank()) {

                fileStorageService.delete(oldPublicId);
            }
        }

        Cruise updatedCruise = cruiseRepository.save(cruise);

        return CruiseMapper.toResponse(updatedCruise);
    }

    /*
     * =====================================================
     * DELETE
     * =====================================================
     */
    public void delete(UUID id) {

        Cruise cruise = findById(id);

        /*
         * Xóa ảnh trên Cloudinary
         */
        if (cruise.getImagePublicId() != null
                && !cruise.getImagePublicId().isBlank()) {

            fileStorageService.delete(
                    cruise.getImagePublicId());
        }

        cruiseRepository.delete(cruise);
    }

    /*
     * =====================================================
     * FIND ENTITY
     * =====================================================
     */
    private Cruise findById(UUID id) {

        return cruiseRepository.findById(id)
                .orElseThrow(() -> new AppException(
                        "Cruise not found",
                        HttpStatus.NOT_FOUND));
    }
}