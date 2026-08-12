package com.project.auth.controller.admin;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.project.auth.dto.CreateStaffRequest;
import com.project.auth.dto.CreateStaffResponse;
import com.project.auth.service.StaffService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/staff")
public class StaffController {

    private final StaffService staffService;

    public StaffController(StaffService staffService) {
        this.staffService = staffService;
    }

    @PostMapping
    public ResponseEntity<CreateStaffResponse> createStaff(
            @Valid @RequestBody CreateStaffRequest request) {

        CreateStaffResponse response = staffService.createStaff(request);

        return ResponseEntity.ok(response);
    }
}