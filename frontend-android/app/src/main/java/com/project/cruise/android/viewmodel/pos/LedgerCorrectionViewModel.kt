package com.project.cruise.android.viewmodel.pos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.project.cruise.android.data.dto.booking.*
import com.project.cruise.android.data.repository.LedgerCorrectionSource
import com.google.gson.JsonParser
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

data class LedgerCorrectionState(val page: LedgerPage? = null, val busy: Boolean = false, val error: String? = null, val receipt: String? = null)
object CorrectionDraft {
    fun valid(entry: LedgerEntry, type: String, text: String, reason: String): Boolean {
        if (reason.isBlank() || reason.trim().length > 1000) return false
        if (type == "VOID") return entry.canVoid
        if (type != "REFUND" || !entry.canRefund) return false
        val amount = SpendingLimitDraft.amount(text) ?: return false
        return amount <= entry.remainingAmount && (amount.signum() > 0 || entry.amount.signum() == 0)
    }
}
class LedgerCorrectionViewModel(private val source: LedgerCorrectionSource): ViewModel() {
    private val mutable = MutableStateFlow(LedgerCorrectionState())
    val state = mutable.asStateFlow()
    fun find(code: String, page: Int = 0) {
        if (mutable.value.busy || code.isBlank() || page < 0) return
        mutable.value = LedgerCorrectionState(busy=true)
        viewModelScope.launch {
            try { mutable.value = LedgerCorrectionState(page=source.list(code.trim(),page)) }
            catch (ex: CancellationException) { throw ex }
            catch (ex: Exception) { mutable.value = LedgerCorrectionState(error=message(ex)) }
        }
    }
    fun correct(id: Long, type: String, text: String, reason: String) {
        val page = mutable.value.page ?: return
        val entry = page.items.find { it.id == id } ?: return
        if (mutable.value.busy || !CorrectionDraft.valid(entry,type,text,reason)) return
        mutable.value = mutable.value.copy(busy=true,error=null,receipt=null)
        viewModelScope.launch {
            try {
                val result=source.correct(id,LedgerCorrectionRequest(type,if(type=="REFUND") SpendingLimitDraft.amount(text) else null,reason.trim()))
                val receipt="Đã điều chỉnh khoản #${result.refEntryId}. Hoàn lại ${result.restoredQuota} lượt trong gói."
                val refreshed=try { source.list(page.bookingCode,page.page) }
                    catch(ex: CancellationException) { throw ex }
                    catch(ex: Exception) { null }
                mutable.value=LedgerCorrectionState(page=refreshed,receipt=receipt + if(refreshed==null) " Tra cứu lại để cập nhật số tiền." else "")
            } catch(ex: CancellationException) { throw ex }
            catch(ex: Exception) { mutable.value=mutable.value.copy(busy=false,error=message(ex)) }
        }
    }
    private fun message(ex: Exception): String {
        if (ex !is HttpException) return "Chưa nhận được kết quả. Kiểm tra mạng và thử lại đúng nội dung cũ."
        val code=runCatching { JsonParser().parse(ex.response()?.errorBody()?.string()).asJsonObject.get("code")?.asString }.getOrNull()
        return when(code) {
            "VOID_WINDOW_EXPIRED", "FINANCE_REQUIRED" -> "Cần Finance xử lý khoản này; Convenience chỉ hủy khoản mình ghi trong 10 phút."
            "ENTRY_ALREADY_CORRECTED" -> "Khoản chi đã được điều chỉnh. Hãy tra cứu lại."
            "REFUND_EXCEEDS_REMAINING" -> "Số tiền vượt phần còn được hoàn. Hãy tra cứu lại."
            "HISTORY_RECONCILIATION_REQUIRED", "QUOTA_RECONCILIATION_REQUIRED" -> "Lịch sử hoặc số lượt trong gói chưa khớp. Cần đối soát trước khi điều chỉnh."
            "ACCOUNT_NOT_OPEN", "SETTLEMENT_ALREADY_STARTED" -> "Tài khoản đã bắt đầu quyết toán. Không thể điều chỉnh ở màn này."
            "IDEMPOTENCY_CONFLICT" -> "Yêu cầu trước có nội dung khác. Không gửi giao dịch mới khi chưa xác minh kết quả."
            else -> when(ex.code()) {
                401,403 -> "Phiên đăng nhập hết hạn hoặc bạn không có quyền xử lý khoản này."
                404 -> "Không tìm thấy booking hoặc khoản chi."
                else -> "Chưa xác nhận điều chỉnh. Thử lại đúng nội dung hoặc tra cứu để kiểm tra."
            }
        }
    }
    class Factory(private val source: LedgerCorrectionSource): ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST") override fun <T: ViewModel> create(modelClass: Class<T>): T = LedgerCorrectionViewModel(source) as T
    }
}
