package com.project.tour.service.policy;

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

@Service
@Transactional
public class BookingPolicyService {

}
