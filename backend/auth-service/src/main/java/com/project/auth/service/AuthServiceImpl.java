package com.project.auth.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.project.auth.dto.auth.LoginRequestDTO;
import com.project.auth.dto.auth.RegisterRequestDTO;
import com.project.auth.dto.auth.RegisterResponseDTO;
import com.project.auth.dto.auth.VerifyOtpRequestDTO;
import com.project.auth.model.Users;
import com.project.auth.model.enums.UserRole;
import com.project.auth.model.enums.UserStatus;
import com.project.auth.repository.UserRepository;
import com.project.auth.service.redis.RedisService;

@Service
public class AuthServiceImpl implements AuthService{

private final UserRepository userRepository;
private final PasswordEncoder passwordEncoder;
private final JwtService jwtService;
private final RedisService redisService;


public AuthServiceImpl(
        UserRepository userRepository,
        PasswordEncoder passwordEncoder,
        JwtService jwtService,
        RedisService redisService
) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtService = jwtService;
    this.redisService = redisService;
}
    /**
     * Khách ghé thăm (GUEST) tự đăng ký tài khoản -> Chuyển thành Hành khách (PASSENGER)
     */
    public RegisterResponseDTO register(RegisterRequestDTO request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username đã tồn tại");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email đã tồn tại");
        }

        Users user = new Users();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        
        // Luôn cố định vai trò là PASSENGER cho tài khoản tự đăng ký
        user.setRole(UserRole.PASSENGER);
        user.setStatus(UserStatus.ACTIVE);
        user.setEnabled(false);

        Users savedUser = userRepository.save(user);

String otp = generateOtp();

redisService.saveOtp(
        savedUser.getId(),
        otp
);

System.out.println("OTP: " + otp);

return new RegisterResponseDTO(
        savedUser.getId(),
        savedUser.getUsername(),
        savedUser.getEmail(),
        "Đã gửi mã OTP xác thực email"
);
    }

    /**
     * Đăng nhập dùng chung cho tất cả các Role (PASSENGER, ADMIN, SCHEDULER, SHORE, ONBOARD,...)
     */
    public Users login(LoginRequestDTO request) {

    Users user = userRepository.findByUsername(request.getUsername())
            .orElseThrow(() -> new RuntimeException(
                    "Tài khoản hoặc mật khẩu không chính xác"
            ));


    // Kiểm tra mật khẩu
    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
        throw new RuntimeException(
                "Tài khoản hoặc mật khẩu không chính xác"
        );
    }


    // Kiểm tra trạng thái tài khoản
    if (user.getStatus() != UserStatus.ACTIVE) {
        throw new RuntimeException(
                "Tài khoản đã bị khóa"
        );
    }


    // Kiểm tra email đã xác thực chưa
    if (!user.getEnabled()) {
    throw new RuntimeException(
            "Vui lòng xác thực email trước khi đăng nhập"
    );
    }


    return user;
}

    private String generateOtp() {
    int otp = 100000 + new java.util.Random().nextInt(900000);
    return String.valueOf(otp);
}

    public Users refresh(String refreshToken) {
        if (refreshToken == null || !jwtService.isRefreshToken(refreshToken)) {
            throw new RuntimeException("Refresh Token không hợp lệ hoặc đã hết hạn");
        }

        String username = jwtService.extractUsername(refreshToken);

        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new RuntimeException("Tài khoản đã bị khóa");
        }

        return user;
    }
    @Override
public void verifyEmail(VerifyOtpRequestDTO request) {

    String storedOtp = redisService.getOtp(request.getUserId());

    if (storedOtp == null) {
        throw new RuntimeException("OTP đã hết hạn");
    }


    if (!storedOtp.equals(request.getOtp())) {
        throw new RuntimeException("OTP không chính xác");
    }


    Users user = userRepository.findById(request.getUserId())
            .orElseThrow(() -> 
                new RuntimeException("Không tìm thấy tài khoản")
            );


    user.setEnabled(true);

    userRepository.save(user);


    redisService.deleteOtp(request.getUserId());
}
}