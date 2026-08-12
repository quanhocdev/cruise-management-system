package com.project.tour.repository.product;

import com.project.tour.model.Product;
import com.project.tour.model.enums.ProductStatus;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository
        extends JpaRepository<Product, UUID> {

    boolean existsByCruiseArea_IdAndNameIgnoreCase(
            UUID areaId,
            String name);

    boolean existsByCruiseArea_IdAndNameIgnoreCaseAndIdNot(
            UUID areaId,
            String name,
            UUID excludedProductId);

    Optional<Product> findByIdAndCruiseArea_Id(
            UUID productId,
            UUID areaId);

    List<Product> findAllByCruiseArea_IdOrderByNameAsc(
            UUID areaId);

    List<Product> findAllByCruiseArea_IdAndStatusOrderByNameAsc(
            UUID areaId,
            ProductStatus status);
}