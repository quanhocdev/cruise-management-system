package com.project.tour.repository.policy;

import com.project.tour.model.Policy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PolicyRepository extends JpaRepository<Policy, UUID> {

}
