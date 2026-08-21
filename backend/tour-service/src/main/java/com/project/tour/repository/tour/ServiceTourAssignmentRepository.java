package com.project.tour.repository.tour;

import com.project.tour.model.ServiceTour;
import com.project.tour.model.enums.convenience.ServiceTourStatus;
import com.project.tour.model.enums.tour.TourStatusTrip;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ServiceTourAssignmentRepository
                extends JpaRepository<ServiceTour, UUID> {

        @EntityGraph(attributePaths = {
                        "tour",
                        "cruiseArea",
                        "cruiseArea.cruiseDeck",
                        "service"
        })
        Optional<ServiceTour> findById(UUID id);

        @EntityGraph(attributePaths = {
                        "tour",
                        "cruiseArea",
                        "cruiseArea.cruiseDeck",
                        "service"
        })
        Optional<ServiceTour> findByTourIdAndCruiseAreaId(
                        UUID tourId,
                        UUID cruiseAreaId);

        @EntityGraph(attributePaths = {
                        "tour",
                        "cruiseArea",
                        "cruiseArea.cruiseDeck",
                        "service"
        })
        List<ServiceTour> findAllByTourIdOrderByCreatedAtAsc(
                        UUID tourId);

        @EntityGraph(attributePaths = {
                        "tour",
                        "cruiseArea",
                        "cruiseArea.cruiseDeck",
                        "service"
        })
        List<ServiceTour> findAllByStatusOrderByCreatedAtAsc(
                        ServiceTourStatus status);

        @EntityGraph(attributePaths = {
                        "tour",
                        "cruiseArea",
                        "cruiseArea.cruiseDeck",
                        "service"
        })
        List<ServiceTour> findAllByTourIdAndStatusOrderByCreatedAtAsc(
                        UUID tourId,
                        ServiceTourStatus status);

        void deleteByTourIdAndCruiseAreaId(
                        UUID tourId,
                        UUID cruiseAreaId);

        @Query("""
                        SELECT st
                        FROM ServiceTour st
                        JOIN FETCH st.tour t
                        JOIN FETCH st.cruiseArea ca
                        JOIN FETCH ca.cruiseDeck cd
                        LEFT JOIN FETCH st.service s
                        WHERE t.statusTrip = :tourStatus
                          AND st.status IN :statuses
                        ORDER BY st.createdAt ASC
                        """)
        List<ServiceTour> findConfigurable(
                        @Param("tourStatus") TourStatusTrip tourStatus,
                        @Param("statuses") List<ServiceTourStatus> statuses);
}