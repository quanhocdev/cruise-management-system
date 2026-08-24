package com.project.convenience.repository;

import com.project.convenience.model.Product;
import com.project.convenience.model.enums.ProductStatus;

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