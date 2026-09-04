package com.project.booking.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class QrCodeServiceTests {
    @Test void generatesPngQrCode() {
        byte[] result = new QrCodeService().png("CR00000001");
        assertTrue(result.length > 100);
        assertArrayEquals(new byte[] {(byte) 0x89, 0x50, 0x4e, 0x47},
            new byte[] {result[0], result[1], result[2], result[3]});
    }
}
