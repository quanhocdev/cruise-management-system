package com.project.tour.service.convenience.product;

import com.project.tour.dto.convenience.product.convenience.ProductConvenienceResponse;
import com.project.tour.mapper.convenience.ProductMapper;
import com.project.tour.model.convenience.enums.ProductStatus;
import com.project.tour.model.convenience.product.Product;
import com.project.tour.repository.convenience.ProductRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class ProductConvenienceService {

    private final ProductRepository productRepository;

    public ProductConvenienceService(
            ProductRepository productRepository) {

        this.productRepository = productRepository;
    }

    public List<ProductConvenienceResponse> getProducts() {

        return productRepository
                .findAllByStatusOrderByNameAsc(ProductStatus.ACTIVE)
                .stream()
                .map(ProductMapper::toConvenienceResponse)
                .toList();
    }

    public ProductConvenienceResponse getProductById(
            UUID productId) {

        Product product = productRepository
                .findById(productId)
                .orElseThrow(() -> new RuntimeException(
                        "Không tìm thấy sản phẩm với ID: " + productId));

        return ProductMapper.toConvenienceResponse(product);
    }
}