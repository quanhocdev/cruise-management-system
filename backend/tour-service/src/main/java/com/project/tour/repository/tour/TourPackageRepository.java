package com.project.tour.repository.tour;

import com.project.tour.model.TourPackage;
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
}