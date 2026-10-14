package com.project.booking.service.finance.checkout;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Component
public class BillCodeGenerator {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    public String generate() {
        return "BILL-"
                + LocalDateTime.now().format(FORMATTER)
                + "-"
                + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}