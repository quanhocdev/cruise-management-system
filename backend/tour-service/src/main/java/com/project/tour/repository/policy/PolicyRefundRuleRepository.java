package com.project.tour.repository.policy;

import com.project.tour.model.admin.PolicyRefundRule;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PolicyRefundRuleRepository extends JpaRepository<PolicyRefundRule, UUID> {

    // Lấy tất cả các mức hoàn tiền thuộc một policy
    List<PolicyRefundRule> findByPolicy_IdOrderByMinDaysBeforeDepartureAsc(UUID policyId);

    // Tìm một mức hoàn tiền theo ID và policy ID
    Optional<PolicyRefundRule> findByIdAndPolicy_Id(UUID id, UUID policyId);

    // Xóa tất cả mức hoàn tiền thuộc một policy
    void deleteByPolicy_Id(UUID policyId);
}