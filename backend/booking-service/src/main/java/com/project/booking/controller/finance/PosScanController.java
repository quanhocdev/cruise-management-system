package com.project.booking.controller.finance;

import com.project.booking.dto.QrScanRequest;
import com.project.booking.service.finance.PosScanService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/finance")
public class PosScanController {

    private final PosScanService posScanService;

    public PosScanController(PosScanService posScanService) {
        this.posScanService = posScanService;
    }

    @PostMapping("/scan")
    public ResponseEntity<?> scanQrCode(@RequestBody QrScanRequest request) {
        // Gọi Service xử lý logic quét QR và bắn WebSocket
        posScanService.processQrScan(request);

        // Phản hồi HTTP về cho máy POS Android biết là đã tiếp nhận thành công
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Quét mã thành công!"));
    }
}