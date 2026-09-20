package com.project.booking.service.finance;

import com.project.booking.dto.finance.QrScanRequest;

public interface PosScanService {
    void processQrScan(QrScanRequest request);
}