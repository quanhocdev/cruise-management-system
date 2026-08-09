package com.project.auth.mapper;

import org.springframework.stereotype.Component;

import com.project.auth.dto.RegisterRequest;
import com.project.auth.dto.RegisterResponse;
import com.project.auth.dto.JwtResponse;
import com.project.auth.model.Users;
import com.project.auth.model.enums.UserProvider;
import com.project.auth.model.enums.UserRole;
import com.project.auth.model.enums.UserStatus;

@Component
public class AuthMapper {

    /**
     * Map từ RegisterRequestDTO sang Users entity (đã encode password)
     */
    public Users toUserEntity(RegisterRequest request, String encodedPassword) {
        Users user = new Users();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(encodedPassword);
        user.setRole(UserRole.PASSENGER);
        user.setStatus(UserStatus.ACTIVE);
        user.setProvider(UserProvider.LOCAL);
        user.setEnabled(false);
        return user;
    }

    /**
     * Map từ Users entity và thông báo sang RegisterResponseDTO
     */
    public RegisterResponse toRegisterResponseDTO(Users user, String message) {
        return new RegisterResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                message
        );
    }

    /**
     * Map từ Tokens + Users sang JwtResponse trả về cho Client
     */
    public JwtResponse toJwtResponse(String accessToken, String refreshToken, Users user) {
        return new JwtResponse(
                accessToken,
                refreshToken,
                user.getUsername(),
                user.getRole().name()
        );
    }
}