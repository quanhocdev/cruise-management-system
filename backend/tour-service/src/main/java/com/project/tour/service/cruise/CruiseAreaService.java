package com.project.tour.service.cruise;

import com.project.common.dto.UploadResult;
import com.project.common.service.file.FileStorageService;
import com.project.tour.dto.cruise.area.CreateCruiseAreaRequest;
import com.project.tour.dto.cruise.area.CruiseAreaResponse;
import com.project.tour.dto.cruise.area.UpdateCruiseAreaRequest;
import com.project.tour.exception.AppException;
import com.project.tour.mapper.cruise.CruiseAreaMapper;
import com.project.tour.model.CruiseArea;
import com.project.tour.model.CruiseDeck;
import com.project.tour.model.enums.cruise.CruiseAreaStatus;
import com.project.tour.repository.cruise.CruiseAreaRepository;
import com.project.tour.repository.cruise.CruiseDeckRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class CruiseAreaService {

        private final CruiseAreaRepository cruiseAreaRepository;
        private final CruiseDeckRepository cruiseDeckRepository;
        private final FileStorageService fileStorageService;

        public CruiseAreaService(
                        CruiseAreaRepository cruiseAreaRepository,
                        CruiseDeckRepository cruiseDeckRepository,
                        FileStorageService fileStorageService) {

                this.cruiseAreaRepository = cruiseAreaRepository;
                this.cruiseDeckRepository = cruiseDeckRepository;
                this.fileStorageService = fileStorageService;
        }

        public CruiseAreaResponse create(
                        UUID deckId,
                        CreateCruiseAreaRequest request) {

                CruiseDeck deck = findDeck(deckId);

                if (cruiseAreaRepository.existsByCruiseDeck_IdAndNameIgnoreCase(
                                deckId,
                                request.getName())) {

                        throw new AppException(
                                        "Area name already exists in this deck",
                                        HttpStatus.CONFLICT);
                }

                CruiseArea area = CruiseAreaMapper.toEntity(request);
                area.setCruiseDeck(deck);

                // Xử lý upload ảnh khu vực nếu có gửi file
                if (request.getImage() != null
                                && !request.getImage().isEmpty()) {

                        UploadResult uploadResult = fileStorageService.saveMultipart(
                                        request.getImage(),
                                        "cruise-areas"); // Folder lưu ảnh trên Cloudinary

                        area.setImageUrl(uploadResult.getUrl());
                        area.setImagePublicId(uploadResult.getPublicId());
                }

                CruiseArea saved = cruiseAreaRepository.save(area);

                return CruiseAreaMapper.toResponse(saved);
        }

        @Transactional(readOnly = true)
        public CruiseAreaResponse getById(
                        UUID deckId,
                        UUID areaId) {

                CruiseArea area = findById(deckId, areaId);

                return CruiseAreaMapper.toResponse(area);
        }

        @Transactional(readOnly = true)
        public List<CruiseAreaResponse> getAll(
                        UUID deckId) {

                findDeck(deckId);

                return cruiseAreaRepository
                                .findAllByCruiseDeck_IdOrderByNameAsc(deckId)
                                .stream()
                                .map(CruiseAreaMapper::toResponse)
                                .toList();
        }

        @Transactional(readOnly = true)
        public List<CruiseAreaResponse> getActive(
                        UUID deckId) {

                findDeck(deckId);

                return cruiseAreaRepository
                                .findAllByCruiseDeck_IdAndStatusOrderByNameAsc(
                                                deckId,
                                                CruiseAreaStatus.ACTIVE)
                                .stream()
                                .map(CruiseAreaMapper::toResponse)
                                .toList();
        }

        public CruiseAreaResponse update(
                        UUID deckId,
                        UUID areaId,
                        UpdateCruiseAreaRequest request) {

                CruiseArea area = findById(deckId, areaId);

                if (cruiseAreaRepository
                                .existsByCruiseDeck_IdAndNameIgnoreCaseAndIdNot(
                                                deckId,
                                                request.getName(),
                                                areaId)) {

                        throw new AppException(
                                        "Area name already exists in this deck",
                                        HttpStatus.CONFLICT);
                }

                String oldPublicId = area.getImagePublicId();

                CruiseAreaMapper.updateEntity(area, request);

                // Xử lý cập nhật ảnh mới nếu có
                if (request.getImage() != null
                                && !request.getImage().isEmpty()) {

                        UploadResult uploadResult = fileStorageService.saveMultipart(
                                        request.getImage(),
                                        "cruise-areas");

                        area.setImageUrl(uploadResult.getUrl());
                        area.setImagePublicId(uploadResult.getPublicId());

                        // Xóa ảnh cũ trên Cloudinary nếu có
                        if (oldPublicId != null && !oldPublicId.isBlank()) {
                                fileStorageService.delete(oldPublicId);
                        }
                }

                CruiseArea updated = cruiseAreaRepository.save(area);

                return CruiseAreaMapper.toResponse(updated);
        }

        public CruiseAreaResponse deactivate(
                        UUID deckId,
                        UUID areaId) {

                CruiseArea area = findById(deckId, areaId);

                area.setStatus(CruiseAreaStatus.INACTIVE);

                CruiseArea updated = cruiseAreaRepository.save(area);

                return CruiseAreaMapper.toResponse(updated);
        }

        public void delete(
                        UUID deckId,
                        UUID areaId) {

                CruiseArea area = findById(deckId, areaId);

                // Xóa ảnh trên Cloudinary trước khi xóa bản ghi dưới DB
                if (area.getImagePublicId() != null
                                && !area.getImagePublicId().isBlank()) {

                        fileStorageService.delete(area.getImagePublicId());
                }

                cruiseAreaRepository.delete(area);
        }

        private CruiseDeck findDeck(UUID deckId) {

                return cruiseDeckRepository.findById(deckId)
                                .orElseThrow(() -> new AppException(
                                                "Cruise deck not found",
                                                HttpStatus.NOT_FOUND));
        }

        private CruiseArea findById(
                        UUID deckId,
                        UUID areaId) {

                return cruiseAreaRepository
                                .findByIdAndCruiseDeck_Id(areaId, deckId)
                                .orElseThrow(() -> new AppException(
                                                "Cruise area not found",
                                                HttpStatus.NOT_FOUND));
        }
}