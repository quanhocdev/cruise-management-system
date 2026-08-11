package com.project.tour.repository;

import com.project.tour.model.Policy;
import com.project.tour.model.enums.PolicyStatus;
import com.project.tour.model.enums.PolicyType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PolicyRepository extends JpaRepository<Policy, UUID> {
    List<Policy> findAllByOrderByCreatedAtDesc();
    List<Policy> findAllByStatusOrderByCreatedAtDesc(PolicyStatus status);
    List<Policy> findAllByTypeAndStatusOrderByCreatedAtDesc(
        PolicyType type,
        PolicyStatus status
    );
}
