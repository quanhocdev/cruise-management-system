package com.project.tour.repository.product;

import com.project.tour.model.Product;
import com.project.tour.model.enums.ProductStatus;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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