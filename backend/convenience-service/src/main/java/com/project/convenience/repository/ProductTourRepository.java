package com.project.convenience.repository;

import com.project.convenience.model.ProductTour;
import com.project.convenience.model.enums.ProductTourStatus;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductTourRepository extends JpaRepository<ProductTour, UUID> {

    // Lấy danh sách ProductTour theo tourId (Chỉ load kèm Product)
    @EntityGraph(attributePaths = { "product" })
    List<ProductTour> findAllByTourIdOrderByCreatedAtAsc(UUID tourId);

    // Lấy danh sách theo status
    @EntityGraph(attributePaths = { "product" })
    List<ProductTour> findAllByStatusOrderByCreatedAtAsc(ProductTourStatus status);

    // Lấy danh sách theo tourId và status
    @EntityGraph(attributePaths = { "product" })
    List<ProductTour> findAllByTourIdAndStatusOrderByCreatedAtAsc(UUID tourId, ProductTourStatus status);

    // Kiểm tra/Tìm bản ghi theo tourId và cruiseAreaId
    Optional<ProductTour> findByTourIdAndCruiseAreaId(UUID tourId, UUID cruiseAreaId);

    // Xóa theo tourId và cruiseAreaId
    void deleteByTourIdAndCruiseAreaId(UUID tourId, UUID cruiseAreaId);

    // Lấy danh sách ProductTour theo trạng thái status (Chỉ join fetch product)
    @Query("""
            SELECT pt
            FROM ProductTour pt
            LEFT JOIN FETCH pt.product p
            WHERE pt.status = :productTourStatus
            ORDER BY pt.createdAt ASC
            """)
    List<ProductTour> findPendingConfig(@Param("productTourStatus") ProductTourStatus productTourStatus);

    // Lấy danh sách theo nhiều trạng thái status
    @Query("""
            SELECT pt
            FROM ProductTour pt
            LEFT JOIN FETCH pt.product p
            WHERE pt.status IN :statuses
            ORDER BY pt.createdAt ASC
            """)
    List<ProductTour> findConfigurable(@Param("statuses") List<ProductTourStatus> statuses);
}