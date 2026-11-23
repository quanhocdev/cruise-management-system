package com.project.cruise.android

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import com.project.cruise.android.data.repository.chat.ChatRepositoryProvider
import com.project.cruise.android.navigation.NavGraph
import com.project.cruise.android.ui.theme.CruiseManagementTheme

class MainActivity : ComponentActivity() {

    private var onImagePickedCallback: ((Uri) -> Unit)? = null

    private val pickIdCardImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        val callback = onImagePickedCallback
        onImagePickedCallback = null
        if (uri != null) {
            callback?.invoke(uri)
        }
    }

    fun launchPickIdCardImage(onResult: (Uri) -> Unit) {
        onImagePickedCallback = onResult
        pickIdCardImageLauncher.launch("image/*")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        // Khởi tạo Chat Repository dùng chung
        ChatRepositoryProvider.initialize(applicationContext)

        setContent {
            CruiseManagementTheme {
                NavGraph()
            }
        }
    }
}