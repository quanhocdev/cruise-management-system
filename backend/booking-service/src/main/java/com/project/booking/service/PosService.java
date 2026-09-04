package com.project.booking.service;

import com.project.booking.dto.*;
import com.project.booking.exception.BookingException;
import com.project.booking.model.PosTerminal;
import com.project.booking.model.PosTransaction;
import com.project.booking.repository.PosTerminalRepository;
import com.project.booking.repository.PosTransactionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;

@Service
public class PosService {
    private final PosTerminalRepository terminalRepository;
    private final PosTransactionRepository transactionRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom secureRandom = new SecureRandom();

    public PosService(PosTerminalRepository terminalRepository,
                      PosTransactionRepository transactionRepository,
                      PasswordEncoder passwordEncoder) {
        this.terminalRepository = terminalRepository;
        this.transactionRepository = transactionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public PosTerminalRegistrationResponse register(RegisterPosTerminalRequest request) {
        String code = request.code().trim().toUpperCase();
        if (terminalRepository.existsByCodeIgnoreCase(code))
            throw new BookingException(HttpStatus.CONFLICT, "POS terminal code already exists");
        byte[] secretBytes = new byte[32];
        secureRandom.nextBytes(secretBytes);
        String secret = Base64.getUrlEncoder().withoutPadding().encodeToString(secretBytes);
        PosTerminal terminal = new PosTerminal();
        terminal.setCode(code);
        terminal.setName(request.name().trim());
        terminal.setSecretHash(passwordEncoder.encode(secret));
        terminal.setActive(true);
        terminal.setCreatedAt(Instant.now());
        PosTerminal saved = terminalRepository.save(terminal);
        return new PosTerminalRegistrationResponse(saved.getId(), saved.getCode(), saved.getName(), secret);
    }

    @Transactional
    public PosSyncResponse sync(String terminalCode, String posKey, PosSyncRequest request) {
        PosTerminal terminal = authenticate(terminalCode, posKey);
        return transactionRepository.findByLocalId(request.localId())
            .map(existing -> response(existing, true))
            .orElseGet(() -> {
                PosTransaction transaction = new PosTransaction();
                transaction.setLocalId(request.localId());
                transaction.setTerminalCode(terminal.getCode());
                transaction.setScanType(request.scanType());
                transaction.setScannedValue(request.scannedValue().trim());
                transaction.setDeviceCreatedAt(request.createdAt());
                transaction.setReceivedAt(Instant.now());
                return response(transactionRepository.save(transaction), false);
            });
    }

    private PosTerminal authenticate(String terminalCode, String posKey) {
        if (terminalCode == null || terminalCode.isBlank() || posKey == null || posKey.isBlank())
            throw new BookingException(HttpStatus.UNAUTHORIZED, "POS credentials are required");
        PosTerminal terminal = terminalRepository.findByCodeIgnoreCase(terminalCode.trim())
            .orElseThrow(() -> new BookingException(HttpStatus.UNAUTHORIZED, "Invalid POS credentials"));
        if (!terminal.isActive() || !passwordEncoder.matches(posKey, terminal.getSecretHash()))
            throw new BookingException(HttpStatus.UNAUTHORIZED, "Invalid POS credentials");
        return terminal;
    }

    private PosSyncResponse response(PosTransaction transaction, boolean duplicate) {
        return new PosSyncResponse(transaction.getId(), transaction.getLocalId(), "SYNCED",
            transaction.getReceivedAt(), duplicate);
    }
}
