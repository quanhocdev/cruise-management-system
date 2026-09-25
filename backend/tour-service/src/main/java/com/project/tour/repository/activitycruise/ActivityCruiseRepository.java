package com.project.tour.repository.activitycruise;

import com.project.tour.model.activitycruise.ActivityCruise;
import com.project.tour.model.activitycruise.enums.ActivityCruiseStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ActivityCruiseRepository extends JpaRepository<ActivityCruise, UUID> {

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, UUID id);

    List<ActivityCruise> findAllByOrderByNameAsc();

    List<ActivityCruise> findAllByStatusOrderByNameAsc(ActivityCruiseStatus status);
}
