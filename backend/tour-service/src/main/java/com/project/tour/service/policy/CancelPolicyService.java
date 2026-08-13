package com.project.tour.service.policy;

import com.project.tour.dto.policy.cancel.CancelPolicyResponse;
import com.project.tour.dto.policy.cancel.CreateCancelPolicyRequest;
import com.project.tour.dto.policy.cancel.UpdateCancelPolicyRequest;
import com.project.tour.exception.AppException;
import com.project.tour.mapper.policy.CancelPolicyMapper;
import com.project.tour.model.CancelPolicy;
import com.project.tour.model.Policy;
import com.project.tour.model.enums.policy.PolicyType;
import com.project.tour.model.enums.policy.PolicyStatus;
import com.project.tour.repository.policy.CancelPolicyRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class CancelPolicyService {

        private final CancelPolicyRepository repository;
        private final PolicyService policyService;

        public CancelPolicyService(
                        CancelPolicyRepository repository,
                        PolicyService policyService) {

                this.repository = repository;
                this.policyService = policyService;
        }

        // =====================================================
        // CREATE
        // =====================================================

        public CancelPolicyResponse create(
                        UUID policyId,
                        CreateCancelPolicyRequest request) {

                // Kiểm tra Policy tồn tại và phải là CANCEL
                Policy policy = policyService.findByIdAndType(
                                policyId,
                                PolicyType.CANCEL);

                // Không cho phép trùng số ngày
                if (repository.existsByPolicy_IdAndDaysBefore(
                                policyId,
                                request.getDaysBefore())) {

                        throw new AppException(
                                        "Cancellation rule already exists for this day threshold",
                                        HttpStatus.CONFLICT);
                }

                // Request -> Entity
                CancelPolicy entity = CancelPolicyMapper.toEntity(
                                request,
                                policy);

                // Mặc định khi tạo là ACTIVE
                entity.setStatus(PolicyStatus.ACTIVE);

                // Save -> Response
                return CancelPolicyMapper.toResponse(
                                repository.save(entity));
        }

        // =====================================================
        // GET ALL
        // =====================================================

        @Transactional(readOnly = true)
        public List<CancelPolicyResponse> getAll(
                        UUID policyId,
                        boolean activeOnly) {

                // Kiểm tra Policy tồn tại và phải là CANCEL
                policyService.findByIdAndType(
                                policyId,
                                PolicyType.CANCEL);

                List<CancelPolicy> policies;

                if (activeOnly) {

                        policies = repository
                                        .findAllByPolicy_IdAndStatusOrderByDaysBeforeDesc(
                                                        policyId,
                                                        PolicyStatus.ACTIVE);

                } else {

                        policies = repository
                                        .findAllByPolicy_IdOrderByDaysBeforeDesc(
                                                        policyId);
                }

                return policies.stream()
                                .map(CancelPolicyMapper::toResponse)
                                .toList();
        }

        // =====================================================
        // UPDATE
        // =====================================================

        public CancelPolicyResponse update(
                        UUID policyId,
                        UUID ruleId,
                        UpdateCancelPolicyRequest request) {

                CancelPolicy entity = findByIdAndPolicyId(
                                policyId,
                                ruleId);

                // Không cho phép 2 rule cùng số ngày
                if (repository.existsByPolicy_IdAndDaysBeforeAndIdNot(
                                policyId,
                                request.getDaysBefore(),
                                ruleId)) {

                        throw new AppException(
                                        "Cancellation rule already exists for this day threshold",
                                        HttpStatus.CONFLICT);
                }

                // Update Entity
                CancelPolicyMapper.updateEntity(
                                entity,
                                request);

                return CancelPolicyMapper.toResponse(
                                repository.save(entity));
        }

        // =====================================================
        // DEACTIVATE
        // =====================================================

        public void deactivate(
                        UUID policyId,
                        UUID ruleId) {

                CancelPolicy entity = findByIdAndPolicyId(
                                policyId,
                                ruleId);

                entity.setStatus(PolicyStatus.INACTIVE);

                repository.save(entity);
        }

        // =====================================================
        // FIND CANCEL POLICY
        // =====================================================

        private CancelPolicy findByIdAndPolicyId(
                        UUID policyId,
                        UUID ruleId) {

                return repository
                                .findByIdAndPolicy_Id(
                                                ruleId,
                                                policyId)
                                .orElseThrow(() -> new AppException(
                                                "Cancellation rule not found with id: " + ruleId,
                                                HttpStatus.NOT_FOUND));
        }
}