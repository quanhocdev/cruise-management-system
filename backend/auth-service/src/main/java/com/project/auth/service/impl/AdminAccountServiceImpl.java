
package com.project.auth.service.impl;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.project.auth.dto.StaffResponse;
import com.project.auth.dto.UpdateAccountRequest;
import com.project.auth.dto.UpdateStaffStatusRequest;
import com.project.auth.event.ManagerStatusChangedApplicationEvent;
import com.project.auth.mapper.StaffMapper;
import com.project.auth.model.Users;
import com.project.auth.model.enums.UserStatus;
import com.project.auth.repository.UserRepository;
import com.project.auth.service.AdminAccountService;

@Service
@Transactional
public class AdminAccountServiceImpl implements AdminAccountService {

    private static final Set<String> MANAGER_ROLES = Set.of(
            "ONBOARD",
            "CONVENIENCE",
            "FINANCE",
            "SHORE",
            "SCHEDULER");

    private final UserRepository userRepository;
    private final StaffMapper staffMapper;
    private final ApplicationEventPublisher eventPublisher;

    public AdminAccountServiceImpl(
            UserRepository userRepository,
            StaffMapper staffMapper,
            ApplicationEventPublisher eventPublisher) {
        this.userRepository = userRepository;
        this.staffMapper = staffMapper;
        this.eventPublisher = eventPublisher;
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

        UserStatus previousStatus = user.getStatus();
        UserStatus newStatus = request.status();

        if (newStatus == UserStatus.INVITED) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Không thể chuyển tài khoản sang trạng thái INVITED bằng chức năng này");
        }

        user.setStatus(newStatus);
        user.setEnabled(newStatus == UserStatus.ACTIVE);

        Users savedUser = userRepository.save(user);

        publishManagerStatusChangedEvent(
                savedUser,
                previousStatus,
                newStatus);

        return staffMapper.toStaffResponse(savedUser);
    }

    private void publishManagerStatusChangedEvent(
            Users user,
            UserStatus previousStatus,
            UserStatus newStatus) {

        // Không có thay đổi trạng thái.
        if (previousStatus == newStatus) {
            return;
        }

        // Chỉ xử lý khi trạng thái đi qua ranh giới ACTIVE.
        boolean becameInactive = previousStatus == UserStatus.ACTIVE
                && newStatus != UserStatus.ACTIVE;

        boolean becameActive = previousStatus != UserStatus.ACTIVE
                && newStatus == UserStatus.ACTIVE;

        if (!becameInactive && !becameActive) {
            return;
        }

        // Chỉ Manager mới cần kích hoạt cơ chế phân công lại.
        if (user.getRole() == null || user.getRole().getName() == null) {
            return;
        }

        String roleName = user.getRole().getName()
                .trim()
                .toUpperCase();

        if (!MANAGER_ROLES.contains(roleName)) {
            return;
        }

        ManagerStatusChangedApplicationEvent event = new ManagerStatusChangedApplicationEvent(
                user.getId(),
                roleName,
                previousStatus,
                newStatus,
                Instant.now());

        eventPublisher.publishEvent(event);
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
