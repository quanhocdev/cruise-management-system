package com.project.booking.controller.finance;

import com.project.booking.service.finance.bill.BillReadService;
import com.project.booking.dto.payment.BillPaymentResponse;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.server.ResponseStatusException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

@RestController
public class BillController {
    private final BillReadService service;
    public BillController(BillReadService service){this.service=service;}
    @GetMapping("/api/finance/bookings/{id}/bill")
    public BillReadService.View finance(@PathVariable Long id){return service.get(id);}
    @GetMapping("/api/passengers/bookings/{id}/bill")
    public BillReadService.View passenger(@PathVariable Long id,JwtAuthenticationToken jwt){
        service.authorize(id,user(jwt)); return service.get(id);
    }
    public record PaymentCommand(@NotNull UUID clientRequestId) {}
    @PostMapping("/api/finance/bookings/{id}/bill/payment")
    public ResponseEntity<BillPaymentResponse> payment(@PathVariable Long id,@Valid @RequestBody PaymentCommand command){
        return service.pay(id,command.clientRequestId());
    }
    @GetMapping(value={"/api/finance/bookings/{id}/bill/receipt","/api/passengers/bookings/{id}/bill/receipt"},produces=MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> receipt(@PathVariable Long id,JwtAuthenticationToken jwt,jakarta.servlet.http.HttpServletRequest request) {
        if(request.getRequestURI().startsWith("/api/passengers/")) service.authorize(id,user(jwt));
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=service-bill-"+id+".pdf")
            .header(HttpHeaders.CACHE_CONTROL,"no-store").contentType(MediaType.APPLICATION_PDF).body(service.receipt(id));
    }
    private UUID user(JwtAuthenticationToken jwt){
        if(jwt==null)throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        if(jwt.getAuthorities().stream().noneMatch(a->a.getAuthority().equals("ROLE_PASSENGER")))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        try{return UUID.fromString(String.valueOf(jwt.getTokenAttributes().get("userId")));}
        catch(Exception e){throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);}
    }
}
