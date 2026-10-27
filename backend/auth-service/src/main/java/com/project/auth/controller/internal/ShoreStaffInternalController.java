package com.project.auth.controller.internal;

import com.project.auth.dto.ShoreStaffResponse;
import com.project.auth.service.internal.ShoreStaffInternalService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/internal/staff")
public class ShoreStaffInternalController {

        private final ShoreStaffInternalService shoreStaffInternalService;

        public ShoreStaffInternalController(
                        ShoreStaffInternalService shoreStaffInternalService) {
                this.shoreStaffInternalService = shoreStaffInternalService;
        }

        @GetMapping("/shore")
        public ResponseEntity<List<ShoreStaffResponse>> getShoreStaff() {

                return ResponseEntity.ok(
                                shoreStaffInternalService.getShoreStaff());
        }
}