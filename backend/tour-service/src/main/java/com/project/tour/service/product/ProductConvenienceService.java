package com.project.tour.service.product;

import com.project.tour.dto.product.ProductConvenienceResponse;
import com.project.tour.mapper.product.ProductMapper;
import com.project.tour.model.Product;
import com.project.tour.model.enums.ProductStatus;
import com.project.tour.repository.product.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProductConvenienceService {

    private final ProductRepository productRepository;

    public ProductConvenienceService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<ProductConvenienceResponse> getProducts() {
        return productRepository.findAllByStatusOrderByNameAsc(ProductStatus.ACTIVE)
                .stream()
                .map(ProductMapper::toConvenienceResponse)
                .toList();
    }

    public ProductConvenienceResponse getProductById(UUID productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với ID: " + productId));

        return ProductMapper.toConvenienceResponse(product);
    }
}