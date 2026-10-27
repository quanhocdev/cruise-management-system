
package com.project.tour.controller.onboard;

import com.project.tour.client.auth.AuthOnboardStaffClient;
import com.project.tour.client.auth.ShoreStaffResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/onboard/staff")
public class OnboardStaffController {

    private final AuthOnboardStaffClient authOnboardStaffClient;

    public OnboardStaffController(
            AuthOnboardStaffClient authOnboardStaffClient) {
        this.authOnboardStaffClient = authOnboardStaffClient;
    }

    @GetMapping
    public List<ShoreStaffResponse> getAvailableStaff() {
        return authOnboardStaffClient.getAllOnboardStaff();
    }
}
