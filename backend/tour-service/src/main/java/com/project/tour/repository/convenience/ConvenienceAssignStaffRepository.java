
package com.project.tour.repository.convenience;

import com.project.tour.model.ConvenienceAssignStaff;
import com.project.tour.model.StatusAssignStaff;
import com.project.tour.model.TypeConvenienceAssign;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConvenienceAssignStaffRepository
                extends JpaRepository<ConvenienceAssignStaff, UUID> {

        List<ConvenienceAssignStaff> findAllByTourIdAndAssignmentTypeAndTargetIdOrderByAssignedAtDesc(
                        UUID tourId,
                        TypeConvenienceAssign assignmentType,
                        UUID targetId);

        List<ConvenienceAssignStaff> findAllByTourIdAndAssignmentTypeAndTargetIdAndUserIdIn(
                        UUID tourId,
                        TypeConvenienceAssign assignmentType,
                        UUID targetId,
                        Collection<UUID> userIds);

        List<ConvenienceAssignStaff> findAllByTourIdOrderByAssignedAtDesc(UUID tourId);

        boolean existsByTourIdAndAssignmentTypeAndTargetIdAndUserId(
                        UUID tourId,
                        TypeConvenienceAssign assignmentType,
                        UUID targetId,
                        UUID userId);

        Optional<ConvenienceAssignStaff> findByIdAndTourIdAndAssignmentTypeAndTargetIdAndStatus(
                        UUID id,
                        UUID tourId,
                        TypeConvenienceAssign assignmentType,
                        UUID targetId,
                        StatusAssignStaff status);

        List<ConvenienceAssignStaff> findAllByUserIdOrderByAssignedAtDesc(
                        UUID userId);

        Optional<ConvenienceAssignStaff> findByIdAndUserIdAndStatus(
                        UUID id,
                        UUID userId,
                        StatusAssignStaff status);

}
