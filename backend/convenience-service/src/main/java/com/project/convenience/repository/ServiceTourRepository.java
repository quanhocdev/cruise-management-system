package com.project.convenience.repository;

import com.project.convenience.model.ServiceTour;
import com.project.convenience.model.enums.ServiceTourStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ServiceTourRepository extends JpaRepository<ServiceTour, UUID> {

    @EntityGraph(attributePaths = { "service" })
    Optional<ServiceTour> findByTourIdAndCruiseAreaId(UUID tourId, UUID cruiseAreaId);

    @EntityGraph(attributePaths = { "service" })
    List<ServiceTour> findAllByTourIdOrderByCreatedAtAsc(UUID tourId);

    @EntityGraph(attributePaths = { "service" })
    List<ServiceTour> findAllByStatusOrderByCreatedAtAsc(ServiceTourStatus status);

    @EntityGraph(attributePaths = { "service" })
    List<ServiceTour> findAllByTourIdAndStatusOrderByCreatedAtAsc(UUID tourId, ServiceTourStatus status);

    void deleteByTourIdAndCruiseAreaId(UUID tourId, UUID cruiseAreaId);

    @Query("""
            SELECT st
            FROM ServiceTour st
            LEFT JOIN FETCH st.service s
            WHERE st.status IN :statuses
            ORDER BY st.createdAt ASC
            """)
    List<ServiceTour> findConfigurable(@Param("statuses") List<ServiceTourStatus> statuses);
}