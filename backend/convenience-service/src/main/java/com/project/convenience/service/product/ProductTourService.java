package com.project.convenience.service.product;

import com.project.convenience.dto.product.convenience.HistoryProductTourResponse;
import com.project.convenience.dto.product.convenience.ProductTourResponse;
import com.project.convenience.mapper.HistoryProductTourMapper;
import com.project.convenience.mapper.ProductTourMapper;
import com.project.convenience.model.ProductTour;
import com.project.convenience.model.enums.ProductTourStatus;
import com.project.convenience.repository.HistoryProductTourRepository;
import com.project.convenience.repository.ProductTourRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ProductTourService {

        private final ProductTourRepository productTourRepository;
        private final ProductTourMapper mapper;
        private final HistoryProductTourRepository historyProductTourRepository;

        public ProductTourService(
                        ProductTourRepository productTourRepository,
                        ProductTourMapper mapper,
                        HistoryProductTourRepository historyProductTourRepository) {

                this.productTourRepository = productTourRepository;
                this.mapper = mapper;
                this.historyProductTourRepository = historyProductTourRepository;
        }

        public void createProductTourFromEvent(
                        UUID tourId,
                        UUID cruiseAreaId) {

                boolean exists = productTourRepository
                                .findByTourIdAndCruiseAreaId(
                                                tourId,
                                                cruiseAreaId)
                                .isPresent();

                if (exists) {
                        return;
                }

                ProductTour productTour = new ProductTour();

                productTour.setTourId(tourId);
                productTour.setCruiseAreaId(cruiseAreaId);
                productTour.setStatus(
                                ProductTourStatus.WAITING_CONFIG);

                productTourRepository.save(productTour);
        }

        // =========================================================
        // GET ALL PRODUCT TOURS
        // =========================================================

        @Transactional(readOnly = true)
        public List<ProductTourResponse> getAllAssignments() {

                return productTourRepository
                                .findAll()
                                .stream()
                                .map(mapper::toProductTourResponse)
                                .toList();
        }

        // =========================================================
        // GET PRODUCT TOUR CẦN CẤU HÌNH
        // =========================================================

        @Transactional(readOnly = true)
        public List<ProductTourResponse> getPendingConfig() {

                return productTourRepository
                                .findConfigurable(
                                                List.of(
                                                                ProductTourStatus.WAITING_CONFIG))
                                .stream()
                                .map(mapper::toProductTourResponse)
                                .toList();
        }
        // =========================================================
        // GET CONFIGURATION HISTORY
        // =========================================================

        @Transactional(readOnly = true)
        public List<HistoryProductTourResponse> getConfigurationHistory() {

                return historyProductTourRepository
                                .findAllByOrderByCompletedAtDesc()
                                .stream()
                                .map(HistoryProductTourMapper::toResponse)
                                .toList();
        }
        // =========================================================
        // GET CONFIGURATION HISTORY DETAIL
        // =========================================================

        @Transactional(readOnly = true)
        public List<ProductTourResponse> getConfigurationHistoryDetail(
                        UUID tourId) {

                return productTourRepository
                                .findAllByTourIdOrderByCreatedAtAsc(tourId)
                                .stream()
                                .map(mapper::toProductTourResponse)
                                .toList();
        }
}