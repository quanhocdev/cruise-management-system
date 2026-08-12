package com.project.tour.service.product;

import com.project.common.dto.UploadResult;
import com.project.common.service.file.FileStorageService;
import com.project.tour.dto.product.CreateProductRequest;
import com.project.tour.dto.product.ProductResponse;
import com.project.tour.dto.product.UpdateProductRequest;
import com.project.tour.exception.AppException;
import com.project.tour.mapper.product.ProductMapper;
import com.project.tour.model.CruiseArea;
import com.project.tour.model.Product;
import com.project.tour.repository.cruise.CruiseAreaRepository;
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
    private final CruiseAreaRepository cruiseAreaRepository;
    private final FileStorageService fileStorageService;

    public ProductService(
            ProductRepository productRepository,
            CruiseAreaRepository cruiseAreaRepository,
            FileStorageService fileStorageService) {

        this.productRepository = productRepository;
        this.cruiseAreaRepository = cruiseAreaRepository;
        this.fileStorageService = fileStorageService;
    }

    public ProductResponse createProduct(
            UUID areaId,
            CreateProductRequest request) {

        CruiseArea area = findArea(areaId);

        if (productRepository.existsByCruiseArea_IdAndNameIgnoreCase(
                areaId,
                request.getName())) {

            throw new AppException(
                    "Product name already exists in this area",
                    HttpStatus.CONFLICT);
        }

        Product product = ProductMapper.toEntity(request);

        product.setCruiseArea(area);

        if (request.getImage() != null
                && !request.getImage().isEmpty()) {

            UploadResult uploadResult = fileStorageService.saveMultipart(
                    request.getImage(),
                    "products");

            product.setImageUrl(uploadResult.getUrl());
            product.setImagePublicId(uploadResult.getPublicId());
        }

        Product savedProduct = productRepository.save(product);

        return ProductMapper.toResponse(savedProduct);
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductById(
            UUID areaId,
            UUID productId) {

        Product product = findProduct(areaId, productId);

        return ProductMapper.toResponse(product);
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsByArea(
            UUID areaId) {

        findArea(areaId);

        return productRepository
                .findAllByCruiseArea_IdOrderByNameAsc(areaId)
                .stream()
                .map(ProductMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getActiveProductsByArea(
            UUID areaId) {

        findArea(areaId);

        return productRepository
                .findAllByCruiseArea_IdAndStatusOrderByNameAsc(
                        areaId,
                        com.project.tour.model.enums.ProductStatus.ACTIVE)
                .stream()
                .map(ProductMapper::toResponse)
                .toList();
    }

    public ProductResponse updateProduct(
            UUID areaId,
            UUID productId,
            UpdateProductRequest request) {

        Product product = findProduct(areaId, productId);

        if (productRepository
                .existsByCruiseArea_IdAndNameIgnoreCaseAndIdNot(
                        areaId,
                        request.getName(),
                        productId)) {

            throw new AppException(
                    "Product name already exists in this area",
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

            product.setImageUrl(uploadResult.getUrl());
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

    public void deleteProduct(
            UUID areaId,
            UUID productId) {

        Product product = findProduct(areaId, productId);

        if (product.getImagePublicId() != null
                && !product.getImagePublicId().isBlank()) {

            fileStorageService.delete(
                    product.getImagePublicId());
        }

        productRepository.delete(product);
    }

    private CruiseArea findArea(UUID areaId) {

        return cruiseAreaRepository
                .findById(areaId)
                .orElseThrow(() -> new AppException(
                        "Cruise area not found",
                        HttpStatus.NOT_FOUND));
    }

    private Product findProduct(
            UUID areaId,
            UUID productId) {

        return productRepository
                .findByIdAndCruiseArea_Id(
                        productId,
                        areaId)
                .orElseThrow(() -> new AppException(
                        "Product not found",
                        HttpStatus.NOT_FOUND));
    }
}