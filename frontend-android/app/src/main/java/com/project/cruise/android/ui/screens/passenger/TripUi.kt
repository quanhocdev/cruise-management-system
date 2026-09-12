//package com.project.cruise.android.ui.screens.passenger
//
//import androidx.compose.foundation.layout.*
//import androidx.compose.material3.*
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import com.project.cruise.android.ui.theme.*
//
///** Scope the passenger dialog palette without changing POS or other role screens. */
//@Composable
//internal fun TripSurface(content: @Composable () -> Unit) {
//    OceanTheme { Surface(Modifier.fillMaxSize(), color = OceanMist, content = content) }
//}
//
//@Composable
//internal fun TripBackButton(label: String, onClick: () -> Unit) {
//    FilledTonalButton(onClick = onClick, modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp)) {
//        Text(label, style = MaterialTheme.typography.titleMedium)
//    }
//}
