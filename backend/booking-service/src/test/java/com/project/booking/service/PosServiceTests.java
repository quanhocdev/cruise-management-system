package com.project.booking.service;

import com.project.booking.dto.PosSyncRequest;
import com.project.booking.dto.RegisterPosTerminalRequest;
import com.project.booking.model.PosTerminal;
import com.project.booking.model.PosTransaction;
import com.project.booking.repository.PosTerminalRepository;
import com.project.booking.repository.PosTransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PosServiceTests {
    private PosTerminalRepository terminalRepository;
    private PosTransactionRepository transactionRepository;
    private PosService service;

    @BeforeEach
    void setUp() {
        terminalRepository = mock(PosTerminalRepository.class);
        transactionRepository = mock(PosTransactionRepository.class);
        service = new PosService(terminalRepository, transactionRepository, new BCryptPasswordEncoder());
    }

    @Test
    void registerReturnsSecretOnlyAfterSavingHashedValue() {
        when(terminalRepository.existsByCodeIgnoreCase("POS-001")).thenReturn(false);
        when(terminalRepository.save(any())).thenAnswer(invocation -> {
            PosTerminal terminal = invocation.getArgument(0);
            terminal.setId(1L);
            return terminal;
        });

        var response = service.register(new RegisterPosTerminalRequest("pos-001", "Quầy lễ tân"));

        assertEquals("POS-001", response.code());
        assertFalse(response.secret().isBlank());
        verify(terminalRepository).save(argThat(terminal ->
            !terminal.getSecretHash().equals(response.secret()) && terminal.isActive()));
    }

    @Test
    void retryWithSameLocalIdReturnsExistingTransaction() {
        String secret = "device-secret";
        PosTerminal terminal = terminal("POS-001", secret);
        PosTransaction existing = new PosTransaction();
        existing.setId(8L);
        existing.setLocalId(UUID.randomUUID().toString());
        existing.setReceivedAt(Instant.now());
        when(terminalRepository.findByCodeIgnoreCase("POS-001")).thenReturn(Optional.of(terminal));
        when(transactionRepository.findByLocalId(existing.getLocalId())).thenReturn(Optional.of(existing));

        var response = service.sync("POS-001", secret,
            new PosSyncRequest(existing.getLocalId(), "QR", "BOOKING:CR1", Instant.now()));

        assertTrue(response.duplicate());
        assertEquals(8L, response.serverId());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void identityQrIsNotStoredInPlaintextScanLog() {
        when(terminalRepository.findByCodeIgnoreCase("POS-001")).thenReturn(Optional.of(terminal("POS-001", "device-secret")));
        when(transactionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        String token = "POS:" + "A".repeat(43);
        service.sync("POS-001", "device-secret", new PosSyncRequest(UUID.randomUUID().toString(), "QR", token, Instant.now()));
        verify(transactionRepository).save(argThat(t -> t.getScannedValue().startsWith("SHA256:") && !t.getScannedValue().contains(token)));
    }

    private PosTerminal terminal(String code, String secret) {
        PosTerminal terminal = new PosTerminal();
        terminal.setCode(code);
        terminal.setActive(true);
        terminal.setSecretHash(new BCryptPasswordEncoder().encode(secret));
        return terminal;
    }
}
