package com.project.tour.repository.shore;

import com.project.tour.model.ShoreAssignStaff;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Collection;
import java.util.UUID;

@Repository
public interface ShoreAssignStaffRepository
                extends JpaRepository<ShoreAssignStaff, UUID> {

        List<ShoreAssignStaff> findAllByVisitTourIdOrderByAssignedAtDesc(
                        UUID visitTourId);

        List<ShoreAssignStaff> findAllByTourIdOrderByAssignedAtDesc(
                        UUID tourId);

        List<ShoreAssignStaff> findAllByVisitTourIdAndUserIdIn(
                        UUID visitTourId,
                        Collection<UUID> userIds);

        boolean existsByVisitTourIdAndUserId(
                        UUID visitTourId,
                        UUID userId);
}