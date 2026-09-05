// package com.project.tour.repository;

// import com.project.tour.model.TourPackage;
// import com.project.tour.model.enums.TourPackageStatus;
// import org.springframework.data.jpa.repository.JpaRepository;

// import java.util.List;
// import java.util.UUID;

// public interface TourPackageRepository extends JpaRepository<TourPackage,
// UUID> {
// boolean existsByNameIgnoreCase(String name);
// boolean existsByNameIgnoreCaseAndIdNot(String name, UUID id);
// List<TourPackage> findAllByOrderByNameAsc();
// List<TourPackage> findAllByStatusOrderByNameAsc(TourPackageStatus status);
// }
