package com.project.auth.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.project.auth.dto.CreateStaffRequest;
import com.project.auth.dto.StaffResponse;
import com.project.auth.dto.UpdateStaffRequest;
import com.project.auth.dto.UpdateStaffStatusRequest;
import com.project.auth.service.AdminStaffService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/staff")
public class AdminStaffController {

    private final AdminStaffService adminStaffService;

    public AdminStaffController(AdminStaffService adminStaffService) {
        this.adminStaffService = adminStaffService;
    }

    // Admin tạo tài khoản nhân viên
    @PostMapping
    public ResponseEntity<StaffResponse> createStaff(
            @Valid @RequestBody CreateStaffRequest request) {

        StaffResponse response = adminStaffService.createStaff(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // Lấy danh sách tất cả nhân viên
    @GetMapping
    public ResponseEntity<List<StaffResponse>> getAllStaff() {
        return ResponseEntity.ok(
                adminStaffService.getAllStaff());
    }

    // Lấy thông tin nhân viên theo ID
    @GetMapping("/{id}")
    public ResponseEntity<StaffResponse> getStaffById(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                adminStaffService.getStaffById(id));
    }

    // Cập nhật thông tin nhân viên
    @PutMapping("/{id}")
    public ResponseEntity<StaffResponse> updateStaff(
            @PathVariable Long id,
            @Valid @RequestBody UpdateStaffRequest request) {
        return ResponseEntity.ok(
                adminStaffService.updateStaff(id, request));
    }

    // Cập nhật trạng thái nhân viên
    @PatchMapping("/{id}/status")
    public ResponseEntity<StaffResponse> updateStaffStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateStaffStatusRequest request) {
        return ResponseEntity.ok(
                adminStaffService.updateStaffStatus(id, request));
    }
}