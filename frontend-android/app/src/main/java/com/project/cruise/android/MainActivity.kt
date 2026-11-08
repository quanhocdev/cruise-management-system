package com.project.cruise.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.project.cruise.android.data.repository.ChatRepositoryProvider
import com.project.cruise.android.navigation.NavGraph
import com.project.cruise.android.ui.theme.CruiseManagementTheme

class MainActivity : ComponentActivity() {

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