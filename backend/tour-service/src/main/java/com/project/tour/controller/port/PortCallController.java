package com.project.tour.controller.port;

import com.project.tour.dto.portcall.*;
import com.project.tour.service.port.PortCallService;

import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/schedules/{scheduleId}/days/{dayId}/port-calls")
public class PortCallController {

}
