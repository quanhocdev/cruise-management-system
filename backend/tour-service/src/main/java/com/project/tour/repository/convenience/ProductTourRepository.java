package com.project.tour.repository.convenience;

import com.project.tour.model.convenience.enums.ProductTourStatus;
import com.project.tour.model.convenience.product.ProductTour;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductTourRepository
        extends JpaRepository<ProductTour, UUID> {

    boolean existsByTourIdAndCruiseAreaId(
            UUID tourId,
            UUID cruiseAreaId);

    @EntityGraph(attributePaths = { "product" })
    List<ProductTour> findAllByTourIdOrderByCreatedAtAsc(
            UUID tourId);

    @EntityGraph(attributePaths = { "product" })
    List<ProductTour> findAllByOrderByCreatedAtAsc();

    @EntityGraph(attributePaths = { "product" })
    List<ProductTour> findAllByStatusOrderByCreatedAtAsc(
            ProductTourStatus status);

    @EntityGraph(attributePaths = { "product" })
    List<ProductTour> findAllByTourIdAndStatusOrderByCreatedAtAsc(
            UUID tourId,
            ProductTourStatus status);

    Optional<ProductTour> findByTourIdAndCruiseAreaId(
            UUID tourId,
            UUID cruiseAreaId);

    void deleteByTourIdAndCruiseAreaId(
            UUID tourId,
            UUID cruiseAreaId);

    @Query("""
            SELECT pt
            FROM ProductTour pt
            LEFT JOIN FETCH pt.product p
            WHERE pt.status = :productTourStatus
            ORDER BY pt.createdAt ASC
            """)
    List<ProductTour> findPendingConfig(
            @Param("productTourStatus") ProductTourStatus productTourStatus);

    @Query("""
            SELECT pt
            FROM ProductTour pt
            LEFT JOIN FETCH pt.product p
            WHERE pt.status IN :statuses
            ORDER BY pt.createdAt ASC
            """)
    List<ProductTour> findConfigurable(
            @Param("statuses") List<ProductTourStatus> statuses);
}