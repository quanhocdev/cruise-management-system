package com.project.tour.repository.product;

import com.project.tour.model.ProductArea;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductAreaRepository
        extends JpaRepository<ProductArea, UUID> {

    boolean existsByCruiseArea_IdAndProduct_Id(
            UUID areaId,
            UUID productId);

    Optional<ProductArea> findByCruiseArea_IdAndProduct_Id(
            UUID areaId,
            UUID productId);

    List<ProductArea> findAllByCruiseArea_IdOrderByCreatedAtDesc(
            UUID areaId);

    List<ProductArea> findAllByProduct_IdOrderByCreatedAtDesc(
            UUID productId);
}