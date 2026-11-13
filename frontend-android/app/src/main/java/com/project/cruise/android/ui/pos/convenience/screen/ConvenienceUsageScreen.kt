package com.project.cruise.android.ui.pos.convenience.screen

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.project.cruise.android.data.dto.convenience.ProductTourResponse
import com.project.cruise.android.data.dto.convenience.ServiceTourResponse
import com.project.cruise.android.ui.components.QuantityStepper
import com.project.cruise.android.ui.components.SuccessSoundEffect
import com.project.cruise.android.ui.components.usage.SectionTitle
import com.project.cruise.android.ui.components.usage.SelectableList
import com.project.cruise.android.ui.components.usage.UsageScreenScaffold
import com.project.cruise.android.ui.pos.convenience.components.usage.*
import com.project.cruise.android.viewmodel.convenience.ConvenienceUsageViewModel

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

    var selectedType by remember { mutableStateOf(ConvenienceUsageType.PRODUCT) }
    var selectedProduct by remember { mutableStateOf<ProductTourResponse?>(null) }
    var selectedService by remember { mutableStateOf<ServiceTourResponse?>(null) }
    var quantity by remember { mutableIntStateOf(1) }

    val isProduct = selectedType == ConvenienceUsageType.PRODUCT

    SuccessSoundEffect(uiState.successMessage) { viewModel.clearSuccess() }

    LaunchedEffect(tourId) {
        viewModel.loadConvenienceTours(tourId)
    }

    LaunchedEffect(selectedType) {
        selectedProduct = null
        selectedService = null
        quantity = 1
        viewModel.clearError()
    }

    val hasSelection = if (isProduct) selectedProduct != null else selectedService != null

    UsageScreenScaffold(
        title = "Tiện ích hành khách",
        passengerName = passengerName,
        bookingId = bookingId,
        bookingPassengerId = bookingPassengerId,
        isSubmitting = uiState.isSubmitting,
        errorMessage = uiState.errorMessage,
        successMessage = uiState.successMessage,
        confirmEnabled = hasSelection && !uiState.isLoading,
        onConfirm = {
            if (isProduct) {
                selectedProduct?.let { product ->
                    viewModel.createProductUsage(
                        nfcCardUid = nfcCardUid,
                        productTourId = product.id,
                        quantity = quantity
                    )
                }
            } else {
                selectedService?.let { service ->
                    viewModel.createServiceUsage(
                        nfcCardUid = nfcCardUid,
                        serviceTourId = service.id
                    )
                }
            }
        },
        onBackClick = onBackClick
    ) {
        SectionTitle("Loại tiện ích")

        ConvenienceTypeSelector(
            selected = selectedType,
            onSelect = { selectedType = it },
        )

        Spacer(Modifier.height(16.dp))

        SectionTitle(if (isProduct) "Chọn sản phẩm" else "Chọn dịch vụ")

        if (isProduct) {
            SelectableList(
                items = uiState.products,
                isLoading = uiState.isLoading,
                emptyText = "Tour này không có sản phẩm tiện ích.",
                key = { it.id }
            ) { product ->
                ConvenienceProductCard(
                    product = product,
                    selected = selectedProduct?.id == product.id,
                    onClick = { selectedProduct = product }
                )
            }
        } else {
            SelectableList(
                items = uiState.services,
                isLoading = uiState.isLoading,
                emptyText = "Tour này không có dịch vụ tiện ích.",
                key = { it.id }
            ) { service ->
                ConvenienceServiceCard(
                    service = service,
                    selected = selectedService?.id == service.id,
                    onClick = { selectedService = service }
                )
            }
        }

        if (isProduct && selectedProduct != null) {
            Spacer(Modifier.height(12.dp))

            SectionTitle("Số lượng")

            QuantityStepper(
                quantity = quantity,
                onQuantityChange = { quantity = it },
                enabled = !uiState.isSubmitting
            )
        }
    }
}