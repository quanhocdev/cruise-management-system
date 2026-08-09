package com.project.auth.service;

import com.project.auth.dto.LoginRequest;
import com.project.auth.dto.RegisterRequest;
import com.project.auth.dto.RegisterResponse;
import com.project.auth.dto.VerifyOtpRequest;
import com.project.auth.model.Users;

public interface AuthService {

    RegisterResponse register(RegisterRequest request);

    Users login(LoginRequest request);

    Users refresh(String refreshToken);

    void verifyEmail(VerifyOtpRequest request);
}