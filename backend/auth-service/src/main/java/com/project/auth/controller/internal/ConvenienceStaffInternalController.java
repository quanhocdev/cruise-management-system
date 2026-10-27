package com.project.auth.controller.internal;

import com.project.auth.dto.ShoreStaffResponse;
import com.project.auth.service.internal.ConvenienceStaffInternalService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/internal/staff")
public class ConvenienceStaffInternalController {

    private final ConvenienceStaffInternalService convenienceStaffInternalService;

    public ConvenienceStaffInternalController(
            ConvenienceStaffInternalService convenienceStaffInternalService) {
        this.convenienceStaffInternalService = convenienceStaffInternalService;
    }

    @GetMapping("/convenience")
    public ResponseEntity<List<ShoreStaffResponse>> getConvenienceStaff() {

        return ResponseEntity.ok(
                convenienceStaffInternalService.getConvenienceStaff());
    }
}