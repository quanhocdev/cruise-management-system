
package com.project.tour.repository.shore;

import com.project.tour.model.ShoreAssignStaff;
import com.project.tour.model.StatusAssignStaff;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ShoreAssignStaffRepository
        extends JpaRepository<ShoreAssignStaff, UUID> {

    // GET ALL: phân công mới nhất trước
    List<ShoreAssignStaff> findAllByOrderByCreatedAtDesc();

    // GET BY USER: danh sách nhiệm vụ của nhân viên
    List<ShoreAssignStaff> findAllByUserIdOrderByAssignedAtDesc(
            UUID userId);

    // GET BY TOUR
    List<ShoreAssignStaff> findAllByTourIdOrderByAssignedAtDesc(
            UUID tourId);

    // GET BY VISIT TOUR
    List<ShoreAssignStaff> findAllByVisitTourIdOrderByAssignedAtDesc(
            UUID visitTourId);

    // GET BY USER + STATUS
    List<ShoreAssignStaff> findAllByUserIdAndStatusOrderByAssignedAtDesc(
            UUID userId,
            StatusAssignStaff status);

    // GET BY TOUR + STATUS
    List<ShoreAssignStaff> findAllByTourIdAndStatusOrderByAssignedAtDesc(
            UUID tourId,
            StatusAssignStaff status);

    // GET BY VISIT TOUR + STATUS
    List<ShoreAssignStaff> findAllByVisitTourIdAndStatusOrderByAssignedAtDesc(
            UUID visitTourId,
            StatusAssignStaff status);

    // FIND ASSIGNMENT
    Optional<ShoreAssignStaff> findByIdAndUserId(
            UUID id,
            UUID userId);

    // CHECK EXISTING ASSIGNMENT
    boolean existsByUserIdAndVisitTourIdAndStatusIn(
            UUID userId,
            UUID visitTourId,
            List<StatusAssignStaff> statuses);
}
