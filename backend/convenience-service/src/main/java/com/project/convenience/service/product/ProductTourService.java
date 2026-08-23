package com.project.convenience.service.product;

import com.project.convenience.dto.product.convenience.ProductTourResponse;
import com.project.convenience.mapper.ProductTourMapper;
import com.project.convenience.model.ProductTour;
import com.project.convenience.model.enums.ProductTourStatus;
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

    public ProductTourService(
            ProductTourRepository productTourRepository,
            ProductTourMapper mapper) {

        this.productTourRepository = productTourRepository;
        this.mapper = mapper;
    }

    // =====================================================
    // HÀM MỚI BỔ SUNG: Xử lý Event từ Kafka
    // =====================================================
    public void createProductTourFromEvent(UUID tourId, UUID cruiseAreaId) {
        // 1. Kiểm tra cặp (tourId, cruiseAreaId) đã tồn tại trong DB chưa bằng hàm sẵn
        // có trong Repository
        boolean exists = productTourRepository.findByTourIdAndCruiseAreaId(tourId, cruiseAreaId).isPresent();
        if (exists) {
            return; // Nếu đã tạo rồi thì bỏ qua (tránh duplicate khi Kafka retry)
        }

        // 2. Khởi tạo Entity ProductTour mới
        ProductTour productTour = new ProductTour();
        productTour.setTourId(tourId);
        productTour.setCruiseAreaId(cruiseAreaId);
        productTour.setStatus(ProductTourStatus.WAITING_CONFIG); // Trạng thái chờ cấu hình

        // 3. Lưu vào DB
        productTourRepository.save(productTour);
    }

    // =====================================================
    // GET CONFIGURABLE PRODUCTS
    // =====================================================
    @Transactional(readOnly = true)
    public List<ProductTourResponse> getPendingConfig() {

        return productTourRepository
                .findConfigurable(
                        List.of(
                                ProductTourStatus.WAITING_CONFIG,
                                ProductTourStatus.NOT_STARTED))
                .stream()
                .map(mapper::toProductTourResponse)
                .toList();
    }
}