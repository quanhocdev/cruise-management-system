package com.project.auth.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.project.auth.dto.StaffResponse;
import com.project.auth.dto.UpdateAccountRequest;
import com.project.auth.dto.UpdateStaffStatusRequest;
import com.project.auth.service.AdminAccountService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/accounts")
@Validated
@PreAuthorize("hasRole('ADMIN')")
public class AdminAccountController {

    private final AdminAccountService adminAccountService;

    public AdminAccountController(AdminAccountService adminAccountService) {
        this.adminAccountService = adminAccountService;
    }

    @GetMapping
    public ResponseEntity<List<StaffResponse>> getAllAccounts() {
        return ResponseEntity.ok(
                adminAccountService.getAllAccounts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StaffResponse> getAccountById(
            @PathVariable UUID id) {
        return ResponseEntity.ok(
                adminAccountService.getAccountById(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<StaffResponse> updateAccount(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateAccountRequest request) {
        return ResponseEntity.ok(
                adminAccountService.updateAccount(id, request));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<StaffResponse> updateAccountStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateStaffStatusRequest request) {
        return ResponseEntity.ok(
                adminAccountService.updateAccountStatus(id, request));
    }
}