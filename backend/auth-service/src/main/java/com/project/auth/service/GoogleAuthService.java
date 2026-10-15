
package com.project.auth.service;

import java.security.SecureRandom;
import java.util.Locale;

import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.project.auth.model.Role;
import com.project.auth.model.Users;
import com.project.auth.model.enums.UserProvider;
import com.project.auth.model.enums.UserStatus;
import com.project.auth.repository.RoleRepository;
import com.project.auth.repository.UserRepository;
import com.project.auth.service.FirebaseTokenService.VerifiedGoogleUser;
import com.project.auth.service.redis.RedisService;
import com.project.common.event.SendOtpEvent;

import java.time.Instant;

@Service
public class GoogleAuthService {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final FirebaseTokenService firebaseTokenService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RedisService redisService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public GoogleAuthService(
            FirebaseTokenService firebaseTokenService,
            UserRepository userRepository,
            RoleRepository roleRepository,
            RedisService redisService,
            KafkaTemplate<String, Object> kafkaTemplate) {
        this.firebaseTokenService = firebaseTokenService;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.redisService = redisService;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Transactional
    public GoogleLoginResult login(String idToken) {
        final VerifiedGoogleUser googleUser;

        try {
            googleUser = firebaseTokenService.verifyIdToken(idToken);
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Firebase ID token không hợp lệ hoặc email chưa xác minh");
        }

        String firebaseUid = googleUser.firebaseUid();
        String email = googleUser.email().trim().toLowerCase(Locale.ROOT);

        // Tài khoản đã liên kết với Firebase UID này.
        Users existingUser = userRepository
                .findByFirebaseUid(firebaseUid)
                .orElse(null);

        if (existingUser != null) {
            ensureAccountCanLogin(existingUser);

            // Không cho UID Google đã liên kết sử dụng email
            // của một tài khoản khác.
            if (!existingUser.getEmail().equalsIgnoreCase(email)) {
                boolean emailOwnedByAnotherUser = userRepository
                        .findByEmail(email)
                        .filter(user -> !user.getId().equals(existingUser.getId()))
                        .isPresent();

                if (emailOwnedByAnotherUser) {
                    throw new ResponseStatusException(
                            HttpStatus.CONFLICT,
                            "Email này đã được sử dụng bởi tài khoản khác");
                }

                existingUser.setEmail(email);
                userRepository.save(existingUser);
            }

            if (Boolean.TRUE.equals(existingUser.getEnabled())) {
                return GoogleLoginResult.authenticated(existingUser);
            }

            // Tài khoản Google đã tạo nhưng chưa xác minh OTP.
            return sendGoogleOtp(existingUser);
        }

        // UID chưa tồn tại: không tự động liên kết chỉ vì email trùng.
        if (userRepository.findByEmail(email).isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Email đã có tài khoản. Vui lòng đăng nhập bằng phương thức đã đăng ký");
        }

        Role passengerRole = roleRepository.findByName("PASSENGER")
                .orElseThrow(() -> new IllegalStateException(
                        "Default role PASSENGER not found"));

        Users newUser = new Users();
        newUser.setUsername(generateUniqueUsername(firebaseUid));
        newUser.setEmail(email);
        newUser.setFirebaseUid(firebaseUid);
        newUser.setProvider(UserProvider.GOOGLE);
        newUser.setRole(passengerRole);
        newUser.setStatus(UserStatus.ACTIVE);
        newUser.setEnabled(false);

        // Google account không dùng mật khẩu LOCAL.
        newUser.setPassword(null);

        Users savedUser = userRepository.save(newUser);

        return sendGoogleOtp(savedUser);
    }

    @Transactional
    public Users verifyOtp(Long userId, String otp) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy tài khoản"));

        if (user.getProvider() != UserProvider.GOOGLE
                || user.getFirebaseUid() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Tài khoản không thuộc luồng xác minh Google");
        }

        ensureAccountCanLogin(user);

        if (Boolean.TRUE.equals(user.getEnabled())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Tài khoản đã được kích hoạt. Vui lòng đăng nhập lại bằng Google");
        }
        String savedOtp = redisService.getGoogleLoginOtp(userId);

        if (savedOtp == null) {
            throw new ResponseStatusException(
                    HttpStatus.GONE,
                    "OTP đã hết hạn hoặc chưa được tạo");
        }

        if (!savedOtp.equals(otp)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "OTP không chính xác");
        }

        user.setEnabled(true);
        Users activatedUser = userRepository.save(user);

        redisService.deleteGoogleLoginOtp(userId);

        return activatedUser;
    }

    private GoogleLoginResult sendGoogleOtp(Users user) {
        String otp = String.format(
                "%06d",
                SECURE_RANDOM.nextInt(1_000_000));

        redisService.saveGoogleLoginOtp(user.getId(), otp);

        SendOtpEvent event = new SendOtpEvent(
                user.getId(),
                user.getEmail(),
                otp,
                Instant.now());

        kafkaTemplate.send("send-otp-topic", event);

        return GoogleLoginResult.otpRequired(
                user.getId(),
                "Vui lòng nhập OTP được gửi đến email để hoàn tất đăng nhập Google");
    }

    private void ensureAccountCanLogin(Users user) {
        if (user.getStatus() == UserStatus.BLOCKED) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Tài khoản đã bị khóa");
        }

        if (user.getStatus() == UserStatus.INACTIVE) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Tài khoản không hoạt động");
        }

        if (user.getStatus() == UserStatus.INVITED) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Tài khoản chưa được kích hoạt theo quy trình của hệ thống");
        }
    }

    private String generateUniqueUsername(String firebaseUid) {
        String sanitizedUid = firebaseUid.replaceAll("[^a-zA-Z0-9]", "")
                .toLowerCase(Locale.ROOT);

        if (sanitizedUid.isBlank()) {
            sanitizedUid = "user";
        }

        String base = "g_" + sanitizedUid;

        if (base.length() > 14) {
            base = base.substring(0, 14);
        }

        String candidate = base;

        while (Boolean.TRUE.equals(
                userRepository.existsByUsername(candidate))) {
            String suffix = String.format(
                    "%04d",
                    SECURE_RANDOM.nextInt(10_000));

            candidate = base.substring(0, Math.min(base.length(), 14))
                    + suffix;
        }

        return candidate;
    }
}
