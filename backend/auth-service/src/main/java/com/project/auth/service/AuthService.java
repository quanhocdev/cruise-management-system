package com.project.auth.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.project.auth.dto.auth.LoginRequestDTO;
import com.project.auth.dto.auth.RegisterRequestDTO;
import com.project.auth.dto.auth.RegisterResponseDTO;
import com.project.auth.model.Users;
import com.project.auth.model.enums.UserRole;
import com.project.auth.model.enums.UserStatus;
import com.project.auth.repository.UserRepository;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
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

        Users savedUser = userRepository.save(user);

        return new RegisterResponseDTO(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                "Đăng ký tài khoản hành khách thành công"
        );
    }

    /**
     * Đăng nhập dùng chung cho tất cả các Role (PASSENGER, ADMIN, SCHEDULER, SHORE, ONBOARD,...)
     */
    public Users login(LoginRequestDTO request) {
        Users user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Tài khoản hoặc mật khẩu không chính xác"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Tài khoản hoặc mật khẩu không chính xác");
        }

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new RuntimeException("Tài khoản đã bị khóa hoặc chưa kích hoạt");
        }

        return user;
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
}