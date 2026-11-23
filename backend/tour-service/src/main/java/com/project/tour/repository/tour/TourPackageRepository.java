
package com.project.tour.repository.tour;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.tour.dto.internal.TourPriceSearchResponse;
import com.project.tour.model.enums.tour.TourStatusTrip;
import org.springframework.data.domain.Pageable;
import com.project.tour.model.operation.TourPackage;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface TourPackageRepository extends JpaRepository<TourPackage, UUID> {

    List<TourPackage> findAllByTourId(UUID tourId);

    boolean existsByTourIdAndName(UUID tourId, String name);

    @Query("SELECT MIN(tp.price) FROM TourPackage tp WHERE tp.tourId = :tourId")
    BigDecimal findLowestPriceByTourId(@Param("tourId") UUID tourId);

    @Query("SELECT tp FROM TourPackage tp JOIN FETCH tp.roomType WHERE tp.tourId = :tourId")
    List<TourPackage> findAllByTourIdWithRoomType(@Param("tourId") UUID tourId);

    // =====================================================
    // TÌM TOUR THEO GIÁ TỐI THIỂU
    // =====================================================

    @Query("""
            SELECT new com.project.tour.dto.internal.TourPriceSearchResponse(
                t.id,
                t.name,
                MIN(tp.price)
            )
            FROM TourPackage tp
            JOIN Tour t ON t.id = tp.tourId
            WHERE t.statusTrip = :statusTrip
            GROUP BY t.id, t.name
            HAVING MIN(tp.price) >= :minPrice
            ORDER BY t.name ASC
            """)
    List<TourPriceSearchResponse> searchToursByStatusAndMinimumPrice(
            @Param("statusTrip") TourStatusTrip statusTrip,
            @Param("minPrice") BigDecimal minPrice,
            Pageable pageable);

    // =====================================================
    // TÌM TOUR THEO NHIỀU TIÊU CHÍ
    // =====================================================

    @Query("""
            SELECT new com.project.tour.dto.internal.TourPriceSearchResponse(
                t.id,
                t.name,
                MIN(tp.price)
            )
            FROM TourPackage tp
            JOIN Tour t ON t.id = tp.tourId

            LEFT JOIN t.tourRoute tr
            LEFT JOIN tr.departurePort departurePort
            LEFT JOIN tr.destinationPort destinationPort

            WHERE t.statusTrip = :statusTrip

            AND (
                :departurePoint IS NULL
                OR LOWER(departurePort.name) LIKE LOWER(CONCAT('%', :departurePoint, '%'))
                OR LOWER(departurePort.city) LIKE LOWER(CONCAT('%', :departurePoint, '%'))
                OR LOWER(departurePort.country) LIKE LOWER(CONCAT('%', :departurePoint, '%'))
            )

            AND (
                :destination IS NULL
                OR LOWER(destinationPort.name) LIKE LOWER(CONCAT('%', :destination, '%'))
                OR LOWER(destinationPort.city) LIKE LOWER(CONCAT('%', :destination, '%'))
                OR LOWER(destinationPort.country) LIKE LOWER(CONCAT('%', :destination, '%'))
            )

            AND (
                :startDate IS NULL
                OR t.startDate >= :startDate
            )

            AND (
                :endDate IS NULL
                OR t.startDate <= :endDate
            )

            GROUP BY t.id, t.name

            HAVING
                (:minPrice IS NULL OR MIN(tp.price) >= :minPrice)
                AND
                (:maxPrice IS NULL OR MIN(tp.price) <= :maxPrice)

            ORDER BY t.startDate ASC, t.name ASC
            """)
    List<TourPriceSearchResponse> searchToursByCriteria(
            @Param("statusTrip") TourStatusTrip statusTrip,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("departurePoint") String departurePoint,
            @Param("destination") String destination,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable);
}
