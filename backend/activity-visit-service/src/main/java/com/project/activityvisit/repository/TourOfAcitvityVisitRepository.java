package com.project.activityvisit.repository;

import com.project.activityvisit.model.TourOfAcitvityVisit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TourOfAcitvityVisitRepository extends JpaRepository<TourOfAcitvityVisit, UUID> {
}