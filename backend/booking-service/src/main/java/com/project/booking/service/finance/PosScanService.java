package com.project.booking.service.finance;

import com.project.booking.dto.QrScanRequest;

public interface PosScanService {
    void processQrScan(QrScanRequest request);
}