package com.project.auth.service;

import com.project.auth.dto.ManagerInfoResponse;

import java.util.List;

public interface InternalUserService {

    List<ManagerInfoResponse> getManagersByRoleName(String roleName);
}