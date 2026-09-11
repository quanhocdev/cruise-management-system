package com.project.cruise.android.ui.screens.passenger

import com.project.cruise.android.data.dto.passenger.TripCatalogItem
import java.math.BigDecimal

/** Local preview only. The server must validate price, ownership and stock before accepting an order. */
internal fun orderDraftError(item: TripCatalogItem, passengerId: Long?, passengerIds: Set<Long>, quantity: String): String? {
    if (passengerId == null || passengerId !in passengerIds) return "Chọn hành khách trong booking này."
    if (item.type !in setOf("SERVICE", "PRODUCT")) return "Loại danh mục chưa được hỗ trợ."
    if (item.status !in setOf("CONFIGURED", "NOT_STARTED", "IN_PROGRESS")) return "Mục này hiện không phù hợp để soạn đơn."
    val count = quantity.toIntOrNull()
    if (count == null || count !in 1..99) return "Nhập số lượng từ 1 đến 99."
    if (item.type == "SERVICE" && count != 1) return "Mỗi bản nháp dịch vụ dành cho một hành khách."
    if (item.price == null || item.price.signum() < 0) return "Chưa có giá để tính tạm tính."
    return null
}

internal fun orderDraftTotal(price: BigDecimal?, quantity: String): BigDecimal? {
    val count = quantity.toIntOrNull()?.takeIf { it in 1..99 } ?: return null
    return price?.takeIf { it.signum() >= 0 }?.multiply(BigDecimal.valueOf(count.toLong()))
}
