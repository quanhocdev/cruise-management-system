package com.project.tour.repository.activityvisit;

import com.project.tour.model.activityvisit.TourOfAcitvityVisit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TourOfAcitvityVisitRepository
        extends JpaRepository<TourOfAcitvityVisit, UUID> {

}
