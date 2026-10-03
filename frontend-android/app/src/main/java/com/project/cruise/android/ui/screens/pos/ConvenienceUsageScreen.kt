package com.project.cruise.android.ui.screens.pos

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.project.cruise.android.data.dto.convenience.ProductTourResponse
import com.project.cruise.android.data.dto.convenience.ServiceTourResponse
import com.project.cruise.android.viewmodel.ConvenienceUsageViewModel
import android.media.AudioManager
import android.media.ToneGenerator

@Composable
fun ConvenienceUsageScreen(
    bookingPassengerId: Long,
    passengerName: String,
    bookingId: Long,
    tourId: String,
    tourPackageId: String,
    nfcCardUid: String,
    onBackClick: () -> Unit,
    viewModel: ConvenienceUsageViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.successMessage) {
        if (uiState.successMessage != null) {
            val toneGenerator = ToneGenerator(
                AudioManager.STREAM_NOTIFICATION, 100 )
                toneGenerator.startTone( ToneGenerator.TONE_PROP_ACK, 200 )
                toneGenerator.release()
                kotlinx.coroutines.delay(2000)
                viewModel.clearSuccess() } }

    var selectedType by remember {
        mutableStateOf(ConvenienceUsageType.PRODUCT)
    }

    var selectedProduct by remember {
        mutableStateOf<ProductTourResponse?>(null)
    }

    var selectedService by remember {
        mutableStateOf<ServiceTourResponse?>(null)
    }

    var quantity by remember {
        mutableIntStateOf(1)
    }

    LaunchedEffect(tourId) {
        viewModel.loadConvenienceTours(tourId)
    }

    LaunchedEffect(selectedType) {
        selectedProduct = null
        selectedService = null
        quantity = 1
        viewModel.clearError()
    }

    val isProduct =
        selectedType == ConvenienceUsageType.PRODUCT

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {

        TextButton(
            onClick = onBackClick,
            enabled = !uiState.isSubmitting
        ) {
            Text("← Quay lại")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Tiện ích hành khách",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = passengerName.ifBlank {
                        "Không có tên hành khách"
                    },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Booking #$bookingId",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "Booking Passenger #$bookingPassengerId",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Loại tiện ích",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            FilterChip(
                selected = isProduct,
                onClick = {
                    selectedType = ConvenienceUsageType.PRODUCT
                },
                label = {
                    Text("Sản phẩm")
                },
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            FilterChip(
                selected = !isProduct,
                onClick = {
                    selectedType = ConvenienceUsageType.SERVICE
                },
                label = {
                    Text("Dịch vụ")
                },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (isProduct) {
                "Chọn sản phẩm"
            } else {
                "Chọn dịch vụ"
            },
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (uiState.isLoading) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }

        } else {

            if (isProduct) {

                if (uiState.products.isEmpty()) {

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        Text(
                            text = "Tour này không có sản phẩm tiện ích.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                } else {

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(
                            items = uiState.products,
                            key = { it.id }
                        ) { product ->

                            ConvenienceProductCard(
                                product = product,
                                selected =
                                    selectedProduct?.id == product.id,
                                onClick = {
                                    selectedProduct = product
                                }
                            )
                        }
                    }
                }

            } else {

                if (uiState.services.isEmpty()) {

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        Text(
                            text = "Tour này không có dịch vụ tiện ích.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                } else {

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(
                            items = uiState.services,
                            key = { it.id }
                        ) { service ->

                            ConvenienceServiceCard(
                                service = service,
                                selected =
                                    selectedService?.id == service.id,
                                onClick = {
                                    selectedService = service
                                }
                            )
                        }
                    }
                }
            }
        }

        if (
            isProduct &&
            selectedProduct != null
        ) {

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Số lượng",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {

                OutlinedButton(
                    onClick = {
                        if (quantity > 1) {
                            quantity--
                        }
                    },
                    enabled = !uiState.isSubmitting
                ) {
                    Text("−")
                }

                Text(
                    text = quantity.toString(),
                    modifier = Modifier.padding(
                        horizontal = 24.dp,
                        vertical = 10.dp
                    ),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                OutlinedButton(
                    onClick = {
                        quantity++
                    },
                    enabled = !uiState.isSubmitting
                ) {
                    Text("+")
                }
            }
        }

        uiState.errorMessage?.let { message ->

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = message,
                color = MaterialTheme.colorScheme.error
            )
        }

        uiState.successMessage?.let { message ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {

                if (isProduct) {

                    val product = selectedProduct
                        ?: return@Button

                    viewModel.createProductUsage(
                        nfcCardUid = nfcCardUid,
                        productTourId = product.id,
                        quantity = quantity
                    )

                } else {

                    val service = selectedService
                        ?: return@Button

                    viewModel.createServiceUsage(
                        nfcCardUid = nfcCardUid,
                        serviceTourId = service.id
                    )
                }
            },
            enabled =
                if (isProduct) {
                    selectedProduct != null
                } else {
                    selectedService != null
                } &&
                        !uiState.isLoading &&
                        !uiState.isSubmitting,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (uiState.isSubmitting) {

                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )

            } else {
                Text("Xác nhận")
            }
        }
    }
}

@Composable
private fun ConvenienceProductCard(
    product: ProductTourResponse,
    selected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = if (selected) {
            CardDefaults.cardColors(
                containerColor =
                    MaterialTheme.colorScheme.primaryContainer
            )
        } else {
            CardDefaults.cardColors()
        }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = product.productName
                    ?: "Không có tên sản phẩm",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            product.productDescription
                ?.takeIf { it.isNotBlank() }
                ?.let { description ->

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

            product.quantity?.let { quantity ->

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Số lượng cấu hình: $quantity",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ConvenienceServiceCard(
    service: ServiceTourResponse,
    selected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = if (selected) {
            CardDefaults.cardColors(
                containerColor =
                    MaterialTheme.colorScheme.primaryContainer
            )
        } else {
            CardDefaults.cardColors()
        }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = service.serviceName
                    ?: "Không có tên dịch vụ",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            service.serviceDescription
                ?.takeIf { it.isNotBlank() }
                ?.let { description ->

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

            service.servicePrice
                ?.takeIf { it.isNotBlank() }
                ?.let { price ->

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = price,
                        fontWeight = FontWeight.SemiBold
                    )
                }

            service.durationMinutes?.let { duration ->

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Thời lượng: $duration phút",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

enum class ConvenienceUsageType {
    PRODUCT,
    SERVICE
}