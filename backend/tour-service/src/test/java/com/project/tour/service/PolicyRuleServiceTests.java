package com.project.tour.service;

import com.project.tour.dto.policy.CreateBookingPolicyRequest;
import com.project.tour.dto.policy.CreateCancelPolicyRequest;
import com.project.tour.exception.DuplicateResourceException;
import com.project.tour.model.Policy;
import com.project.tour.model.enums.PolicyType;
import com.project.tour.repository.BookingPolicyRepository;
import com.project.tour.repository.CancelPolicyRepository;
import com.project.tour.service.policy.BookingPolicyService;
import com.project.tour.service.policy.CancelPolicyService;
import com.project.tour.service.policy.PolicyService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PolicyRuleServiceTests {
    @Mock
    CancelPolicyRepository cancelRepository;
    @Mock
    BookingPolicyRepository bookingRepository;
    @Mock
    PolicyService policyService;

    @Test
    void cancellationRuleRejectsDuplicateThreshold() {
        UUID policyId = UUID.randomUUID();
        Policy policy = new Policy();
        policy.setType(PolicyType.CANCEL);
        when(policyService.findByIdAndType(policyId, PolicyType.CANCEL)).thenReturn(policy);
        when(cancelRepository.existsByPolicy_IdAndDaysBefore(policyId, 7)).thenReturn(true);
        CancelPolicyService service = new CancelPolicyService(cancelRepository, policyService);
        assertThrows(DuplicateResourceException.class, () -> service.create(
                policyId, new CreateCancelPolicyRequest(7, new BigDecimal("50"))));
    }

    @Test
    void bookingRuleRejectsDuplicateThreshold() {
        UUID policyId = UUID.randomUUID();
        Policy policy = new Policy();
        policy.setType(PolicyType.BOOKING);
        when(policyService.findByIdAndType(policyId, PolicyType.BOOKING)).thenReturn(policy);
        when(bookingRepository.existsByPolicy_IdAndDaysBeforeDeparture(policyId, 30)).thenReturn(true);
        BookingPolicyService service = new BookingPolicyService(bookingRepository, policyService);
        assertThrows(DuplicateResourceException.class, () -> service.create(
                policyId, new CreateBookingPolicyRequest(30, new BigDecimal("10"))));
    }
}
