package com.project.tour.service.cruise;

import com.project.common.dto.UploadResult;
import com.project.common.service.file.FileStorageService;
import com.project.tour.dto.cruise.CreateCruiseRequest;
import com.project.tour.dto.cruise.CruiseResponse;
import com.project.tour.dto.cruise.UpdateCruiseRequest;
import com.project.tour.exception.AppException;
import com.project.tour.mapper.cruise.CruiseMapper;
import com.project.tour.model.Cruise;
import com.project.tour.model.enums.CruiseStatus;
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
    public CruiseResponse createCruise(
            CreateCruiseRequest request) {

        /*
         * Kiểm tra code đã tồn tại
         */
        if (cruiseRepository.existsByCodeIgnoreCase(
                request.getCode())) {

            throw new AppException(
                    "Cruise code already exists",
                    HttpStatus.CONFLICT);
        }

        /*
         * Request -> Entity
         */
        Cruise cruise = CruiseMapper.toEntity(request);

        /*
         * Upload image nếu có
         */
        if (request.getImage() != null
                && !request.getImage().isEmpty()) {

            UploadResult uploadResult = fileStorageService.saveMultipart(
                    request.getImage(),
                    "cruises");

            cruise.setImageUrl(
                    uploadResult.getUrl());

            cruise.setImagePublicId(
                    uploadResult.getPublicId());
        }

        /*
         * Save database
         */
        Cruise savedCruise = cruiseRepository.save(cruise);

        /*
         * Entity -> Response
         */
        return CruiseMapper.toResponse(savedCruise);
    }

    /*
     * =====================================================
     * GET BY ID
     * =====================================================
     */
    @Transactional(readOnly = true)
    public CruiseResponse getCruiseById(
            UUID id) {

        Cruise cruise = findById(id);

        return CruiseMapper.toResponse(cruise);
    }

    /*
     * =====================================================
     * GET BY CODE
     * =====================================================
     */
    @Transactional(readOnly = true)
    public CruiseResponse getCruiseByCode(
            String code) {

        Cruise cruise = cruiseRepository
                .findByCodeIgnoreCase(code)
                .orElseThrow(() -> new AppException(
                        "Cruise not found",
                        HttpStatus.NOT_FOUND));

        return CruiseMapper.toResponse(cruise);
    }

    /*
     * =====================================================
     * GET ALL
     * =====================================================
     */
    @Transactional(readOnly = true)
    public List<CruiseResponse> getAllCruises() {

        return cruiseRepository.findAll()
                .stream()
                .map(CruiseMapper::toResponse)
                .toList();
    }

    /*
     * =====================================================
     * GET ACTIVE
     * =====================================================
     */
    @Transactional(readOnly = true)
    public List<CruiseResponse> getActiveCruises() {

        return cruiseRepository
                .findAllByStatusOrderByNameAsc(
                        CruiseStatus.ACTIVE)
                .stream()
                .map(CruiseMapper::toResponse)
                .toList();
    }

    /*
     * =====================================================
     * UPDATE
     * =====================================================
     */
    public CruiseResponse updateCruise(
            UUID id,
            UpdateCruiseRequest request) {

        Cruise cruise = findById(id);

        /*
         * Kiểm tra code trùng với Cruise khác
         */
        if (cruiseRepository
                .existsByCodeIgnoreCaseAndIdNot(
                        request.getCode(),
                        id)) {

            throw new AppException(
                    "Cruise code already exists",
                    HttpStatus.CONFLICT);
        }

        /*
         * Lưu publicId ảnh cũ
         */
        String oldPublicId = cruise.getImagePublicId();

        /*
         * Update thông tin cơ bản
         */
        CruiseMapper.updateEntity(
                cruise,
                request);

        /*
         * Nếu request có ảnh mới
         */
        if (request.getImage() != null
                && !request.getImage().isEmpty()) {

            /*
             * Upload ảnh mới trước
             */
            UploadResult uploadResult = fileStorageService.saveMultipart(
                    request.getImage(),
                    "cruises");

            cruise.setImageUrl(
                    uploadResult.getUrl());

            cruise.setImagePublicId(
                    uploadResult.getPublicId());

            /*
             * Sau khi upload thành công
             * mới xóa ảnh cũ
             */
            if (oldPublicId != null
                    && !oldPublicId.isBlank()) {

                fileStorageService.delete(
                        oldPublicId);
            }
        }

        /*
         * Save database
         */
        Cruise updatedCruise = cruiseRepository.save(cruise);

        return CruiseMapper.toResponse(
                updatedCruise);
    }

    /*
     * =====================================================
     * DEACTIVATE
     * =====================================================
     *
     * Soft delete:
     *
     * ACTIVE -> INACTIVE
     *
     * Không xóa record khỏi database.
     *
     * =====================================================
     */
    public CruiseResponse deactivateCruise(
            UUID id) {

        Cruise cruise = findById(id);

        cruise.setStatus(
                CruiseStatus.INACTIVE);

        Cruise updatedCruise = cruiseRepository.save(cruise);

        return CruiseMapper.toResponse(
                updatedCruise);
    }

    /*
     * =====================================================
     * FIND ENTITY BY ID
     * =====================================================
     */
    private Cruise findById(UUID id) {

        return cruiseRepository
                .findById(id)
                .orElseThrow(() -> new AppException(
                        "Cruise not found",
                        HttpStatus.NOT_FOUND));
    }
}