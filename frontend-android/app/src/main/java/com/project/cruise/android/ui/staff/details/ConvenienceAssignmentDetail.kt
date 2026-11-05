
package com.project.cruise.android.ui.staff.details

import androidx.compose.runtime.Composable
import com.project.cruise.android.data.dto.staff.StaffAssignment
import com.project.cruise.android.ui.staff.components.StaffDetailRow
import com.project.cruise.android.viewmodel.staff.StaffAssignmentsState

@Composable
fun ConvenienceAssignmentDetail(
    item: StaffAssignment,
    state: StaffAssignmentsState
) {
    when (item.assignmentType) {
        "PRODUCT" -> {
            val product = state.productOf(item)

            if (product != null) {
                StaffDetailRow("Sản phẩm", product.productName)
                StaffDetailRow("Khu vực", product.cruiseAreaName)

                StaffDetailRow(
                    "Tầng",
                    product.deckNumber?.let { "Tầng $it" }
                )

                StaffDetailRow(
                    "Số lượng",
                    product.quantity?.toString()
                )

                StaffDetailRow(
                    "Mô tả",
                    product.productDescription
                )
            } else {
                StaffDetailRow("Sản phẩm", "Đang tải thông tin...")
            }
        }

        "SERVICE" -> {
            val service = state.serviceOf(item)

            if (service != null) {
                StaffDetailRow("Dịch vụ", service.serviceName)
                StaffDetailRow("Khu vực", service.cruiseAreaName)

                StaffDetailRow(
                    "Tầng",
                    service.deckNumber?.let { "Tầng $it" }
                )

                StaffDetailRow(
                    "Thời lượng",
                    service.durationMinutes?.let { "$it phút" }
                )

                StaffDetailRow(
                    "Số khách tối đa",
                    service.maxPassengers?.toString()
                )

                StaffDetailRow(
                    "Mô tả",
                    service.serviceDescription
                )
            } else {
                StaffDetailRow("Dịch vụ", "Đang tải thông tin...")
            }
        }
    }
}
