
package com.project.tour.service.policy;

import com.project.tour.dto.request.admin.PolicyRefundRuleRequest;
import com.project.tour.dto.response.admin.PolicyRefundRuleResponse;
import com.project.tour.mapper.admin.PolicyRefundRuleMapper;
import com.project.tour.model.admin.Policy;
import com.project.tour.model.admin.PolicyRefundRule;
import com.project.tour.model.enums.policy.PolicyStatus;
import com.project.tour.model.enums.policy.PolicyType;
import com.project.tour.repository.policy.PolicyRefundRuleRepository;
import com.project.tour.repository.policy.PolicyRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class PolicyRefundRuleService {

    private final PolicyRefundRuleRepository policyRefundRuleRepository;
    private final PolicyRepository policyRepository;
    private final PolicyRefundRuleMapper policyRefundRuleMapper;

    public PolicyRefundRuleService(
            PolicyRefundRuleRepository policyRefundRuleRepository,
            PolicyRepository policyRepository,
            PolicyRefundRuleMapper policyRefundRuleMapper) {
        this.policyRefundRuleRepository = policyRefundRuleRepository;
        this.policyRepository = policyRepository;
        this.policyRefundRuleMapper = policyRefundRuleMapper;
    }

    @Transactional
    public PolicyRefundRuleResponse create(
            UUID policyId,
            PolicyRefundRuleRequest request) {
        Policy policy = getValidPolicy(policyId);

        validateRule(request, policyId, null);

        PolicyRefundRule rule = policyRefundRuleMapper.toEntity(
                request,
                policy);

        PolicyRefundRule savedRule = policyRefundRuleRepository.save(rule);

        return policyRefundRuleMapper.toResponse(savedRule);
    }

    @Transactional
    public PolicyRefundRuleResponse update(
            UUID policyId,
            UUID ruleId,
            PolicyRefundRuleRequest request) {
        getValidPolicy(policyId);

        PolicyRefundRule rule = policyRefundRuleRepository
                .findByIdAndPolicy_Id(ruleId, policyId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Refund rule not found"));

        validateRule(request, policyId, ruleId);

        policyRefundRuleMapper.updateEntity(rule, request);

        PolicyRefundRule updatedRule = policyRefundRuleRepository.save(rule);

        return policyRefundRuleMapper.toResponse(updatedRule);
    }

    @Transactional
    public void delete(UUID policyId, UUID ruleId) {
        getValidPolicy(policyId);

        PolicyRefundRule rule = policyRefundRuleRepository
                .findByIdAndPolicy_Id(ruleId, policyId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Refund rule not found"));

        policyRefundRuleRepository.delete(rule);
    }

    @Transactional(readOnly = true)
    public List<PolicyRefundRuleResponse> getByPolicyId(
            UUID policyId) {
        getValidPolicy(policyId);

        List<PolicyRefundRule> rules = policyRefundRuleRepository
                .findByPolicy_IdOrderByMinDaysBeforeDepartureAsc(
                        policyId);

        return policyRefundRuleMapper.toResponseList(rules);
    }

    private Policy getValidPolicy(UUID policyId) {
        Policy policy = policyRepository.findById(policyId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Policy not found"));

        if (policy.getType() != PolicyType.CANCEL) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Refund rules can only be configured for CANCEL policies");
        }

        if (policy.getStatus() != PolicyStatus.ACTIVE) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Cannot configure refund rules for an inactive policy");
        }

        return policy;
    }

    private void validateRule(
            PolicyRefundRuleRequest request,
            UUID policyId,
            UUID excludedRuleId) {
        Integer minDays = request.minDaysBeforeDeparture();
        Integer maxDays = request.maxDaysBeforeDeparture();
        BigDecimal percentage = request.refundPercentage();

        if (minDays == null || minDays < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Minimum days before departure must be greater than or equal to 0");
        }

        if (maxDays != null && maxDays < minDays) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Maximum days must be greater than or equal to minimum days");
        }

        if (percentage == null
                || percentage.compareTo(BigDecimal.ZERO) < 0
                || percentage.compareTo(new BigDecimal("100")) > 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Refund percentage must be between 0 and 100");
        }

        validateNoOverlap(
                policyId,
                minDays,
                maxDays,
                excludedRuleId);
    }

    private void validateNoOverlap(
            UUID policyId,
            Integer newMinDays,
            Integer newMaxDays,
            UUID excludedRuleId) {
        List<PolicyRefundRule> existingRules = policyRefundRuleRepository
                .findByPolicy_IdOrderByMinDaysBeforeDepartureAsc(
                        policyId);

        for (PolicyRefundRule existing : existingRules) {

            if (excludedRuleId != null
                    && existing.getId().equals(excludedRuleId)) {
                continue;
            }

            Integer existingMin = existing.getMinDaysBeforeDeparture();
            Integer existingMax = existing.getMaxDaysBeforeDeparture();

            boolean overlaps = (newMaxDays == null || existingMin <= newMaxDays)
                    && (existingMax == null || newMinDays <= existingMax);

            if (overlaps) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Refund rule day range overlaps with an existing rule");
            }
        }
    }
}
