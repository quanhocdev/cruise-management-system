package com.project.auth.service.internal;

import com.project.auth.dto.response.ManagerInfoResponse;

import java.util.List;

public interface InternalUserService {

    List<ManagerInfoResponse> getManagersByRoleName(String roleName);
}