package com.project.auth.service;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.project.auth.dto.ActivateTokenRequest;
import com.project.auth.dto.CreateStaffRequest;
import com.project.auth.dto.CreateStaffResponse;
import com.project.auth.dto.SetPasswordRequest;
import com.project.auth.exception.AppException;
import com.project.auth.model.Users;
import com.project.auth.model.enums.UserProvider;
import com.project.auth.model.enums.UserStatus;
import com.project.auth.repository.UserRepository;
import com.project.auth.service.mail.MailService;
import com.project.auth.service.redis.TokenRedisService;

@Service
public class StaffServiceImpl implements StaffService {

    private static final Duration ACTIVATION_TOKEN_TTL = Duration.ofMinutes(10);

    private final UserRepository userRepository;
    private final TokenRedisService tokenRedisService;
    private final MailService mailService;
    private final PasswordEncoder passwordEncoder;

    public StaffServiceImpl(
            UserRepository userRepository,
            TokenRedisService tokenRedisService,
            MailService mailService,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.tokenRedisService = tokenRedisService;
        this.mailService = mailService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public CreateStaffResponse createStaff(
            CreateStaffRequest request) {

        // Kiểm tra username
        if (userRepository.existsByUsername(request.username())) {
            throw new AppException(
                    "Username đã tồn tại",
                    HttpStatus.BAD_REQUEST);
        }

        // Kiểm tra email
        if (userRepository.existsByEmail(request.email())) {
            throw new AppException(
                    "Email đã tồn tại",
                    HttpStatus.BAD_REQUEST);
        }

        // Không cho Admin tạo Passenger/GUEST bằng API này
        if (request.role().name().equals("PASSENGER")
                || request.role().name().equals("GUEST")) {

            throw new AppException(
                    "Role không hợp lệ cho tài khoản nhân viên",
                    HttpStatus.BAD_REQUEST);
        }

        /*
         * Staff chưa có password.
         * Password sẽ được tạo khi staff kích hoạt tài khoản.
         */
        Users user = new Users();

        user.setUsername(request.username());
        user.setPassword(null);
        user.setEmail(request.email());
        user.setFirebaseUid(null);
        user.setRole(request.role());
        user.setProvider(UserProvider.LOCAL);
        user.setEnabled(false);
        user.setStatus(UserStatus.INVITED);

        Users savedUser = userRepository.save(user);

        /*
         * Tạo activation token
         */
        String activationToken = generateActivationToken();

        tokenRedisService.saveActivationToken(
                activationToken,
                savedUser.getId(),
                ACTIVATION_TOKEN_TTL);

        /*
         * Link frontend
         */
        String activationLink = "http://localhost:5173/activate?token="
                + activationToken;

        /*
         * Gửi email
         */
        try {

            mailService.sendStaffInvitation(
                    savedUser.getEmail(),
                    savedUser.getUsername(),
                    activationLink);

        } catch (Exception e) {

            // Nếu gửi email thất bại thì xóa user + token
            tokenRedisService.deleteActivationToken(
                    activationToken);

            userRepository.delete(savedUser);

            throw new AppException(
                    "Không thể gửi email kích hoạt tài khoản",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new CreateStaffResponse(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                savedUser.getRole().name(),
                savedUser.getStatus().name(),
                "Tạo tài khoản nhân viên thành công. Email kích hoạt đã được gửi.");
    }

    private String generateActivationToken() {

        return UUID.randomUUID()
                .toString()
                .replace("-", "")
                + UUID.randomUUID()
                        .toString()
                        .replace("-", "");
    }

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

    @Override
    public void setPassword(SetPasswordRequest request) {

        if (!request.password().equals(request.confirmPassword())) {
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

        // Hash password
        user.setPassword(
                passwordEncoder.encode(request.password()));

        // Kích hoạt tài khoản
        user.setStatus(UserStatus.ACTIVE);
        user.setEnabled(true);

        userRepository.save(user);

        // Token chỉ được sử dụng một lần
        tokenRedisService.deleteActivationToken(
                request.token());
    }
}