// package com.project.tour.service.product;

// import com.project.tour.dto.product.area.CreateProductAreaRequest;
// import com.project.tour.dto.product.area.ProductAreaResponse;
// import com.project.tour.exception.AppException;
// import com.project.tour.mapper.product.ProductAreaMapper;
// import com.project.tour.model.CruiseArea;
// import com.project.tour.model.Product;
// import com.project.tour.model.ProductArea;
// import com.project.tour.repository.cruise.CruiseAreaRepository;
// import com.project.tour.repository.product.ProductAreaRepository;
// import com.project.tour.repository.product.ProductRepository;

// import org.springframework.http.HttpStatus;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;

// import java.util.List;
// import java.util.UUID;

// @Service
// @Transactional
// public class ProductAreaService {

// private final ProductAreaRepository productAreaRepository;
// private final CruiseAreaRepository cruiseAreaRepository;
// private final ProductRepository productRepository;

// public ProductAreaService(
// ProductAreaRepository productAreaRepository,
// CruiseAreaRepository cruiseAreaRepository,
// ProductRepository productRepository) {

// this.productAreaRepository = productAreaRepository;
// this.cruiseAreaRepository = cruiseAreaRepository;
// this.productRepository = productRepository;
// }

// /*
// * =====================================================
// * ASSIGN PRODUCT TO AREA
// * =====================================================
// */
// public ProductAreaResponse assignProduct(
// UUID areaId,
// CreateProductAreaRequest request) {

// CruiseArea area = findArea(areaId);

// Product product = findProduct(request.getProductId());

// if (productAreaRepository
// .existsByCruiseArea_IdAndProduct_Id(
// areaId,
// product.getId())) {

// throw new AppException(
// "Product is already assigned to this area",
// HttpStatus.CONFLICT);
// }

// ProductArea productArea = ProductAreaMapper.toEntity(request);

// productArea.setCruiseArea(area);
// productArea.setProduct(product);

// ProductArea savedProductArea = productAreaRepository.save(productArea);

// return ProductAreaMapper.toResponse(savedProductArea);
// }

// /*
// * =====================================================
// * GET PRODUCTS IN AREA
// * =====================================================
// */
// @Transactional(readOnly = true)
// public List<ProductAreaResponse> getProductsByArea(
// UUID areaId) {

// findArea(areaId);

// return productAreaRepository
// .findAllByCruiseArea_IdOrderByCreatedAtDesc(areaId)
// .stream()
// .map(ProductAreaMapper::toResponse)
// .toList();
// }

// /*
// * =====================================================
// * REMOVE PRODUCT FROM AREA
// * =====================================================
// */
// public void removeProduct(
// UUID areaId,
// UUID productId) {

// ProductArea productArea = productAreaRepository
// .findByCruiseArea_IdAndProduct_Id(
// areaId,
// productId)
// .orElseThrow(() -> new AppException(
// "Product is not assigned to this area",
// HttpStatus.NOT_FOUND));

// productAreaRepository.delete(productArea);
// }

// /*
// * =====================================================
// * FIND AREA
// * =====================================================
// */
// private CruiseArea findArea(UUID areaId) {

// return cruiseAreaRepository
// .findById(areaId)
// .orElseThrow(() -> new AppException(
// "Cruise area not found",
// HttpStatus.NOT_FOUND));
// }

// /*
// * =====================================================
// * FIND PRODUCT
// * =====================================================
// */
// private Product findProduct(UUID productId) {

// return productRepository
// .findById(productId)
// .orElseThrow(() -> new AppException(
// "Product not found",
// HttpStatus.NOT_FOUND));
// }
// }