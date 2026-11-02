package com.project.booking.controller.finance;

import com.project.booking.service.account.FinanceReportService;
import java.util.UUID;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestController
@RequestMapping("/api/finance/bookings/reports")
public class FinanceReportController {
    private final FinanceReportService reports;
    public FinanceReportController(FinanceReportService reports){this.reports=reports;}
    @GetMapping("/services")
    public FinanceReportService.Report read(@RequestParam UUID tourId,@RequestParam(defaultValue="0") int page,
            @RequestParam(defaultValue="20") int size,@RequestParam(defaultValue="ALL") String status,
            @RequestParam(defaultValue="newest") String sort) {
        return reports.read(tourId,page,size,status,sort);
    }

    @ExceptionHandler({MissingServletRequestParameterException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<java.util.Map<String,String>> invalidQuery(Exception exception) {
        return ResponseEntity.badRequest().body(java.util.Map.of("code","INVALID_REPORT_QUERY",
                "message","Cần chọn chuyến và cung cấp bộ lọc hợp lệ"));
    }
}
