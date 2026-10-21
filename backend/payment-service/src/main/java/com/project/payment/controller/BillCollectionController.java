package com.project.payment.controller;

import com.project.payment.service.BillCollectionReport;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class BillCollectionController {
    private final BillCollectionReport report;
    private final String key;
    public BillCollectionController(BillCollectionReport report,@Value("${internal.api-key:${INTERNAL_API_KEY:}}") String key) {
        this.report=report;this.key=key;
    }
    @GetMapping("/internal/payments/bill-collections")
    public List<BillCollectionReport.Collection> read(@RequestParam List<Long> billIds,
            @RequestHeader(value="X-Internal-Api-Key",defaultValue="") String supplied) {
        if(key.isBlank() || !MessageDigest.isEqual(key.getBytes(StandardCharsets.UTF_8),supplied.getBytes(StandardCharsets.UTF_8)))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        return report.read(billIds);
    }
}
