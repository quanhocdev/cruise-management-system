package com.project.tour.repository.tour;

import com.project.tour.model.TourPackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TourPackageRepository extends JpaRepository<TourPackage, UUID> {
    List<TourPackage> findAllByTourId(UUID tourId);

    boolean existsByTourIdAndName(UUID tourId, String name);
}