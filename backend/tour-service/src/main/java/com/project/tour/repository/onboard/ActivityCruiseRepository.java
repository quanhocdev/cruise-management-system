package com.project.tour.repository.onboard;

import com.project.tour.model.ActivityCruise;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityCruiseRepository extends JpaRepository<ActivityCruise, Long> {

    List<ActivityCruise> findByCruiseAreaId(Long cruiseAreaId);

    Page<ActivityCruise> findByCruiseAreaId(Long cruiseAreaId, Pageable pageable);
}