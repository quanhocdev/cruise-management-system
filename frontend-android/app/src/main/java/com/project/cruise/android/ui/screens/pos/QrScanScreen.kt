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
    onSaved: (String) -> Unit,
    onManualClick: (() -> Unit)? = null
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
    var camera by remember { mutableStateOf<androidx.camera.core.Camera?>(null) }
    var torch by remember { mutableStateOf(false) }
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
    Column(Modifier.fillMaxSize().background(PosBackground).safeDrawingPadding().verticalScroll(rememberScrollState()).padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(18.dp)) {
        PosTopBar("Quét QR", role, onBackClick, !isSaving)
        Text(when(role) {
            PosRole.ONBOARD -> "Đưa vé khách vào khung"
            PosRole.SHORE -> "Đưa QR tham quan vào khung"
            else -> "Đưa QR booking vào khung"
        }, style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center)
        Text("Giữ mã rõ nét. Thiết bị sẽ tự đọc khi nhận diện được QR.",
            color = PosMuted, textAlign = TextAlign.Center, style = MaterialTheme.typography.bodyMedium)
        Box(
            Modifier.fillMaxWidth().aspectRatio(1f).clip(RoundedCornerShape(26.dp)).background(PosInk),
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
                                runCatching {
                                    camera = provider.bindToLifecycle(lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, preview, analysis)
                                }.onFailure { error = "Không mở được camera. Hãy nhập mã thủ công hoặc thử lại." }
                            }, ContextCompat.getMainExecutor(previewContext))
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            } else Column(Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                PosGlyph(PosSymbol.QR, androidx.compose.ui.graphics.Color.White, Modifier.size(56.dp))
                Text("Cho phép camera để đọc QR", color = androidx.compose.ui.graphics.Color.White, textAlign = TextAlign.Center)
                TextButton(onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) }) { Text("Cho phép camera") }
                TextButton(onClick = {
                    context.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:${context.packageName}")))
                }) { Text("Mở cài đặt ứng dụng") }
            }
            if (hasPermission) {
                androidx.compose.foundation.Canvas(Modifier.fillMaxSize().padding(38.dp)) {
                    val corner = size.width*.17f
                    val color = androidx.compose.ui.graphics.Color(0xFF9DE1DE)
                    val stroke = 4.dp.toPx()
                    fun edge(a: androidx.compose.ui.geometry.Offset, b: androidx.compose.ui.geometry.Offset) =
                        drawLine(color,a,b,stroke,androidx.compose.ui.graphics.StrokeCap.Round)
                    edge(androidx.compose.ui.geometry.Offset.Zero, androidx.compose.ui.geometry.Offset(corner,0f))
                    edge(androidx.compose.ui.geometry.Offset.Zero, androidx.compose.ui.geometry.Offset(0f,corner))
                    edge(androidx.compose.ui.geometry.Offset(size.width,0f), androidx.compose.ui.geometry.Offset(size.width-corner,0f))
                    edge(androidx.compose.ui.geometry.Offset(size.width,0f), androidx.compose.ui.geometry.Offset(size.width,corner))
                    edge(androidx.compose.ui.geometry.Offset(0f,size.height), androidx.compose.ui.geometry.Offset(corner,size.height))
                    edge(androidx.compose.ui.geometry.Offset(0f,size.height), androidx.compose.ui.geometry.Offset(0f,size.height-corner))
                    edge(androidx.compose.ui.geometry.Offset(size.width,size.height), androidx.compose.ui.geometry.Offset(size.width-corner,size.height))
                    edge(androidx.compose.ui.geometry.Offset(size.width,size.height), androidx.compose.ui.geometry.Offset(size.width,size.height-corner))
                }
            }
            if (isSaving) CircularProgressIndicator(color = androidx.compose.ui.graphics.Color.White)
        }
        if(camera?.cameraInfo?.hasFlashUnit() == true) {
            OutlinedButton(onClick = {
                torch = !torch
                camera?.cameraControl?.enableTorch(torch)
            }, enabled = !isSaving) { Text(if(torch) "Tắt đèn hỗ trợ" else "Bật đèn hỗ trợ") }
        }
        onManualClick?.let { manual ->
            OutlinedButton(onClick = manual, enabled = !isSaving,
                modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp), shape = RoundedCornerShape(16.dp)) {
                PosGlyph(PosSymbol.KEYBOARD, role.accent())
                Spacer(Modifier.width(10.dp))
                Text("Nhập mã thay thế")
            }
        }
        error?.let { PosNotice(it, warning = true) }
        PosNotice(if(role == PosRole.FINANCE) "Gửi mã thành công chưa phải hoàn tất check-in."
            else "Đọc được QR chưa xác nhận hành khách đủ điều kiện tham gia.")
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
