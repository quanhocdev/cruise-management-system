package com.project.auth.controller.internal;

import com.project.auth.dto.ShoreStaffResponse;
import com.project.auth.service.internal.OnboardStaffInternalService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/internal/staff")
public class OnboardStaffInternalController {

    private final OnboardStaffInternalService onboardStaffInternalService;

    public OnboardStaffInternalController(
            OnboardStaffInternalService onboardStaffInternalService) {
        this.onboardStaffInternalService = onboardStaffInternalService;
    }

    @GetMapping("/onboard")
    public ResponseEntity<List<ShoreStaffResponse>> getOnboardStaff() {
        return ResponseEntity.ok(
                onboardStaffInternalService.getOnboardStaff());
    }
}