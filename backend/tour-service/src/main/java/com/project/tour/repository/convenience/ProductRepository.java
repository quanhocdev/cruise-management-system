package com.project.tour.repository.convenience;

import com.project.tour.model.convenience.enums.ProductStatus;
import com.project.tour.model.convenience.product.Product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository
        extends JpaRepository<Product, UUID> {

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(
            String name,
            UUID excludedProductId);

    Optional<Product> findById(UUID productId);

    List<Product> findAllByOrderByNameAsc();

    List<Product> findAllByStatusOrderByNameAsc(
            ProductStatus status);
}