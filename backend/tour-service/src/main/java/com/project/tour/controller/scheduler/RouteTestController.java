package com.project.tour.controller.scheduler;

import com.project.tour.service.scheduler.TourApprovalRouteAsyncService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/scheduler/routes")
public class RouteTestController {

    private final TourApprovalRouteAsyncService routeAsyncService;

    public RouteTestController(TourApprovalRouteAsyncService routeAsyncService) {
        this.routeAsyncService = routeAsyncService;
    }

    // Endpoint gọi ép tính toán lại route cho 1 tour bất kỳ
    @PostMapping("/recalculate/{tourId}")
    public ResponseEntity<String> recalculateRoute(@PathVariable UUID tourId) {
        try {
            // Gọi trực tiếp method tính toán (bỏ qua async để xem log lỗi nếu có)
            routeAsyncService.calculateAndSaveRouteForTour(tourId);
            return ResponseEntity.ok("Đã tính toán lại thành công cho Tour ID: " + tourId);
        } catch (Exception e) {
            e.printStackTrace(); // In toàn bộ vết lỗi đỏ chót ra console backend
            return ResponseEntity.status(500).body("Lỗi thực thi: " + e.getMessage() + " | Nguyên nhân: "
                    + (e.getCause() != null ? e.getCause().getMessage() : "Không rõ"));
        }
    }
}