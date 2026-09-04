package com.project.convenience.service.product;

import com.project.convenience.dto.product.convenience.ProductConvenienceResponse;
import com.project.convenience.mapper.ProductMapper;
import com.project.convenience.model.Product;
import com.project.convenience.model.enums.ProductStatus;
import com.project.convenience.repository.ProductRepository;
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