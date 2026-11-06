package com.project.chat.controller;

import com.project.chat.dto.response.ManagerInfoResponse;
import com.project.chat.service.ManagerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/message/managers")
public class ManagerController {

    private final ManagerService managerService;

    public ManagerController(ManagerService managerService) {
        this.managerService = managerService;
    }

    @GetMapping("/{roleName}")
    public ResponseEntity<List<ManagerInfoResponse>> getManagersByRoleName(
            @PathVariable String roleName) {
        return ResponseEntity.ok(
                managerService.getManagersByRoleName(roleName));
    }
}