package com.project.auth.service;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.project.auth.dto.ActivateTokenRequest;
import com.project.auth.dto.CreateStaffRequest;
import com.project.auth.dto.SetPasswordRequest;
import com.project.auth.dto.StaffResponse;
import com.project.auth.dto.UpdateStaffRequest;
import com.project.auth.dto.UpdateStaffStatusRequest;
import com.project.auth.exception.AppException;
import com.project.auth.model.Role;
import com.project.auth.model.Users;
import com.project.auth.model.enums.UserProvider;
import com.project.auth.model.enums.UserStatus;
import com.project.auth.repository.RoleRepository;
import com.project.auth.repository.UserRepository;
import com.project.auth.service.mail.MailService;
import com.project.auth.service.redis.TokenRedisService;

@Service
public class StaffServiceImpl implements StaffService {

        private static final Duration ACTIVATION_TOKEN_TTL = Duration.ofMinutes(10);

        private final UserRepository userRepository;
        private final RoleRepository roleRepository;
        private final TokenRedisService tokenRedisService;
        private final MailService mailService;
        private final PasswordEncoder passwordEncoder;

        public StaffServiceImpl(
                        UserRepository userRepository,
                        RoleRepository roleRepository,
                        TokenRedisService tokenRedisService,
                        MailService mailService,
                        PasswordEncoder passwordEncoder) {

                this.userRepository = userRepository;
                this.roleRepository = roleRepository;
                this.tokenRedisService = tokenRedisService;
                this.mailService = mailService;
                this.passwordEncoder = passwordEncoder;
        }

        // =====================================================
        // CREATE STAFF
        // =====================================================

        @Override
        public StaffResponse createStaff(
                        CreateStaffRequest request) {

                if (userRepository.existsByUsername(request.username())) {
                        throw new AppException(
                                        "Username đã tồn tại",
                                        HttpStatus.BAD_REQUEST);
                }

                if (userRepository.existsByEmail(request.email())) {
                        throw new AppException(
                                        "Email đã tồn tại",
                                        HttpStatus.BAD_REQUEST);
                }

                Role role = roleRepository.findById(request.roleId())
                                .orElseThrow(() -> new AppException(
                                                "Role không tồn tại",
                                                HttpStatus.BAD_REQUEST));

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

                String activationLink = "http://localhost:5173/activate?token="
                                + activationToken;

                try {

                        mailService.sendStaffInvitation(
                                        savedUser.getEmail(),
                                        savedUser.getUsername(),
                                        activationLink);

                } catch (Exception e) {

                        tokenRedisService.deleteActivationToken(
                                        activationToken);

                        userRepository.delete(savedUser);

                        throw new AppException(
                                        "Không thể gửi email kích hoạt tài khoản",
                                        HttpStatus.INTERNAL_SERVER_ERROR);
                }

                return toStaffResponse(savedUser);
        }

        // =====================================================
        // GET ALL STAFF
        // =====================================================

        @Override
        public List<StaffResponse> getAllStaff() {

                return userRepository.findAll()
                                .stream()
                                .filter(this::isStaff)
                                .map(this::toStaffResponse)
                                .toList();
        }

        // =====================================================
        // GET STAFF BY ID
        // =====================================================

        @Override
        public StaffResponse getStaffById(Long id) {

                Users user = userRepository.findById(id)
                                .orElseThrow(() -> new AppException(
                                                "Không tìm thấy tài khoản",
                                                HttpStatus.NOT_FOUND));

                if (!isStaff(user)) {
                        throw new AppException(
                                        "Tài khoản không phải nhân viên",
                                        HttpStatus.BAD_REQUEST);
                }

                return toStaffResponse(user);
        }

        // =====================================================
        // UPDATE STAFF
        // =====================================================

        @Override
        public StaffResponse updateStaff(
                        Long id,
                        UpdateStaffRequest request) {

                Users user = userRepository.findById(id)
                                .orElseThrow(() -> new AppException(
                                                "Không tìm thấy tài khoản",
                                                HttpStatus.NOT_FOUND));

                if (!isStaff(user)) {
                        throw new AppException(
                                        "Tài khoản không phải nhân viên",
                                        HttpStatus.BAD_REQUEST);
                }

                if (!user.getUsername().equals(request.username())
                                && userRepository.existsByUsername(request.username())) {

                        throw new AppException(
                                        "Username đã tồn tại",
                                        HttpStatus.BAD_REQUEST);
                }

                if (!user.getEmail().equals(request.email())
                                && userRepository.existsByEmail(request.email())) {

                        throw new AppException(
                                        "Email đã tồn tại",
                                        HttpStatus.BAD_REQUEST);
                }

                Role role = roleRepository.findById(request.roleId())
                                .orElseThrow(() -> new AppException(
                                                "Role không tồn tại",
                                                HttpStatus.BAD_REQUEST));

                validateStaffRole(role);

                user.setUsername(request.username());
                user.setEmail(request.email());
                user.setRole(role);

                Users savedUser = userRepository.save(user);

                return toStaffResponse(savedUser);
        }

        // =====================================================
        // UPDATE STAFF STATUS
        // =====================================================

        @Override
        public StaffResponse updateStaffStatus(
                        Long id,
                        UpdateStaffStatusRequest request) {

                Users user = userRepository.findById(id)
                                .orElseThrow(() -> new AppException(
                                                "Không tìm thấy tài khoản",
                                                HttpStatus.NOT_FOUND));

                if (!isStaff(user)) {
                        throw new AppException(
                                        "Tài khoản không phải nhân viên",
                                        HttpStatus.BAD_REQUEST);
                }

                UserStatus newStatus = request.status();

                if (newStatus == UserStatus.INVITED) {
                        throw new AppException(
                                        "Không thể chuyển tài khoản về trạng thái INVITED",
                                        HttpStatus.BAD_REQUEST);
                }

                user.setStatus(newStatus);

                if (newStatus == UserStatus.ACTIVE) {
                        user.setEnabled(true);
                } else {
                        user.setEnabled(false);
                }

                Users savedUser = userRepository.save(user);

                return toStaffResponse(savedUser);
        }

        // =====================================================
        // VERIFY ACTIVATION TOKEN
        // =====================================================

        @Override
        public String verifyActivationToken(
                        ActivateTokenRequest request) {

                Long userId = tokenRedisService.getActivationUserId(
                                request.token());

                if (userId == null) {
                        throw new AppException(
                                        "Liên kết kích hoạt không hợp lệ hoặc đã hết hạn",
                                        HttpStatus.BAD_REQUEST);
                }

                Users user = userRepository.findById(userId)
                                .orElseThrow(() -> new AppException(
                                                "Không tìm thấy tài khoản",
                                                HttpStatus.NOT_FOUND));

                if (user.getStatus() != UserStatus.INVITED) {
                        throw new AppException(
                                        "Tài khoản này đã được kích hoạt hoặc không thể kích hoạt",
                                        HttpStatus.BAD_REQUEST);
                }

                return user.getUsername();
        }

        // =====================================================
        // SET PASSWORD
        // =====================================================

        @Override
        public void setPassword(
                        SetPasswordRequest request) {

                if (!request.password()
                                .equals(request.confirmPassword())) {

                        throw new AppException(
                                        "Mật khẩu xác nhận không khớp",
                                        HttpStatus.BAD_REQUEST);
                }

                Long userId = tokenRedisService.getActivationUserId(
                                request.token());

                if (userId == null) {
                        throw new AppException(
                                        "Liên kết kích hoạt không hợp lệ hoặc đã hết hạn",
                                        HttpStatus.BAD_REQUEST);
                }

                Users user = userRepository.findById(userId)
                                .orElseThrow(() -> new AppException(
                                                "Không tìm thấy tài khoản",
                                                HttpStatus.NOT_FOUND));

                if (user.getStatus() != UserStatus.INVITED) {
                        throw new AppException(
                                        "Tài khoản này đã được kích hoạt hoặc không thể kích hoạt",
                                        HttpStatus.BAD_REQUEST);
                }

                user.setPassword(
                                passwordEncoder.encode(request.password()));

                user.setStatus(UserStatus.ACTIVE);
                user.setEnabled(true);

                userRepository.save(user);

                tokenRedisService.deleteActivationToken(
                                request.token());
        }

        // =====================================================
        // HELPER - CHECK STAFF
        // =====================================================

        private boolean isStaff(Users user) {

                if (user.getRole() == null) {
                        return false;
                }

                String roleName = user.getRole().getName();

                return !roleName.equals("PASSENGER")
                                && !roleName.equals("GUEST");
        }

        // =====================================================
        // HELPER - VALIDATE STAFF ROLE
        // =====================================================

        private void validateStaffRole(Role role) {

                String roleName = role.getName();

                if (roleName.equals("PASSENGER")
                                || roleName.equals("GUEST")) {

                        throw new AppException(
                                        "Role không hợp lệ cho tài khoản nhân viên",
                                        HttpStatus.BAD_REQUEST);
                }
        }

        // =====================================================
        // MAPPER
        // =====================================================

        private StaffResponse toStaffResponse(Users user) {

                return new StaffResponse(
                                user.getId(),
                                user.getUsername(),
                                user.getEmail(),
                                user.getRole().getId(),
                                user.getRole().getName(),
                                user.getProvider().name(),
                                user.getStatus().name(),
                                user.getEnabled(),
                                user.getCreatedAt(),
                                user.getUpdatedAt());
        }

        // =====================================================
        // GENERATE ACTIVATION TOKEN
        // =====================================================

        private String generateActivationToken() {

                return UUID.randomUUID()
                                .toString()
                                .replace("-", "")
                                + UUID.randomUUID()
                                                .toString()
                                                .replace("-", "");
        }
}