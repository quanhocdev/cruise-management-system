
package com.project.tour.mapper.admin;

import com.project.tour.dto.request.admin.PolicyRefundRuleRequest;
import com.project.tour.dto.response.admin.PolicyRefundRuleResponse;
import com.project.tour.model.admin.Policy;
import com.project.tour.model.admin.PolicyRefundRule;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PolicyRefundRuleMapper {

    public PolicyRefundRule toEntity(
            PolicyRefundRuleRequest request,
            Policy policy) {
        PolicyRefundRule rule = new PolicyRefundRule();

        rule.setPolicy(policy);
        rule.setMinDaysBeforeDeparture(
                request.minDaysBeforeDeparture());
        rule.setMaxDaysBeforeDeparture(
                request.maxDaysBeforeDeparture());
        rule.setRefundPercentage(
                request.refundPercentage());
        rule.setDescription(
                request.description());

        return rule;
    }

    public void updateEntity(
            PolicyRefundRule rule,
            PolicyRefundRuleRequest request) {
        rule.setMinDaysBeforeDeparture(
                request.minDaysBeforeDeparture());
        rule.setMaxDaysBeforeDeparture(
                request.maxDaysBeforeDeparture());
        rule.setRefundPercentage(
                request.refundPercentage());
        rule.setDescription(
                request.description());
    }

    public PolicyRefundRuleResponse toResponse(
            PolicyRefundRule rule) {
        return new PolicyRefundRuleResponse(
                rule.getId(),
                rule.getPolicy().getId(),
                rule.getMinDaysBeforeDeparture(),
                rule.getMaxDaysBeforeDeparture(),
                rule.getRefundPercentage(),
                rule.getDescription());
    }

    public List<PolicyRefundRuleResponse> toResponseList(
            List<PolicyRefundRule> rules) {
        return rules.stream()
                .map(this::toResponse)
                .toList();
    }
}
