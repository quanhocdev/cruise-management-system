package com.project.tour.service;

import com.project.tour.dto.policy.*;
import com.project.tour.exception.DuplicateResourceException;
import com.project.tour.exception.ResourceNotFoundException;
import com.project.tour.model.BookingPolicy;
import com.project.tour.model.Policy;
import com.project.tour.model.enums.PolicyStatus;
import com.project.tour.model.enums.PolicyType;
import com.project.tour.repository.BookingPolicyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service @Transactional
public class BookingPolicyService {
    private final BookingPolicyRepository repository;
    private final PolicyService policyService;
    public BookingPolicyService(BookingPolicyRepository repository, PolicyService policyService) {
        this.repository = repository; this.policyService = policyService;
    }
    public BookingPolicyResponse create(UUID policyId, CreateBookingPolicyRequest request) {
        Policy policy = policyService.findByIdAndType(policyId, PolicyType.BOOKING);
        if (repository.existsByPolicy_IdAndDaysBeforeDeparture(policyId, request.daysBeforeDeparture()))
            throw new DuplicateResourceException("Booking rule already exists for this day threshold");
        BookingPolicy rule = new BookingPolicy();
        rule.setPolicy(policy); rule.setDaysBeforeDeparture(request.daysBeforeDeparture());
        rule.setDiscountPercent(request.discountPercent()); rule.setStatus(PolicyStatus.ACTIVE);
        return toResponse(repository.save(rule));
    }
    @Transactional(readOnly = true)
    public List<BookingPolicyResponse> getAll(UUID policyId, boolean activeOnly) {
        policyService.findByIdAndType(policyId, PolicyType.BOOKING);
        List<BookingPolicy> rules = activeOnly
            ? repository.findAllByPolicy_IdAndStatusOrderByDaysBeforeDepartureDesc(policyId, PolicyStatus.ACTIVE)
            : repository.findAllByPolicy_IdOrderByDaysBeforeDepartureDesc(policyId);
        return rules.stream().map(this::toResponse).toList();
    }
    public BookingPolicyResponse update(UUID policyId, UUID id, UpdateBookingPolicyRequest request) {
        BookingPolicy rule = find(policyId, id);
        if (repository.existsByPolicy_IdAndDaysBeforeDepartureAndIdNot(policyId, request.daysBeforeDeparture(), id))
            throw new DuplicateResourceException("Booking rule already exists for this day threshold");
        rule.setDaysBeforeDeparture(request.daysBeforeDeparture()); rule.setDiscountPercent(request.discountPercent());
        rule.setStatus(request.status()); return toResponse(repository.save(rule));
    }
    public BookingPolicyResponse deactivate(UUID policyId, UUID id) {
        BookingPolicy rule = find(policyId, id); rule.setStatus(PolicyStatus.INACTIVE);
        return toResponse(repository.save(rule));
    }
    private BookingPolicy find(UUID policyId, UUID id) {
        return repository.findByIdAndPolicy_Id(id, policyId).orElseThrow(() ->
            new ResourceNotFoundException("Booking rule not found with id: " + id));
    }
    private BookingPolicyResponse toResponse(BookingPolicy r) {
        return new BookingPolicyResponse(r.getId(), r.getPolicy().getId(), r.getDaysBeforeDeparture(), r.getDiscountPercent(), r.getStatus());
    }
}
