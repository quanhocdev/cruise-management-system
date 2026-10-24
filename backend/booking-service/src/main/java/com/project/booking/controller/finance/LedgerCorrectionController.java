package com.project.booking.controller.finance;

import com.project.booking.service.account.LedgerCorrectionService;
import com.project.booking.service.command.IdempotentCommandService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class LedgerCorrectionController {
    private final LedgerCorrectionService ledger;
    private final IdempotentCommandService commands;
    public LedgerCorrectionController(LedgerCorrectionService ledger, IdempotentCommandService commands) {
        this.ledger=ledger; this.commands=commands;
    }
    public record Request(@NotNull @Pattern(regexp="VOID|REFUND") String type,
                          @DecimalMin("0") @Digits(integer=17,fraction=2) BigDecimal amount,
                          @NotBlank @Size(max=1000) String reason, @NotNull UUID clientRequestId) {}
    private record Command(long entryId, String type, BigDecimal amount, String reason, UUID clientRequestId) {}

    @GetMapping({"/api/finance/bookings/ledger", "/api/convenience/product-usages/ledger"})
    public LedgerCorrectionService.Page list(@RequestParam String bookingCode,
            @RequestParam(defaultValue="0") int page, @RequestParam(defaultValue="20") int size) {
        return ledger.list(bookingCode,page,size);
    }

    @PostMapping({"/api/finance/bookings/ledger/{entryId}/corrections", "/api/convenience/product-usages/ledger/{entryId}/corrections"})
    public ResponseEntity<LedgerCorrectionService.Result> correct(@PathVariable long entryId, @Valid @RequestBody Request request) {
        ledger.authorize(entryId, request.type());
        return commands.execute("LEDGER_CORRECTION",request.clientRequestId(),
                new Command(entryId,request.type(),request.amount(),request.reason(),request.clientRequestId()),
                LedgerCorrectionService.Result.class, () -> ledger.correct(entryId,request.type(),request.amount(),request.reason(),request.clientRequestId()));
    }
}
