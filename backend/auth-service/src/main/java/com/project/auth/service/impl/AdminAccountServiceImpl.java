package com.project.auth.service.impl;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.project.auth.dto.StaffResponse;
import com.project.auth.dto.UpdateAccountRequest;
import com.project.auth.dto.UpdateStaffStatusRequest;
import com.project.auth.mapper.StaffMapper;
import com.project.auth.model.Users;
import com.project.auth.model.enums.UserStatus;
import com.project.auth.repository.UserRepository;
import com.project.auth.service.AdminAccountService;

@Service
@Transactional
public class AdminAccountServiceImpl implements AdminAccountService {

    private final UserRepository userRepository;
    private final StaffMapper staffMapper;

    public AdminAccountServiceImpl(
            UserRepository userRepository,
            StaffMapper staffMapper) {
        this.userRepository = userRepository;
        this.staffMapper = staffMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<StaffResponse> getAllAccounts() {
        return userRepository.findAll()
                .stream()
                .sorted(
                        Comparator.comparing(
                                Users::getCreatedAt,
                                Comparator.nullsLast(Comparator.reverseOrder())))
                .map(staffMapper::toStaffResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public StaffResponse getAccountById(UUID id) {
        Users user = findAccount(id);
        return staffMapper.toStaffResponse(user);
    }

    @Override
    public StaffResponse updateAccount(
            UUID id,
            UpdateAccountRequest request) {
        Users user = findAccount(id);

        validateUsername(request.username(), id);
        validateEmail(request.email(), id);

        user.setUsername(request.username().trim());
        user.setEmail(request.email().trim());

        Users savedUser = userRepository.save(user);

        return staffMapper.toStaffResponse(savedUser);
    }

    @Override
    public StaffResponse updateAccountStatus(
            UUID id,
            UpdateStaffStatusRequest request) {
        Users user = findAccount(id);

        UserStatus newStatus = request.status();

        if (newStatus == UserStatus.INVITED) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Không thể chuyển tài khoản sang trạng thái INVITED bằng chức năng này");
        }

        user.setStatus(newStatus);
        user.setEnabled(newStatus == UserStatus.ACTIVE);

        Users savedUser = userRepository.save(user);

        return staffMapper.toStaffResponse(savedUser);
    }

    private Users findAccount(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy tài khoản"));
    }

    private void validateUsername(String username, UUID currentId) {
        userRepository.findByUsername(username.trim())
                .filter(existing -> !existing.getId().equals(currentId))
                .ifPresent(existing -> {
                    throw new ResponseStatusException(
                            HttpStatus.CONFLICT,
                            "Username đã tồn tại");
                });
    }

    private void validateEmail(String email, UUID currentId) {
        userRepository.findByEmail(email.trim())
                .filter(existing -> !existing.getId().equals(currentId))
                .ifPresent(existing -> {
                    throw new ResponseStatusException(
                            HttpStatus.CONFLICT,
                            "Email đã được sử dụng");
                });
    }
}