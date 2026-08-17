package com.project.tour.controller.port.scheduler;

import com.project.tour.dto.port.PortResponse;
import com.project.tour.service.port.PortService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/scheduler/ports")
public class SchedulerPortController {

    private final PortService portService;

    public SchedulerPortController(PortService portService) {
        this.portService = portService;
    }

    @GetMapping
    public ResponseEntity<List<PortResponse>> getActivePorts() {
        return ResponseEntity.ok(
                portService.getActivePorts());
    }
}