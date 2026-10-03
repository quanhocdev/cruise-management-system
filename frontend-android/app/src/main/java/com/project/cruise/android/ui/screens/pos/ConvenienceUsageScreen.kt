package com.project.cruise.android.ui.screens.pos

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class ConvenienceUsageItem(
    val id: String,
    val name: String,
    val description: String? = null,
    val price: String? = null
)

@Composable
fun ConvenienceUsageScreen(
    bookingPassengerId: Long,
    passengerName: String,
    bookingId: Long,
    tourId: String,
    tourPackageId: String,
    products: List<ConvenienceUsageItem>,
    services: List<ConvenienceUsageItem>,
    isLoading: Boolean,
    isSubmitting: Boolean,
    errorMessage: String?,
    onBackClick: () -> Unit,
    onConfirmProduct: (productTourId: String, quantity: Int) -> Unit,
    onConfirmService: (serviceTourId: String) -> Unit
) {
    var selectedType by remember {
        mutableStateOf(ConvenienceUsageType.PRODUCT)
    }

    var selectedItem by remember {
        mutableStateOf<ConvenienceUsageItem?>(null)
    }

    var quantity by remember {
        mutableIntStateOf(1)
    }

    LaunchedEffect(selectedType) {
        selectedItem = null
        quantity = 1
    }

    val items = when (selectedType) {
        ConvenienceUsageType.PRODUCT -> products
        ConvenienceUsageType.SERVICE -> services
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {

        TextButton(
            onClick = onBackClick,
            enabled = !isSubmitting
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
                selected = selectedType == ConvenienceUsageType.PRODUCT,
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
                selected = selectedType == ConvenienceUsageType.SERVICE,
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
            text = when (selectedType) {
                ConvenienceUsageType.PRODUCT ->
                    "Chọn sản phẩm"

                ConvenienceUsageType.SERVICE ->
                    "Chọn dịch vụ"
            },
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (isLoading) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            ) {
                CircularProgressIndicator()
            }

        } else if (items.isEmpty()) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Text(
                    text = when (selectedType) {
                        ConvenienceUsageType.PRODUCT ->
                            "Tour này không có sản phẩm tiện ích."

                        ConvenienceUsageType.SERVICE ->
                            "Tour này không có dịch vụ tiện ích."
                    },
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
                    items = items,
                    key = { it.id }
                ) { item ->

                    ConvenienceUsageItemCard(
                        item = item,
                        selected = selectedItem?.id == item.id,
                        onClick = {
                            selectedItem = item
                        }
                    )
                }
            }
        }

        if (selectedType == ConvenienceUsageType.PRODUCT &&
            selectedItem != null
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
                    }
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
                    }
                ) {
                    Text("+")
                }
            }
        }

        errorMessage?.let { message ->

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = message,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {

                val item = selectedItem ?: return@Button

                when (selectedType) {

                    ConvenienceUsageType.PRODUCT -> {
                        onConfirmProduct(
                            item.id,
                            quantity
                        )
                    }

                    ConvenienceUsageType.SERVICE -> {
                        onConfirmService(
                            item.id
                        )
                    }
                }
            },
            enabled = selectedItem != null &&
                    !isLoading &&
                    !isSubmitting,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isSubmitting) {
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
private fun ConvenienceUsageItemCard(
    item: ConvenienceUsageItem,
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
                text = item.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            item.description
                ?.takeIf { it.isNotBlank() }
                ?.let { description ->

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

            item.price
                ?.takeIf { it.isNotBlank() }
                ?.let { price ->

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = price,
                        fontWeight = FontWeight.SemiBold
                    )
                }
        }
    }
}

enum class ConvenienceUsageType {
    PRODUCT,
    SERVICE
}