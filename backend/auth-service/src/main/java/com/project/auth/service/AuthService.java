package com.project.auth.service;

import com.project.auth.dto.auth.LoginRequestDTO;
import com.project.auth.dto.auth.RegisterRequestDTO;
import com.project.auth.dto.auth.RegisterResponseDTO;
import com.project.auth.dto.auth.VerifyOtpRequestDTO;
import com.project.auth.model.Users;
import com.project.auth.dto.auth.VerifyOtpRequestDTO;

public interface AuthService {

    RegisterResponseDTO register(RegisterRequestDTO request);

    Users login(LoginRequestDTO request);

    Users refresh(String refreshToken);

    void verifyEmail(VerifyOtpRequestDTO request);

}