package com.project.cruise.android.ui.screens.pos

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PosRoleTest {
    @Test
    fun `nhan role co va khong co tien to ROLE`() {
        assertEquals(PosRole.FINANCE, PosRole.fromApiRole("FINANCE"))
        assertEquals(PosRole.CONVENIENCE, PosRole.fromApiRole("ROLE_CONVENIENCE"))
        assertEquals(PosRole.ONBOARD, PosRole.fromApiRole(" role_onboard "))
        assertEquals(PosRole.SHORE, PosRole.fromApiRole("shore"))
    }

    @Test
    fun `tu choi role khong thuoc may POS`() {
        assertNull(PosRole.fromApiRole("PASSENGER"))
        assertNull(PosRole.fromApiRole("ADMIN"))
        assertNull(PosRole.fromApiRole(null))
    }

    @Test
    fun `moi role gan dung nghiep vu offline`() {
        assertEquals("CHECK_IN", PosRole.FINANCE.scanOperation)
        assertEquals("CONVENIENCE_USAGE", PosRole.CONVENIENCE.scanOperation)
        assertEquals("ONBOARD_PARTICIPATION", PosRole.ONBOARD.scanOperation)
        assertEquals("SHORE_PARTICIPATION", PosRole.SHORE.scanOperation)
    }
}
