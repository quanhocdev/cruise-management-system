package com.project.auth.service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import com.project.auth.dto.CreateStaffRequest;
import com.project.auth.dto.StaffResponse;
import com.project.auth.dto.UpdateStaffRequest;
import com.project.auth.dto.UpdateStaffStatusRequest;
import com.project.auth.exception.AppException;
import com.project.auth.mapper.StaffMapper;
import com.project.auth.model.Role;
import com.project.auth.model.Users;
import com.project.auth.model.enums.UserProvider;
import com.project.auth.model.enums.UserStatus;
import com.project.auth.repository.RoleRepository;
import com.project.auth.repository.UserRepository;
import com.project.auth.service.redis.TokenRedisService;
import com.project.common.event.SendStaffInvitationEvent;

@Service
public class AdminStaffServiceImpl implements AdminStaffService {

    private static final Duration ACTIVATION_TOKEN_TTL = Duration.ofMinutes(10);
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final TokenRedisService tokenRedisService;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final StaffMapper staffMapper;

    public AdminStaffServiceImpl(
            UserRepository userRepository,
            RoleRepository roleRepository,
            TokenRedisService tokenRedisService,
            KafkaTemplate<String, Object> kafkaTemplate,
            StaffMapper staffMapper) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.tokenRedisService = tokenRedisService;
        this.kafkaTemplate = kafkaTemplate;
        this.staffMapper = staffMapper;
    }

    // Hàm tạo tài khoản nhân viên mới
    @Override
    public StaffResponse createStaff(CreateStaffRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new AppException("Username đã tồn tại", HttpStatus.BAD_REQUEST);
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new AppException("Email đã tồn tại", HttpStatus.BAD_REQUEST);
        }

        Role role = roleRepository.findById(request.roleId())
                .orElseThrow(() -> new AppException("Role không tồn tại", HttpStatus.BAD_REQUEST));

        validateStaffRole(role);

        Users user = new Users();
        user.setUsername(request.username());
        user.setPassword(null);
        user.setEmail(request.email());
        user.setFirebaseUid(null);
        user.setRole(role);
        user.setProvider(UserProvider.LOCAL);
        user.setEnabled(false);
        user.setStatus(UserStatus.INVITED);

        Users savedUser = userRepository.save(user);

        String activationToken = generateActivationToken();

        tokenRedisService.saveActivationToken(
                activationToken,
                savedUser.getId(),
                ACTIVATION_TOKEN_TTL);

        String activationLink = "http://localhost:5173/activate?token=" + activationToken;

        try {
            SendStaffInvitationEvent event = new SendStaffInvitationEvent(
                    savedUser.getId(),
                    savedUser.getEmail(),
                    savedUser.getUsername(),
                    activationLink,
                    Instant.now());

            kafkaTemplate.send("staff-invitation-topic", event);

        } catch (Exception e) {
            tokenRedisService.deleteActivationToken(activationToken);
            userRepository.delete(savedUser);

            throw new AppException(
                    "Không thể gửi sự kiện kích hoạt tài khoản",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return staffMapper.toStaffResponse(savedUser);
    }

    // Lấy danh sách tất cả nhân viên
    @Override
    public List<StaffResponse> getAllStaff() {
        return userRepository.findAllStaff()
                .stream()
                .map(staffMapper::toStaffResponse)
                .toList();
    }

    // Lấy thông tin nhân viên theo ID
    @Override
    public StaffResponse getStaffById(Long id) {
        Users user = userRepository.findById(id)
                .orElseThrow(() -> new AppException("Không tìm thấy tài khoản", HttpStatus.NOT_FOUND));

        if (!isStaff(user)) {
            throw new AppException("Tài khoản không phải nhân viên", HttpStatus.BAD_REQUEST);
        }

        return staffMapper.toStaffResponse(user);
    }

    // Hàm câp nhật thông tin nhân viên
    @Override
    public StaffResponse updateStaff(Long id, UpdateStaffRequest request) {
        Users user = userRepository.findById(id)
                .orElseThrow(() -> new AppException("Không tìm thấy tài khoản", HttpStatus.NOT_FOUND));

        if (!isStaff(user)) {
            throw new AppException("Tài khoản không phải nhân viên", HttpStatus.BAD_REQUEST);
        }

        if (!user.getUsername().equals(request.username())
                && userRepository.existsByUsername(request.username())) {
            throw new AppException("Username đã tồn tại", HttpStatus.BAD_REQUEST);
        }

        if (!user.getEmail().equals(request.email())
                && userRepository.existsByEmail(request.email())) {
            throw new AppException("Email đã tồn tại", HttpStatus.BAD_REQUEST);
        }

        Role role = roleRepository.findById(request.roleId())
                .orElseThrow(() -> new AppException("Role không tồn tại", HttpStatus.BAD_REQUEST));

        validateStaffRole(role);

        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setRole(role);

        Users savedUser = userRepository.save(user);

        return staffMapper.toStaffResponse(savedUser);
    }

    // Hàm cập nhật trạng thái nhân viên
    @Override
    public StaffResponse updateStaffStatus(Long id, UpdateStaffStatusRequest request) {
        Users user = userRepository.findById(id)
                .orElseThrow(() -> new AppException("Không tìm thấy tài khoản", HttpStatus.NOT_FOUND));

        if (!isStaff(user)) {
            throw new AppException("Tài khoản không phải nhân viên", HttpStatus.BAD_REQUEST);
        }

        UserStatus newStatus = request.status();

        if (newStatus == UserStatus.INVITED) {
            throw new AppException("Không thể chuyển tài khoản về trạng thái INVITED", HttpStatus.BAD_REQUEST);
        }

        user.setStatus(newStatus);
        user.setEnabled(newStatus == UserStatus.ACTIVE);

        Users savedUser = userRepository.save(user);

        return staffMapper.toStaffResponse(savedUser);
    }

    // HELPERS
    private boolean isStaff(Users user) {
        if (user.getRole() == null) {
            return false;
        }
        String roleName = user.getRole().getName();
        return !roleName.equals("PASSENGER") && !roleName.equals("GUEST");
    }

    private void validateStaffRole(Role role) {
        String roleName = role.getName();
        if (roleName.equals("PASSENGER") || roleName.equals("GUEST")) {
            throw new AppException("Role không hợp lệ cho tài khoản nhân viên", HttpStatus.BAD_REQUEST);
        }
    }

    private String generateActivationToken() {
        return UUID.randomUUID().toString().replace("-", "")
                + UUID.randomUUID().toString().replace("-", "");
    }
}