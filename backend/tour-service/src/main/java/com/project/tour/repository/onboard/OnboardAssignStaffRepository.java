
package com.project.tour.repository.onboard;

import com.project.tour.model.OnboardAssignStaff;
import com.project.tour.model.TypeOnboardStaff;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface OnboardAssignStaffRepository
        extends JpaRepository<OnboardAssignStaff, UUID> {

    List<OnboardAssignStaff> findAllByTourIdAndAssignmentTypeAndCruiseDeckIdOrderByAssignedAtDesc(
            UUID tourId,
            TypeOnboardStaff assignmentType,
            UUID cruiseDeckId);

    List<OnboardAssignStaff> findAllByTourIdAndAssignmentTypeAndActivityCruiseTourIdOrderByAssignedAtDesc(
            UUID tourId,
            TypeOnboardStaff assignmentType,
            UUID activityCruiseTourId);

    List<OnboardAssignStaff> findAllByTourIdAndAssignmentTypeAndCruiseDeckIdAndUserIdIn(
            UUID tourId,
            TypeOnboardStaff assignmentType,
            UUID cruiseDeckId,
            Collection<UUID> userIds);

    List<OnboardAssignStaff> findAllByTourIdAndAssignmentTypeAndActivityCruiseTourIdAndUserIdIn(
            UUID tourId,
            TypeOnboardStaff assignmentType,
            UUID activityCruiseTourId,
            Collection<UUID> userIds);

    List<OnboardAssignStaff> findAllByTourIdOrderByAssignedAtDesc(UUID tourId);

    boolean existsByTourIdAndAssignmentTypeAndCruiseDeckIdAndUserId(
            UUID tourId,
            TypeOnboardStaff assignmentType,
            UUID cruiseDeckId,
            UUID userId);

    boolean existsByTourIdAndAssignmentTypeAndActivityCruiseTourIdAndUserId(
            UUID tourId,
            TypeOnboardStaff assignmentType,
            UUID activityCruiseTourId,
            UUID userId);
}
