package com.project.tour.controller.shore;

import com.project.tour.client.auth.AuthShoreStaffClient;
import com.project.tour.client.auth.ShoreStaffResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/shore/staff")
public class ShoreStaffController {

    private final AuthShoreStaffClient authStaffClient;

    public ShoreStaffController(AuthShoreStaffClient authStaffClient) {
        this.authStaffClient = authStaffClient;
    }

    /**
     * Danh sách nhân viên đang hoạt động có role SHORE_STAFF.
     */
    @GetMapping
    public List<ShoreStaffResponse> getAvailableStaff() {
        return authStaffClient.getAllShoreStaff();
    }
}