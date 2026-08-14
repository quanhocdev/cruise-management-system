package com.project.tour.service.product;

import com.project.common.dto.UploadResult;
import com.project.common.service.file.FileStorageService;
import com.project.tour.dto.product.CreateProductRequest;
import com.project.tour.dto.product.ProductResponse;
import com.project.tour.dto.product.UpdateProductRequest;
import com.project.tour.exception.AppException;
import com.project.tour.mapper.product.ProductMapper;
import com.project.tour.model.Product;
import com.project.tour.repository.product.ProductRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ProductService {

        private final ProductRepository productRepository;
        private final FileStorageService fileStorageService;

        public ProductService(
                        ProductRepository productRepository,
                        FileStorageService fileStorageService) {

                this.productRepository = productRepository;
                this.fileStorageService = fileStorageService;
        }

        /*
         * =====================================================
         * CREATE
         * =====================================================
         */
        public ProductResponse createProduct(
                        CreateProductRequest request) {

                if (productRepository.existsByNameIgnoreCase(
                                request.getName())) {

                        throw new AppException(
                                        "Product name already exists",
                                        HttpStatus.CONFLICT);
                }

                Product product = ProductMapper.toEntity(request);

                if (request.getImage() != null
                                && !request.getImage().isEmpty()) {

                        UploadResult uploadResult = fileStorageService.saveMultipart(
                                        request.getImage(),
                                        "products");

                        product.setImageUrl(uploadResult.getUrl());
                        product.setImagePublicId(
                                        uploadResult.getPublicId());
                }

                Product savedProduct = productRepository.save(product);

                return ProductMapper.toResponse(savedProduct);
        }

        /*
         * =====================================================
         * GET BY ID
         * =====================================================
         */
        @Transactional(readOnly = true)
        public ProductResponse getProductById(
                        UUID productId) {

                Product product = findProduct(productId);

                return ProductMapper.toResponse(product);
        }

        /*
         * =====================================================
         * GET ALL
         * =====================================================
         */
        @Transactional(readOnly = true)
        public List<ProductResponse> getProducts() {

                return productRepository
                                .findAllByOrderByNameAsc()
                                .stream()
                                .map(ProductMapper::toResponse)
                                .toList();
        }

        /*
         * =====================================================
         * GET ACTIVE
         * =====================================================
         */
        @Transactional(readOnly = true)
        public List<ProductResponse> getActiveProducts() {

                return productRepository
                                .findAllByStatusOrderByNameAsc(
                                                com.project.tour.model.enums.ProductStatus.ACTIVE)
                                .stream()
                                .map(ProductMapper::toResponse)
                                .toList();
        }

        /*
         * =====================================================
         * UPDATE
         * =====================================================
         */
        public ProductResponse updateProduct(
                        UUID productId,
                        UpdateProductRequest request) {

                Product product = findProduct(productId);

                if (productRepository
                                .existsByNameIgnoreCaseAndIdNot(
                                                request.getName(),
                                                productId)) {

                        throw new AppException(
                                        "Product name already exists",
                                        HttpStatus.CONFLICT);
                }

                String oldPublicId = product.getImagePublicId();

                ProductMapper.updateEntity(
                                product,
                                request);

                if (request.getImage() != null
                                && !request.getImage().isEmpty()) {

                        UploadResult uploadResult = fileStorageService.saveMultipart(
                                        request.getImage(),
                                        "products");

                        product.setImageUrl(
                                        uploadResult.getUrl());

                        product.setImagePublicId(
                                        uploadResult.getPublicId());

                        if (oldPublicId != null
                                        && !oldPublicId.isBlank()) {

                                fileStorageService.delete(oldPublicId);
                        }
                }

                Product updatedProduct = productRepository.save(product);

                return ProductMapper.toResponse(updatedProduct);
        }

        /*
         * =====================================================
         * DELETE
         * =====================================================
         */
        public void deleteProduct(UUID productId) {

                Product product = findProduct(productId);

                if (product.getImagePublicId() != null
                                && !product.getImagePublicId().isBlank()) {

                        fileStorageService.delete(
                                        product.getImagePublicId());
                }

                productRepository.delete(product);
        }

        /*
         * =====================================================
         * FIND
         * =====================================================
         */
        private Product findProduct(UUID productId) {

                return productRepository
                                .findById(productId)
                                .orElseThrow(() -> new AppException(
                                                "Product not found",
                                                HttpStatus.NOT_FOUND));
        }
}