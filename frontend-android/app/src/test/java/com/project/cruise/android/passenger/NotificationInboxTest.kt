package com.project.cruise.android.passenger

import com.project.cruise.android.data.dto.passenger.*
import com.project.cruise.android.data.repository.NotificationSource
import com.project.cruise.android.viewmodel.passenger.NotificationInbox
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.CancellationException
import org.junit.Test
import org.junit.Assert.*

class NotificationInboxTest {
    private fun entry(id: Long = 1) = PassengerNotification(id, "BOOKING_CONFIRMED", "Title", "Body", "BOOKING", 12, null, "2026-09-07T00:00:00Z")
    private class Fake(var items: List<PassengerNotification>) : NotificationSource {
        var fail = false
        var reads = 0
        override suspend fun list(): List<PassengerNotification> { check(!fail); return items }
        override suspend fun read(id: Long): PassengerNotification {
            check(!fail); reads++
            val updated = items.first { it.id == id }.copy(readAt = "2026-09-07T00:01:00Z")
            items = items.map { if (it.id == id) updated else it }; return updated
        }
        override suspend fun readAll() { check(!fail); items = items.map { it.copy(readAt = "2026-09-07T00:01:00Z") } }
    }
    @Test fun readsAndCountsServerConfirmedResults() = runBlocking {
        val fake = Fake(listOf(entry(), entry(2)))
        val inbox = NotificationInbox(fake); inbox.refresh()
        assertEquals(2, inbox.state.value.unreadCount)
        inbox.read(1); assertEquals(1, inbox.state.value.unreadCount)
        inbox.read(1); assertEquals(1, fake.reads)
        inbox.readAll(); assertEquals(0, inbox.state.value.unreadCount)
    }
    @Test fun failedMutationDoesNotPretendReadSucceeded() = runBlocking {
        val fake = Fake(listOf(entry())); val inbox = NotificationInbox(fake)
        inbox.refresh(); fake.fail = true; inbox.read(1)
        assertEquals(1, inbox.state.value.unreadCount)
        assertNotNull(inbox.state.value.error); assertFalse(inbox.state.value.busy)
        inbox.readAll(); assertEquals(1, inbox.state.value.unreadCount)
    }
    @Test fun failedLoadIsDifferentFromEmptyInboxAndCanRetry() = runBlocking {
        val fake = Fake(emptyList()); fake.fail = true
        val inbox = NotificationInbox(fake); inbox.refresh()
        assertFalse(inbox.state.value.loaded); assertNotNull(inbox.state.value.error)
        fake.fail = false; inbox.refresh()
        assertTrue(inbox.state.value.loaded); assertNull(inbox.state.value.error)
    }
    @Test fun unknownOrInvalidReferencesNeverNavigate() {
        assertEquals(12L, entry().bookingId())
        assertNull(entry().copy(referenceType = "PAYMENT").bookingId())
        assertNull(entry().copy(referenceType = "ITINERARY").bookingId())
        assertNull(entry().copy(referenceId = -1).bookingId())
        assertNull(entry().copy(referenceId = null).bookingId())
    }
    @Test fun missingIdsCannotTriggerWrites() = runBlocking {
        val fake = Fake(listOf(entry())); val inbox = NotificationInbox(fake)
        inbox.refresh(); inbox.read(99); assertEquals(0, fake.reads)
    }
    @Test fun cancellationPropagatesAndUnlocksState() = runBlocking {
        val source = object : NotificationSource {
            override suspend fun list(): List<PassengerNotification> = throw CancellationException()
            override suspend fun read(id: Long): PassengerNotification = error("unused")
            override suspend fun readAll() = Unit
        }
        val inbox = NotificationInbox(source)
        try { inbox.refresh(); fail("Expected cancellation") } catch (_: CancellationException) { }
        assertFalse(inbox.state.value.busy); assertNull(inbox.state.value.error)
    }
}
