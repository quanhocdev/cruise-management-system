package com.project.cruise.android.data.repository.booking

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import com.project.cruise.android.data.dto.booking.BookingResponse
import com.project.cruise.android.data.dto.booking.CreateBookingRequestDto
import com.project.cruise.android.data.network.api.BookingApiService
import java.io.File
import java.io.FileOutputStream

class BookingRepository(
    private val apiService: BookingApiService,
    private val appContext: Context? = null,
) {
    suspend fun getPayment(id: Long) = apiService.getBookingPayment(id)

    suspend fun getQr(id: Long): ByteArray = apiService.getBookingQr(id).use { it.bytes() }

    suspend fun getMyBookings(): List<BookingResponse> = apiService.getMyBookings()

    suspend fun getBookingById(id: Long, privileged: Boolean = false): BookingResponse =
        apiService.getBookingById(id, privileged)

    suspend fun createBooking(request: CreateBookingRequestDto): BookingResponse {
        val parts = request.toMultipartParts { uri ->
            resolveImageFile(uri)
        }
        return apiService.createPassengerBooking(parts)
    }

    private fun resolveImageFile(uri: Uri): Pair<File, String>? {
        val context = appContext ?: return null
        val resolver = context.contentResolver
        val mimeType = resolver.getType(uri) ?: "image/jpeg"
        val ext = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType) ?: "jpg"
        val cacheDir = File(context.cacheDir, "booking_docs").apply { mkdirs() }
        val tempFile = File.createTempFile("id_card_", ".$ext", cacheDir)

        resolver.openInputStream(uri)?.use { input ->
            FileOutputStream(tempFile).use { output ->
                input.copyTo(output)
            }
        } ?: return null

        return tempFile to mimeType
    }
}