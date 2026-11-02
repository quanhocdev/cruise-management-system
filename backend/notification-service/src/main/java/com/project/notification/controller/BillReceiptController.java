package com.project.notification.controller;

import com.project.common.event.BillPaidEvent;
import com.project.notification.util.CheckoutInvoicePdfGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@RestController
public class BillReceiptController {
    private final String key;
    public BillReceiptController(@Value("${internal.api-key:${INTERNAL_API_KEY:}}") String key){this.key=key;}
    @PostMapping(value="/internal/bill-receipts",produces=MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> render(@RequestHeader(value="X-Internal-Api-Key",required=false) String supplied,@RequestBody BillPaidEvent event){
        if(key.isBlank()||supplied==null||!MessageDigest.isEqual(key.getBytes(StandardCharsets.UTF_8),supplied.getBytes(StandardCharsets.UTF_8)))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        if(!"PAID".equals(event.billStatus())||event.totalAmount()==null||event.totalAmount().signum()<0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).header(HttpHeaders.CACHE_CONTROL,"no-store").body(CheckoutInvoicePdfGenerator.generate(event));
    }
}
