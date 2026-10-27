package com.project.auth.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.auth.dto.RoleResponse;
import com.project.auth.repository.RoleRepository;

@RestController
@RequestMapping("/api/admin/roles")
public class AdminRoleController {

    private final RoleRepository roleRepository;

    public AdminRoleController(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @GetMapping
    public ResponseEntity<List<RoleResponse>> getAllRoles() {
        List<RoleResponse> roles = roleRepository.findAll()
                .stream()
                .map(RoleResponse::fromEntity)
                .toList();

        return ResponseEntity.ok(roles);
    }
}