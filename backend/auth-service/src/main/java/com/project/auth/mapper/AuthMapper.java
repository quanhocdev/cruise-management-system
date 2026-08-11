package com.project.auth.mapper;

import org.springframework.stereotype.Component;

import com.project.auth.dto.RegisterRequest;
import com.project.auth.dto.RegisterResponse;
import com.project.auth.dto.JwtResponse;
import com.project.auth.model.Role;
import com.project.auth.model.Users;
import com.project.auth.model.enums.UserProvider;
import com.project.auth.model.enums.UserStatus;
import com.project.auth.repository.RoleRepository;

@Component
public class AuthMapper {

    private final RoleRepository roleRepository;

    public AuthMapper(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    /**
     * Map từ RegisterRequest sang Users entity (đã encode password)
     */
    public Users toUserEntity(RegisterRequest request, String encodedPassword) {

        Role passengerRole = roleRepository.findByName("PASSENGER")
                .orElseThrow(() -> new IllegalStateException(
                        "Default role PASSENGER not found"));

        Users user = new Users();

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(encodedPassword);
        user.setRole(passengerRole);
        user.setStatus(UserStatus.ACTIVE);
        user.setProvider(UserProvider.LOCAL);
        user.setEnabled(false);

        return user;
    }

    /**
     * Map từ Users entity và thông báo sang RegisterResponse
     */
    public RegisterResponse toRegisterResponseDTO(
            Users user,
            String message) {
        return new RegisterResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                message);
    }

    /**
     * Map từ Tokens + Users sang JwtResponse trả về cho Client
     */
    public JwtResponse toJwtResponse(
            String accessToken,
            String refreshToken,
            Users user) {
        return new JwtResponse(
                accessToken,
                refreshToken,
                user.getUsername(),
                user.getRole().getName());
    }
}