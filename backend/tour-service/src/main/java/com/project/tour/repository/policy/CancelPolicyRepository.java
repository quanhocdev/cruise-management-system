package com.project.tour.repository.policy;

import com.project.tour.model.CancelPolicy;
import com.project.tour.model.enums.PolicyStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CancelPolicyRepository extends JpaRepository<CancelPolicy, UUID> {
}
