package com.project.tour.repository.policy;

import com.project.tour.model.BookingPolicy;
import com.project.tour.model.enums.PolicyStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookingPolicyRepository
        extends JpaRepository<BookingPolicy, UUID> {

    Optional<BookingPolicy> findByIdAndPolicy_Id(
            UUID id,
            UUID policyId);

    List<BookingPolicy> findAllByPolicy_IdOrderByDaysBeforeDepartureDesc(
            UUID policyId);

    List<BookingPolicy> findAllByPolicy_IdAndStatusOrderByDaysBeforeDepartureDesc(
            UUID policyId,
            PolicyStatus status);

    boolean existsByPolicy_IdAndDaysBeforeDeparture(
            UUID policyId,
            Integer daysBeforeDeparture);

    boolean existsByPolicy_IdAndDaysBeforeDepartureAndIdNot(
            UUID policyId,
            Integer daysBeforeDeparture,
            UUID id);
}