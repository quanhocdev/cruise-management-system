package com.project.auth.controller.internal;

import com.project.auth.dto.response.ManagerInfoResponse;
import com.project.auth.service.internal.InternalUserService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/internal/users")
public class InternalUserController {

    private final InternalUserService internalUserService;

    public InternalUserController(InternalUserService internalUserService) {
        this.internalUserService = internalUserService;
    }

    @GetMapping("/by-role-name/{roleName}")
    public ResponseEntity<List<ManagerInfoResponse>> getManagersByRoleName(
            @PathVariable String roleName) {
        return ResponseEntity.ok(
                internalUserService.getManagersByRoleName(roleName));
    }
}