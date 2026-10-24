package com.project.tour.controller.convenience;

import com.project.tour.client.auth.AuthConvenienceStaffClient;
import com.project.tour.client.auth.ShoreStaffResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/convenience/staff")
public class ConvenienceStaffController {

    private final AuthConvenienceStaffClient authConvenienceStaffClient;

    public ConvenienceStaffController(
            AuthConvenienceStaffClient authConvenienceStaffClient) {
        this.authConvenienceStaffClient = authConvenienceStaffClient;
    }

    @GetMapping
    public List<ShoreStaffResponse> getAvailableStaff() {
        return authConvenienceStaffClient.getAllConvenienceStaff();
    }
}