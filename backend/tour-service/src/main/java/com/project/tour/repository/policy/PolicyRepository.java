package com.project.tour.repository.policy;

import com.project.tour.model.Policy;
import com.project.tour.model.enums.policy.PolicyType;
import com.project.tour.model.enums.policy.PolicyStatus;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PolicyRepository extends JpaRepository<Policy, UUID> {

        List<Policy> findAllByOrderByCreatedAtDesc();

        List<Policy> findAllByStatusOrderByCreatedAtDesc(
                        PolicyStatus status);

        List<Policy> findAllByTypeOrderByCreatedAtDesc(
                        PolicyType type);

        List<Policy> findAllByTypeAndStatusOrderByCreatedAtDesc(
                        PolicyType type,
                        PolicyStatus status);

        boolean existsByType(PolicyType type);

        boolean existsByTypeAndIdNot(
                        PolicyType type,
                        UUID id);
}