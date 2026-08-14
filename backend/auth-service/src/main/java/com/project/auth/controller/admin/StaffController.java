package com.project.auth.controller.admin;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.project.auth.dto.CreateStaffRequest;
import com.project.auth.dto.StaffResponse;
import com.project.auth.dto.UpdateStaffRequest;
import com.project.auth.dto.UpdateStaffStatusRequest;
import com.project.auth.service.StaffService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/staff")
public class StaffController {

    private final StaffService staffService;

    public StaffController(StaffService staffService) {
        this.staffService = staffService;
    }

    // =====================================================
    // CREATE STAFF
    // =====================================================

    @PostMapping
    public ResponseEntity<StaffResponse> createStaff(
            @Valid @RequestBody CreateStaffRequest request) {

        StaffResponse response = staffService.createStaff(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // =====================================================
    // GET ALL STAFF
    // =====================================================

    @GetMapping
    public ResponseEntity<List<StaffResponse>> getAllStaff() {

        return ResponseEntity.ok(
                staffService.getAllStaff());
    }

    // =====================================================
    // GET STAFF BY ID
    // =====================================================

    @GetMapping("/{id}")
    public ResponseEntity<StaffResponse> getStaffById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                staffService.getStaffById(id));
    }

    // =====================================================
    // UPDATE STAFF
    // =====================================================

    @PutMapping("/{id}")
    public ResponseEntity<StaffResponse> updateStaff(
            @PathVariable Long id,
            @Valid @RequestBody UpdateStaffRequest request) {

        return ResponseEntity.ok(
                staffService.updateStaff(id, request));
    }

    // =====================================================
    // UPDATE STAFF STATUS
    // =====================================================

    @PatchMapping("/{id}/status")
    public ResponseEntity<StaffResponse> updateStaffStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateStaffStatusRequest request) {

        return ResponseEntity.ok(
                staffService.updateStaffStatus(id, request));
    }
}