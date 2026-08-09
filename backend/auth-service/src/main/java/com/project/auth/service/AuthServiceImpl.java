package com.project.auth.service;

import java.util.Random;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.project.auth.service.mail.MailService;
import com.project.auth.dto.LoginRequest;
import com.project.auth.dto.RegisterRequest;
import com.project.auth.dto.RegisterResponse;
import com.project.auth.dto.VerifyOtpRequest;
import com.project.auth.exception.AppException;
import com.project.auth.mapper.AuthMapper;
import com.project.auth.model.Users;
import com.project.auth.model.enums.UserStatus;
import com.project.auth.repository.UserRepository;
import com.project.auth.service.redis.RedisService;
import com.project.auth.service.redis.TokenRedisService;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RedisService redisService;
    private final MailService mailService;
    private final TokenRedisService tokenRedisService;
    private final AuthMapper authMapper;

    public AuthServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            RedisService redisService,
            MailService mailService,
            TokenRedisService tokenRedisService,
            AuthMapper authMapper
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.redisService = redisService;
        this.mailService = mailService;
        this.tokenRedisService = tokenRedisService;
        this.authMapper = authMapper;
    }

    @Override
    public RegisterResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AppException("Username đã tồn tại", HttpStatus.BAD_REQUEST);
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException("Email đã tồn tại", HttpStatus.BAD_REQUEST);
        }

        Users user = authMapper.toUserEntity(request, passwordEncoder.encode(request.getPassword()));
        Users savedUser = userRepository.save(user);

        String otp = generateOtp();

        try {
            redisService.saveOtp(savedUser.getId(), otp);
        } catch (Exception e) {
            throw new AppException("Lỗi lưu OTP vào hệ thống. Vui lòng thử lại!", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        try {
            mailService.sendOtp(savedUser.getEmail(), otp);
        } catch (Exception e) {
            return authMapper.toRegisterResponseDTO(
                    savedUser,
                    "Đăng ký thành công nhưng không thể gửi email OTP. Vui lòng bấm gửi lại OTP!"
            );
        }

        return authMapper.toRegisterResponseDTO(
                savedUser,
                "Đã gửi mã OTP xác thực email"
        );
    }

    @Override
    public Users login(LoginRequest request) {
        Users user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException("Tài khoản hoặc mật khẩu không chính xác", HttpStatus.UNAUTHORIZED));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AppException("Tài khoản hoặc mật khẩu không chính xác", HttpStatus.UNAUTHORIZED);
        }

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new AppException("Tài khoản đã bị khóa", HttpStatus.FORBIDDEN);
        }

        if (!user.getEnabled()) {
            throw new AppException("Vui lòng xác thực email trước khi đăng nhập", HttpStatus.FORBIDDEN);
        }

        return user;
    }

    @Override
    public Users refresh(String refreshToken) {
        if (refreshToken == null || !jwtService.isRefreshToken(refreshToken)) {
            throw new AppException("Refresh Token không hợp lệ hoặc đã hết hạn", HttpStatus.UNAUTHORIZED);
        }

        String refreshJti = jwtService.extractJti(refreshToken);
        if (refreshJti == null || refreshJti.isBlank()) {
            throw new AppException("Refresh Token không chứa JTI", HttpStatus.UNAUTHORIZED);
        }

        if (!tokenRedisService.existsRefreshToken(refreshJti)) {
            throw new AppException("Refresh Token đã bị thu hồi hoặc không tồn tại", HttpStatus.UNAUTHORIZED);
        }

        String username = jwtService.extractUsername(refreshToken);
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException("Người dùng không tồn tại", HttpStatus.NOT_FOUND));

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new AppException("Tài khoản đã bị khóa", HttpStatus.FORBIDDEN);
        }

        return user;
    }

    @Override
    public void verifyEmail(VerifyOtpRequest request) {
        String storedOtp = redisService.getOtp(request.getUserId());

        if (storedOtp == null) {
            throw new AppException("OTP đã hết hạn", HttpStatus.BAD_REQUEST);
        }

        if (!storedOtp.equals(request.getOtp())) {
            throw new AppException("OTP không chính xác", HttpStatus.BAD_REQUEST);
        }

        Users user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new AppException("Không tìm thấy tài khoản", HttpStatus.NOT_FOUND));

        user.setEnabled(true);
        userRepository.save(user);

        redisService.deleteOtp(request.getUserId());
    }

    private String generateOtp() {
        int otp = 100000 + new Random().nextInt(900000);
        return String.valueOf(otp);
    }
}