package com.project.tour.repository.convenience;

import com.project.tour.model.convenience.enums.ProductStatus;
import com.project.tour.model.convenience.product.Product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

        /**
         * Trừ tồn kho Product một cách atomic.
         *
         * Chỉ trừ khi stock hiện tại >= quantity.
         *
         * @return số row được update
         *         1 = thành công
         *         0 = không đủ tồn kho
         */
        @Modifying
        @Query("""
                        UPDATE Product p
                        SET p.stockQuantity = p.stockQuantity - :quantity
                        WHERE p.id = :productId
                          AND p.stockQuantity >= :quantity
                        """)
        int decreaseStock(
                        @Param("productId") UUID productId,
                        @Param("quantity") Integer quantity);

        @Modifying
        @Query("""
                        UPDATE Product p
                        SET p.stockQuantity = p.stockQuantity + :quantity
                        WHERE p.id = :productId
                        """)
        int increaseStock(
                        @Param("productId") UUID productId,
                        @Param("quantity") Integer quantity);
}