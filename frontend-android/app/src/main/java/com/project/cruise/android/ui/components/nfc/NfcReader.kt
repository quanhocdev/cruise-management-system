package com.project.cruise.android.ui.components.nfc

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.media.ToneGenerator
import android.nfc.NfcAdapter
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import com.project.cruise.android.ui.components.playTone

@Composable
fun rememberNfcAdapter(): NfcAdapter? {
    val context = LocalContext.current
    return remember(context) { NfcAdapter.getDefaultAdapter(context) }
}

/**
 * Bật reader mode khi composable đang hiển thị, tắt khi rời đi.
 * Khi đọc được thẻ: phát tiếng bíp rồi gọi [onUidRead] với UID dạng HEX in hoa.
 *
 * [enabled] = false thì bỏ qua thẻ quét được (ví dụ đang gọi API xác thực).
 */
@Composable
fun NfcReaderEffect(
    adapter: NfcAdapter?,
    enabled: Boolean = true,
    onUidRead: (String) -> Unit
) {
    val context = LocalContext.current
    val activity = remember(context) { context.findActivity() }

    val currentEnabled by rememberUpdatedState(enabled)
    val currentOnUidRead by rememberUpdatedState(onUidRead)

    DisposableEffect(activity, adapter) {
        if (activity != null && adapter != null && adapter.isEnabled) {
            adapter.enableReaderMode(
                activity,
                { tag ->
                    if (!currentEnabled) {
                        return@enableReaderMode
                    }

                    playTone(ToneGenerator.TONE_PROP_BEEP, 150)
                    currentOnUidRead(tag.id.toUidString())
                },
                NfcAdapter.FLAG_READER_NFC_A or
                        NfcAdapter.FLAG_READER_NFC_B or
                        NfcAdapter.FLAG_READER_NFC_F or
                        NfcAdapter.FLAG_READER_NFC_V or
                        NfcAdapter.FLAG_READER_NO_PLATFORM_SOUNDS,
                null
            )
        }

        onDispose {
            if (activity != null && adapter != null) {
                adapter.disableReaderMode(activity)
            }
        }
    }
}

private fun ByteArray.toUidString(): String =
    joinToString("") { byte -> "%02X".format(byte.toInt() and 0xFF) }

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}