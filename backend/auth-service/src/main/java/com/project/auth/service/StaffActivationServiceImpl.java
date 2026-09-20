package com.project.auth.service;

import java.time.Duration;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.project.auth.dto.ActivateTokenRequest;
import com.project.auth.dto.SetPasswordRequest;
import com.project.auth.exception.AppException;
import com.project.auth.model.Users;
import com.project.auth.model.enums.UserStatus;
import com.project.auth.repository.UserRepository;
import com.project.auth.service.redis.TokenRedisService;

@Service
public class StaffActivationServiceImpl implements StaffActivationService {

        private static final Duration ACTIVATION_TOKEN_TTL = Duration.ofMinutes(10);

        private final UserRepository userRepository;
        private final TokenRedisService tokenRedisService;
        private final PasswordEncoder passwordEncoder;

        public StaffActivationServiceImpl(
                        UserRepository userRepository,
                        TokenRedisService tokenRedisService,
                        PasswordEncoder passwordEncoder) {
                this.userRepository = userRepository;
                this.tokenRedisService = tokenRedisService;
                this.passwordEncoder = passwordEncoder;
        }

        // =====================================================
        // VERIFY ACTIVATION TOKEN
        // =====================================================

        @Override
        public String verifyActivationToken(ActivateTokenRequest request) {
                Long userId = tokenRedisService.getActivationUserId(request.token());

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
        public void setPassword(SetPasswordRequest request) {
                if (!request.password().equals(request.confirmPassword())) {
                        throw new AppException(
                                        "Mật khẩu xác nhận không khớp",
                                        HttpStatus.BAD_REQUEST);
                }

                Long userId = tokenRedisService.getActivationUserId(request.token());

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

                user.setPassword(passwordEncoder.encode(request.password()));
                user.setStatus(UserStatus.ACTIVE);
                user.setEnabled(true);

                userRepository.save(user);

                tokenRedisService.deleteActivationToken(request.token());
        }
}