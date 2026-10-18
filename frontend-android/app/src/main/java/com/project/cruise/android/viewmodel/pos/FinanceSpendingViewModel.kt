package com.project.cruise.android.viewmodel.pos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.project.cruise.android.data.dto.booking.FinanceSpendingAccount
import com.project.cruise.android.data.repository.FinanceSpendingSource
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import retrofit2.HttpException

data class FinanceSpendingState(val account: FinanceSpendingAccount? = null, val busy: Boolean = false,
    val error: String? = null, val message: String? = null)

object SpendingLimitDraft {
    fun amount(text: String): BigDecimal? = text.trim().takeIf { it.matches(Regex("[0-9]{1,17}([.,][0-9]{1,2})?")) }
        ?.replace(',', '.')?.toBigDecimalOrNull()?.setScale(2)
    fun valid(text: String, reason: String, spent: BigDecimal) =
        amount(text)?.let { it >= spent && reason.trim().isNotEmpty() && reason.trim().length <= 1000 } ?: false
}

class FinanceSpendingViewModel(private val source: FinanceSpendingSource) : ViewModel() {
    private val mutable = MutableStateFlow(FinanceSpendingState())
    val state = mutable.asStateFlow()
    fun find(code: String) {
        if (mutable.value.busy || code.isBlank()) return
        mutable.value = FinanceSpendingState(busy=true)
        viewModelScope.launch {
            try { mutable.value = FinanceSpendingState(account=source.find(code.trim())) }
            catch (ex: CancellationException) { throw ex }
            catch (ex: Exception) { mutable.value = FinanceSpendingState(error=errorMessage(ex)) }
        }
    }
    fun save(text: String, reason: String) {
        val account = mutable.value.account ?: return
        if (mutable.value.busy || !account.canEdit || !SpendingLimitDraft.valid(text, reason, account.spent)) return
        action {
            val result = source.setLimit(account.bookingId, requireNotNull(SpendingLimitDraft.amount(text)), reason.trim())
            mutable.value = FinanceSpendingState(account=account.copy(creditLimit=result.creditLimit, spent=result.spent, status="OPEN"), message="Đã lưu hạn mức.")
        }
    }
    fun importHistory() {
        val account = mutable.value.account ?: return
        if (mutable.value.busy || account.pendingHistoryCount == 0L || account.status=="NOT_OPENED") return
        action {
            val result = source.importHistory(account.bookingId)
            // Refresh the count from the server; a concurrent new usage may have been recorded.
            val refreshed = try { source.find(account.bookingCode) } catch (ex: CancellationException) { throw ex }
                catch (ex: Exception) {
                    mutable.value = FinanceSpendingState(message="Đã bổ sung ${result.importedCount} lượt. Tra cứu lại booking để xem dữ liệu mới.")
                    return@action
                }
            mutable.value = FinanceSpendingState(account=refreshed, message="Đã bổ sung ${result.importedCount} lượt; số tiền không thay đổi.")
        }
    }
    private fun action(block: suspend () -> Unit) {
        mutable.value = mutable.value.copy(busy=true, error=null, message=null)
        viewModelScope.launch {
            try { block() }
            catch (ex: CancellationException) { throw ex }
            catch (ex: Exception) { mutable.value = mutable.value.copy(busy=false, error=errorMessage(ex)) }
        }
    }
    private fun errorMessage(ex: Exception): String = if (ex is HttpException) when(ex.code()) {
        401,403 -> "Tài khoản không có quyền Finance hoặc phiên đăng nhập đã hết hạn."
        404 -> "Không tìm thấy mã booking."
        409 -> "Trạng thái đã thay đổi hoặc yêu cầu đang chờ xác minh. Hãy thử lại đúng thao tác trước đó."
        422 -> "Không thể thực hiện: kiểm tra số tiền đã dùng, trạng thái tài khoản và lịch sử cần đối soát."
        else -> "Chưa nhận được kết quả. Hãy thử lại cùng nội dung để tránh ghi trùng."
    } else "Chưa nhận được kết quả. Hãy kiểm tra kết nối và thử lại cùng nội dung."

    class Factory(private val source: FinanceSpendingSource) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST") override fun <T : ViewModel> create(modelClass: Class<T>): T = FinanceSpendingViewModel(source) as T
    }
}
