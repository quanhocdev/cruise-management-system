package com.project.booking.controller.finance;

import com.project.booking.service.account.SpendingLimitService;
import com.project.booking.service.account.LedgerHistoryService;
import com.project.booking.service.command.IdempotentCommandService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/finance/bookings")
public class SpendingLimitController {
    private final SpendingLimitService limits;
    private final IdempotentCommandService commands;
    private final LedgerHistoryService history;

    public SpendingLimitController(SpendingLimitService limits, IdempotentCommandService commands, LedgerHistoryService history) {
        this.limits = limits;
        this.commands = commands;
        this.history = history;
    }

    public record Request(@NotNull @DecimalMin("0") @Digits(integer=17, fraction=2) BigDecimal creditLimit,
                          @NotBlank @Size(max=1000) String reason, @NotNull UUID clientRequestId) {}
    private record Command(Long bookingId, BigDecimal creditLimit, String reason, UUID clientRequestId) {}

    @GetMapping("/spending-account")
    public SpendingLimitService.Account find(@RequestParam String bookingCode) {
        return limits.find(bookingCode);
    }

    public record ImportRequest(@NotNull UUID clientRequestId) {}
    private record ImportCommand(Long bookingId, UUID clientRequestId) {}

    @PostMapping("/{bookingId}/ledger-history")
    public ResponseEntity<LedgerHistoryService.Result> importHistory(@PathVariable Long bookingId,
            @Valid @RequestBody ImportRequest request) {
        return commands.execute("LEDGER_HISTORY", request.clientRequestId(), new ImportCommand(bookingId,request.clientRequestId()),
                LedgerHistoryService.Result.class, () -> history.importMissing(bookingId));
    }

    @PutMapping("/{bookingId}/spending-limit")
    public ResponseEntity<SpendingLimitService.Result> set(@PathVariable Long bookingId,
            @Valid @RequestBody Request request, JwtAuthenticationToken principal) {
        var payload = new Command(bookingId, request.creditLimit(), request.reason(), request.clientRequestId());
        String actor = principal.getTokenAttributes().get("userId") == null
                ? "subject:" + principal.getName() : "user:" + principal.getTokenAttributes().get("userId");
        return commands.execute("SPENDING_LIMIT", request.clientRequestId(), payload, SpendingLimitService.Result.class,
                () -> limits.setLimit(bookingId, request.creditLimit(), request.reason(), request.clientRequestId(), actor));
    }
}
