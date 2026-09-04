package com.project.tour.repository.tour;

import com.project.tour.model.PackageBenefit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PackageBenefitRepository extends JpaRepository<PackageBenefit, UUID> {
    List<PackageBenefit> findAllByTourPackageId(UUID tourPackageId);

    void deleteAllByTourPackageId(UUID tourPackageId);
}