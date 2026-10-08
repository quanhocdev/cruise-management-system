package com.project.cruise.android.ui.components

import android.media.AudioManager
import android.media.ToneGenerator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import kotlinx.coroutines.delay

/** Phát một tiếng bíp ngắn, không cần file âm thanh trong res/raw. */
fun playTone(toneType: Int, durationMs: Int, volume: Int = 100) {
    val toneGenerator = ToneGenerator(AudioManager.STREAM_NOTIFICATION, volume)
    toneGenerator.startTone(toneType, durationMs)
    toneGenerator.release()
}

/**
 * Khi [successMessage] khác null: phát tiếng ACK, chờ 2 giây rồi gọi [onFinished]
 * (thường dùng để clear thông báo thành công trong ViewModel).
 */
@Composable
fun SuccessSoundEffect(
    successMessage: String?,
    onFinished: () -> Unit
) {
    val currentOnFinished by rememberUpdatedState(onFinished)

    LaunchedEffect(successMessage) {
        if (successMessage != null) {
            playTone(ToneGenerator.TONE_PROP_ACK, 200)
            delay(2000)
            currentOnFinished()
        }
    }
}