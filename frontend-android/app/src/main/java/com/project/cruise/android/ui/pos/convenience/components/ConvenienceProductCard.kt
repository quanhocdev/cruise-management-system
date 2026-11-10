package com.project.cruise.android.ui.pos.convenience.components.usage

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.project.cruise.android.data.dto.convenience.ProductTourResponse
import com.project.cruise.android.data.dto.convenience.ServiceTourResponse
import com.project.cruise.android.ui.common.OptionalInfoText
import com.project.cruise.android.ui.common.SelectableCard
import com.project.cruise.android.ui.theme.OceanNavy

@Composable
fun ConvenienceProductCard(
    product: ProductTourResponse,
    selected: Boolean,
    onClick: () -> Unit,
) {
    SelectableCard(selected = selected, onClick = onClick) {
        Text(
            text = product.productName ?: "Không có tên sản phẩm",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = OceanNavy,
        )
        OptionalInfoText(
            value = product.productDescription,
            style = MaterialTheme.typography.bodyMedium,
        )
        OptionalInfoText(product.quantity, prefix = "Số lượng cấu hình: ", topSpace = 8.dp)
    }
}

@Composable
fun ConvenienceServiceCard(
    service: ServiceTourResponse,
    selected: Boolean,
    onClick: () -> Unit,
) {
    SelectableCard(selected = selected, onClick = onClick) {
        Text(
            text = service.serviceName ?: "Không có tên dịch vụ",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = OceanNavy,
        )
        OptionalInfoText(
            value = service.serviceDescription,
            style = MaterialTheme.typography.bodyMedium,
        )
        OptionalInfoText(service.servicePrice, topSpace = 8.dp, emphasized = true)
        OptionalInfoText(service.durationMinutes, prefix = "Thời lượng: ", suffix = " phút")
    }
}