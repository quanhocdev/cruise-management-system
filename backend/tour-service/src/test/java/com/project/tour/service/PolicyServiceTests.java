package com.project.tour.service;

import com.project.tour.dto.policy.CreatePolicyRequest;
import com.project.tour.dto.policy.PolicyResponse;
import com.project.tour.exception.ResourceNotFoundException;
import com.project.tour.model.Policy;
import com.project.tour.model.enums.PolicyStatus;
import com.project.tour.model.enums.PolicyType;
import com.project.tour.repository.PolicyRepository;
import com.project.tour.service.policy.PolicyService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PolicyServiceTests {
    @Mock
    PolicyRepository repository;
    PolicyService service;

    @BeforeEach
    void setUp() {
        service = new PolicyService(repository);
    }

    @Test
    void createPolicyTrimsTextAndActivatesPolicy() {
        when(repository.save(any(Policy.class))).thenAnswer(invocation -> invocation.getArgument(0));
        PolicyResponse response = service.create(new CreatePolicyRequest(
                PolicyType.CANCEL, " Cancellation ", " Refund conditions "));
        assertEquals("Cancellation", response.title());
        assertEquals("Refund conditions", response.content());
        assertEquals(PolicyStatus.ACTIVE, response.status());
    }

    @Test
    void findByIdAndTypeRejectsWrongPolicyType() {
        UUID id = UUID.randomUUID();
        Policy policy = new Policy();
        policy.setType(PolicyType.REGISTER);
        when(repository.findById(id)).thenReturn(Optional.of(policy));
        assertThrows(IllegalArgumentException.class,
                () -> service.findByIdAndType(id, PolicyType.CANCEL));
    }

    @Test
    void getRejectsMissingPolicy() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.get(id));
    }
}
