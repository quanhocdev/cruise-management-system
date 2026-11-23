
package com.project.tour.repository.tour;

import com.project.tour.dto.internal.TourPriceSearchResponse;
import com.project.tour.model.enums.tour.TourStatusTrip;
import com.project.tour.model.operation.TourPackage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
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

    /**
     * Tìm tour READY có giá khởi điểm >= minPrice.
     */
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
            ORDER BY MIN(tp.price) ASC
            """)
    List<TourPriceSearchResponse> searchToursByStatusAndMinimumPrice(
            @Param("statusTrip") TourStatusTrip statusTrip,
            @Param("minPrice") BigDecimal minPrice,
            Pageable pageable);

    /**
     * Tìm tour READY có giá khởi điểm <= maxPrice.
     */
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
            HAVING MIN(tp.price) <= :maxPrice
            ORDER BY MIN(tp.price) ASC
            """)
    List<TourPriceSearchResponse> searchToursByStatusAndMaximumPrice(
            @Param("statusTrip") TourStatusTrip statusTrip,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable);

    /**
     * Tìm tour READY có giá khởi điểm nằm trong khoảng minPrice - maxPrice.
     */
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
               AND MIN(tp.price) <= :maxPrice
            ORDER BY MIN(tp.price) ASC
            """)
    List<TourPriceSearchResponse> searchToursByStatusAndPriceRange(
            @Param("statusTrip") TourStatusTrip statusTrip,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable);
}
