package com.project.tour.repository.policy;

import com.project.tour.model.CancelPolicy;
import com.project.tour.model.enums.PolicyStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CancelPolicyRepository extends JpaRepository<CancelPolicy, UUID> {

    Optional<CancelPolicy> findByIdAndPolicy_Id(
            UUID id,
            UUID policyId);

    List<CancelPolicy> findAllByPolicy_IdOrderByDaysBeforeDesc(
            UUID policyId);

    List<CancelPolicy> findAllByPolicy_IdAndStatusOrderByDaysBeforeDesc(
            UUID policyId,
            PolicyStatus status);

    boolean existsByPolicy_IdAndDaysBefore(
            UUID policyId,
            Integer daysBefore);

    boolean existsByPolicy_IdAndDaysBeforeAndIdNot(
            UUID policyId,
            Integer daysBefore,
            UUID id);
}