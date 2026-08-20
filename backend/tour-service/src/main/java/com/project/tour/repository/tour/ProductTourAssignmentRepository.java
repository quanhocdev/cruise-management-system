package com.project.tour.repository.tour;

import com.project.tour.model.ProductTour;
import com.project.tour.model.enums.convenience.ProductTourStatus;
import com.project.tour.model.enums.tour.TourStatusTrip;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductTourAssignmentRepository
                extends JpaRepository<ProductTour, UUID> {

        @EntityGraph(attributePaths = { "tour", "cruiseArea", "cruiseArea.cruiseDeck", "product" })
        List<ProductTour> findAllByTourIdOrderByCreatedAtAsc(UUID tourId);

        @EntityGraph(attributePaths = { "tour", "cruiseArea", "cruiseArea.cruiseDeck", "product" })
        List<ProductTour> findAllByStatusOrderByCreatedAtAsc(ProductTourStatus status);

        @EntityGraph(attributePaths = { "tour", "cruiseArea", "cruiseArea.cruiseDeck", "product" })
        List<ProductTour> findAllByTourIdAndStatusOrderByCreatedAtAsc(
                        UUID tourId, ProductTourStatus status);

        // Kiểm tra đã phân công khu vực này cho tour chưa
        Optional<ProductTour> findByTourIdAndCruiseAreaId(UUID tourId, UUID cruiseAreaId);

        // Xóa phân công theo tourId và cruiseAreaId
        void deleteByTourIdAndCruiseAreaId(UUID tourId, UUID cruiseAreaId);

        @Query("""
                        SELECT pt
                        FROM ProductTour pt
                        JOIN FETCH pt.tour t
                        JOIN FETCH pt.cruiseArea ca
                        JOIN FETCH ca.cruiseDeck cd
                        LEFT JOIN FETCH pt.product p
                        WHERE t.statusTrip = :tourStatus
                          AND pt.status = :productTourStatus
                        ORDER BY pt.createdAt ASC
                        """)
        List<ProductTour> findPendingConfig(
                        @Param("tourStatus") TourStatusTrip tourStatus,
                        @Param("productTourStatus") ProductTourStatus productTourStatus);
}