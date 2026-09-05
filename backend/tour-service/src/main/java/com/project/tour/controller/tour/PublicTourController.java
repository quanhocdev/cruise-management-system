package com.project.tour.controller.tour;

import com.project.tour.dto.tour.PublicTourDetailResponse;
import com.project.tour.dto.tour.PublicTourSummaryResponse;
import com.project.tour.service.tour.PublicTourService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/public/tours")
public class PublicTourController {

    private final PublicTourService publicTourService;

    public PublicTourController(PublicTourService publicTourService) {
        this.publicTourService = publicTourService;
    }

    // 1. Lấy danh sách tour rút gọn (kèm ảnh tàu, trạng thái booking, giá khởi
    // điểm) cho trang chủ
    @GetMapping
    public ResponseEntity<List<PublicTourSummaryResponse>> getPublicTours() {
        return ResponseEntity.ok(publicTourService.getPublicTourSummaries());
    }

    // 2. Lấy chi tiết đầy đủ của 1 tour (lịch trình, điểm dừng, gói, hoạt động,
    // dịch vụ...)
    @GetMapping("/{id}")
    public ResponseEntity<PublicTourDetailResponse> getPublicTourDetail(@PathVariable UUID id) {
        return ResponseEntity.ok(publicTourService.getPublicTourDetail(id));
    }
}