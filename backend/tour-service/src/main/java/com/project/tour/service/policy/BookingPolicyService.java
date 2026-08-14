package com.project.tour.service.policy;

import com.project.tour.dto.policy.booking.BookingPolicyResponse;
import com.project.tour.dto.policy.booking.CreateBookingPolicyRequest;
import com.project.tour.dto.policy.booking.UpdateBookingPolicyRequest;
import com.project.tour.exception.AppException;
import com.project.tour.mapper.policy.BookingPolicyMapper;
import com.project.tour.model.BookingPolicy;
import com.project.tour.model.Policy;
import com.project.tour.model.enums.policy.PolicyType;
import com.project.tour.model.enums.policy.PolicyStatus;
import com.project.tour.repository.policy.BookingPolicyRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class BookingPolicyService {

        private final BookingPolicyRepository repository;
        private final PolicyService policyService;

        public BookingPolicyService(
                        BookingPolicyRepository repository,
                        PolicyService policyService) {

                this.repository = repository;
                this.policyService = policyService;
        }

        // CREATE
        public BookingPolicyResponse create(
                        UUID policyId,
                        CreateBookingPolicyRequest request) {

                Policy policy = policyService.findByIdAndType(
                                policyId,
                                PolicyType.BOOKING);

                if (repository.existsByPolicy_IdAndDaysBeforeDeparture(
                                policyId,
                                request.getDaysBeforeDeparture())) {

                        throw new AppException(
                                        "Booking rule already exists for this day threshold",
                                        HttpStatus.CONFLICT);
                }

                BookingPolicy entity = BookingPolicyMapper.toEntity(
                                request,
                                policy);

                entity.setStatus(PolicyStatus.ACTIVE);

                BookingPolicy saved = repository.save(entity);

                return BookingPolicyMapper.toResponse(saved);
        }

        // GET ALL
        @Transactional(readOnly = true)
        public List<BookingPolicyResponse> getAll(
                        UUID policyId,
                        boolean activeOnly) {

                policyService.findByIdAndType(
                                policyId,
                                PolicyType.BOOKING);

                List<BookingPolicy> policies;

                if (activeOnly) {

                        policies = repository
                                        .findAllByPolicy_IdAndStatusOrderByDaysBeforeDepartureDesc(
                                                        policyId,
                                                        PolicyStatus.ACTIVE);

                } else {

                        policies = repository
                                        .findAllByPolicy_IdOrderByDaysBeforeDepartureDesc(
                                                        policyId);
                }

                return policies.stream()
                                .map(BookingPolicyMapper::toResponse)
                                .toList();
        }

        // UPDATE
        public BookingPolicyResponse update(
                        UUID policyId,
                        UUID ruleId,
                        UpdateBookingPolicyRequest request) {

                BookingPolicy entity = findByIdAndPolicyId(
                                policyId,
                                ruleId);

                if (repository.existsByPolicy_IdAndDaysBeforeDepartureAndIdNot(
                                policyId,
                                request.getDaysBeforeDeparture(),
                                ruleId)) {

                        throw new AppException(
                                        "Booking rule already exists for this day threshold",
                                        HttpStatus.CONFLICT);
                }

                BookingPolicyMapper.updateEntity(
                                entity,
                                request);

                BookingPolicy updated = repository.save(entity);

                return BookingPolicyMapper.toResponse(updated);
        }

        public void delete(
                        UUID policyId,
                        UUID ruleId) {

                BookingPolicy entity = findByIdAndPolicyId(
                                policyId,
                                ruleId);

                repository.delete(entity);
        }

        // FIND RULE
        private BookingPolicy findByIdAndPolicyId(
                        UUID policyId,
                        UUID ruleId) {

                return repository
                                .findByIdAndPolicy_Id(
                                                ruleId,
                                                policyId)
                                .orElseThrow(() -> new AppException(
                                                "Booking rule not found with id: " + ruleId,
                                                HttpStatus.NOT_FOUND));
        }
}