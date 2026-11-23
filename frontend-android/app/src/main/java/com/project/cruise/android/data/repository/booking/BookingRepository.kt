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
        val rawMime = resolver.getType(uri) ?: "image/jpeg"
        val cacheDir = File(context.cacheDir, "booking_docs").apply { mkdirs() }

        val bytes = resolver.openInputStream(uri)?.use { it.readBytes() } ?: return null
        val maxBytes = 800 * 1024 // ~800 KB an toàn dưới mức 1 MB (1,048,576 bytes)

        // Nếu file đã nhỏ hơn giới hạn thì ghi thẳng ra cache
        if (bytes.size <= maxBytes) {
            val ext = android.webkit.MimeTypeMap.getSingleton()
                .getExtensionFromMimeType(rawMime)?.takeIf { it.isNotBlank() } ?: "jpg"
            val outFile = File.createTempFile("id_card_", ".$ext", cacheDir)
            outFile.writeBytes(bytes)
            return outFile to rawMime
        }

        // Nếu lớn hơn giới hạn: decode & nén JPEG cho đến khi <= maxBytes
        val bitmap = android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size) ?: return null
        val scaled = scaleBitmapDown(bitmap, maxSide = 1280)
        if (scaled !== bitmap) bitmap.recycle()

        val outFile = File.createTempFile("id_card_", ".jpg", cacheDir)
        var quality = 85
        var done = false

        while (!done) {
            val compressedBytes = java.io.ByteArrayOutputStream().use { baos ->
                scaled.compress(android.graphics.Bitmap.CompressFormat.JPEG, quality, baos)
                baos.toByteArray()
            }
            if (compressedBytes.size <= maxBytes || quality <= 35) {
                outFile.writeBytes(compressedBytes)
                done = true
            } else {
                quality -= 10
            }
        }

        scaled.recycle()
        return outFile to "image/jpeg"
    }

    private fun scaleBitmapDown(src: android.graphics.Bitmap, maxSide: Int): android.graphics.Bitmap {
        val w = src.width
        val h = src.height
        val longest = maxOf(w, h)
        if (longest <= maxSide) return src
        val ratio = maxSide.toFloat() / longest.toFloat()
        val newW = (w * ratio).toInt().coerceAtLeast(1)
        val newH = (h * ratio).toInt().coerceAtLeast(1)
        return android.graphics.Bitmap.createScaledBitmap(src, newW, newH, true)
    }
}