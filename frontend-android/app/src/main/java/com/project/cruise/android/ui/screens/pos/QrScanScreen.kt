package com.project.cruise.android.ui.screens.pos

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.project.cruise.android.data.local.pos.PosScanType
import com.project.cruise.android.data.repository.PosTransactionQueue
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

@Composable
fun QrScanScreen(
    role: PosRole,
    onBackClick: () -> Unit,
    onSaved: (String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val queue = remember { PosTransactionQueue(context) }
    val scope = rememberCoroutineScope()
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    val scanner = remember {
        BarcodeScanning.getClient(
            BarcodeScannerOptions.Builder().setBarcodeFormats(Barcode.FORMAT_QR_CODE).build()
        )
    }
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    var hasPermission by remember {
        mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
    }
    var isSaving by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        hasPermission = it
        if (!it) error = "Cần quyền camera để quét mã QR"
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                hasPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
    LaunchedEffect(Unit) { if (!hasPermission) permissionLauncher.launch(Manifest.permission.CAMERA) }
    DisposableEffect(Unit) {
        onDispose {
            if (cameraProviderFuture.isDone) cameraProviderFuture.get().unbindAll()
            scanner.close()
            cameraExecutor.shutdown()
        }
    }

    PosTheme {
    Column(Modifier.fillMaxSize().background(PosBackground).safeDrawingPadding().verticalScroll(rememberScrollState()).padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        TextButton(onClick = onBackClick, modifier = Modifier.align(Alignment.Start), enabled = !isSaving) {
            Text("← Quay lại POS")
        }
        Spacer(Modifier.height(16.dp))
        Text(
            if (role == PosRole.ONBOARD) "Quét QR vé khách" else "Quét QR booking",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            if (role == PosRole.ONBOARD) {
                "Đưa QR trên vé của hành khách vào giữa khung hình."
            } else {
                "Đưa mã QR của vé hoặc booking vào giữa khung hình."
            },
            modifier = Modifier.padding(top = 8.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Box(
            Modifier.padding(top = 24.dp).fillMaxWidth().height(360.dp).clip(RoundedCornerShape(22.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (hasPermission) {
                AndroidView(
                    factory = { previewContext ->
                        PreviewView(previewContext).also { previewView ->
                            cameraProviderFuture.addListener({
                                val provider = cameraProviderFuture.get()
                                val preview = Preview.Builder().build().also { it.surfaceProvider = previewView.surfaceProvider }
                                val analysis = ImageAnalysis.Builder()
                                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build()

                                analysis.setAnalyzer(cameraExecutor) { imageProxy ->
                                    processImageProxy(
                                        imageProxy = imageProxy,
                                        isSaving = isSaving,
                                        scanner = scanner,
                                        onSavingChanged = { isSaving = it },
                                        onSuccess = onSaved,
                                        onError = { error = it },
                                        scope = scope,
                                        queue = queue,
                                        role = role
                                    )
                                }

                                provider.unbindAll()
                                provider.bindToLifecycle(lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, preview, analysis)
                            }, ContextCompat.getMainExecutor(previewContext))
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            } else Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Cho phép camera để đọc mã booking", textAlign = TextAlign.Center)
                TextButton(onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) }) { Text("Cho phép camera") }
                TextButton(onClick = {
                    context.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:${context.packageName}")))
                }) { Text("Mở cài đặt ứng dụng") }
            }
            if (isSaving) CircularProgressIndicator()
        }
        error?.let { Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 12.dp)) }
        Spacer(Modifier.height(24.dp))
        Text(
            if (role == PosRole.ONBOARD) {
                "Quét được mã chưa có nghĩa khách đã được xác nhận lên tàu."
            } else {
                "Đưa QR trong email vào giữa khung hình. Gửi mã thành công chưa phải hoàn tất check-in."
            },
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

}

@androidx.annotation.OptIn(ExperimentalGetImage::class)
@OptIn(ExperimentalGetImage::class)
private fun processImageProxy(
    imageProxy: androidx.camera.core.ImageProxy,
    isSaving: Boolean,
    scanner: com.google.mlkit.vision.barcode.BarcodeScanner,
    onSavingChanged: (Boolean) -> Unit,
    onSuccess: (String) -> Unit,
    onError: (String) -> Unit,
    scope: kotlinx.coroutines.CoroutineScope,
    queue: PosTransactionQueue,
    role: PosRole
) {
    val mediaImage = imageProxy.image
    if (mediaImage == null || isSaving) {
        imageProxy.close()
    } else {
        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
        scanner.process(image)
            .addOnSuccessListener { codes ->
                val value = codes.firstOrNull()?.rawValue?.trim()
                if (!value.isNullOrBlank() && !isSaving) {
                    onSavingChanged(true)
                    scope.launch {
                        runCatching {
                            queue.enqueue(
                                scanType = PosScanType.QR,
                                scannedValue = value,
                                operatorRole = role.apiRole,
                                operation = role.scanOperation
                            )
                        }
                            .onSuccess { onSuccess(it) }
                            .onFailure {
                                onError("Không thể lưu giao dịch trên thiết bị")
                                onSavingChanged(false)
                            }
                    }
                }
            }
            .addOnFailureListener { onError("Không thể đọc mã QR") }
            .addOnCompleteListener { imageProxy.close() }
    }
}
