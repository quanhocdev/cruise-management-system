package com.project.auth.service.internal;

import com.project.auth.dto.ShoreStaffResponse;
import com.project.auth.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ShoreStaffInternalService {

    private final UserRepository userRepository;

    public ShoreStaffInternalService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<ShoreStaffResponse> getShoreStaff() {
        return userRepository.findAllShoreStaffResponses();
    }
}