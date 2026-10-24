package com.project.cruise.android.viewmodel.pos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonParser
import com.project.cruise.android.data.dto.booking.*
import com.project.cruise.android.data.repository.PaymentRefundSource
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

data class PaymentRefundState(val bookingCode: String = "", val payments: List<RefundPayment> = emptyList(),
    val summary: PaymentRefundSummary? = null, val busy: Boolean = false, val error: String? = null,
    val receipt: String? = null)

object PaymentRefundDraft {
    fun valid(summary: PaymentRefundSummary, amount: String, reason: String): Boolean {
        val value = SpendingLimitDraft.amount(amount) ?: return false
        return summary.canRefund && value.signum()>0 && value<=summary.refundableAmount &&
            reason.isNotBlank() && reason.trim().length<=1000
    }
    fun status(value: String) = when(value) {
        "QUEUED" -> "Chờ gửi VNPay"
        "SENDING", "PROCESSING" -> "Đang xử lý"
        "UNKNOWN" -> "Chờ xác minh với VNPay"
        "SUCCEEDED" -> "Hoàn tiền thành công"
        "REJECTED" -> "Yêu cầu bị từ chối"
        else -> "Chưa xác định"
    }
}

class PaymentRefundViewModel(private val source: PaymentRefundSource): ViewModel() {
    private val mutable = MutableStateFlow(PaymentRefundState())
    val state = mutable.asStateFlow()
    fun find(code: String) {
        if (mutable.value.busy || code.isBlank()) return
        mutable.value = PaymentRefundState(bookingCode=code.trim(),busy=true)
        viewModelScope.launch {
            try { mutable.value = mutable.value.copy(payments=source.find(code.trim()),busy=false) }
            catch (ex: CancellationException) { throw ex }
            catch (ex: Exception) { mutable.value=mutable.value.copy(busy=false,error=message(ex)) }
        }
    }
    fun select(id: Long,page: Int=0) {
        if (mutable.value.busy || page<0) return
        mutable.value=mutable.value.copy(busy=true,error=null,summary=null)
        viewModelScope.launch {
            try { mutable.value=mutable.value.copy(summary=source.summary(id,page),busy=false) }
            catch (ex: CancellationException) { throw ex }
            catch (ex: Exception) { mutable.value=mutable.value.copy(busy=false,error=message(ex)) }
        }
    }
    fun refund(amount: String,reason: String) {
        val summary=mutable.value.summary ?: return
        if (mutable.value.busy || !PaymentRefundDraft.valid(summary,amount,reason)) return
        mutable.value=mutable.value.copy(busy=true,error=null,receipt=null)
        viewModelScope.launch {
            try {
                val result=source.refund(summary.paymentId,PaymentRefundRequest(SpendingLimitDraft.amount(amount)!!,reason.trim()))
                val refreshed=try { source.summary(summary.paymentId,0) }
                    catch (ex: CancellationException) { throw ex }
                    catch (ex: Exception) { null }
                mutable.value=mutable.value.copy(summary=refreshed,busy=false,
                    receipt="Đã tiếp nhận yêu cầu ${result.refundId.take(8)}. Bấm cập nhật để xem kết quả VNPay; chưa có nghĩa tiền đã hoàn.")
            } catch (ex: CancellationException) { throw ex }
            catch (ex: Exception) { mutable.value=mutable.value.copy(busy=false,error=message(ex)) }
        }
    }
    private fun message(ex: Exception): String {
        val code=if(ex is HttpException) runCatching { JsonParser().parse(ex.response()?.errorBody()?.string()).asJsonObject.get("code")?.asString }.getOrNull() else null
        return when(code) {
            "REFUND_EXCEEDS_REMAINING" -> "Số tiền vượt phần còn được hoàn. Hãy cập nhật lại."
            "REFUND_RECONCILIATION_PENDING" -> "Có yêu cầu đang chờ VNPay xác minh. Chưa thể tạo khoản hoàn tiếp theo."
            "PAYMENT_NOT_REFUNDABLE", "PAYMENT_PROVIDER_REFERENCE_MISSING" -> "Giao dịch chưa đủ thông tin thanh toán VNPay để hoàn tiền."
            "IDEMPOTENCY_CONFLICT" -> "Nội dung khác lần gửi trước. Kiểm tra lịch sử trước khi tiếp tục."
            else -> if(ex is HttpException && ex.code() in listOf(401,403)) "Bạn cần đăng nhập bằng tài khoản Finance."
                else "Chưa nhận được kết quả. Kiểm tra kết nối và thử lại đúng nội dung cũ."
        }
    }
    class Factory(private val source: PaymentRefundSource): ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST") override fun <T: ViewModel> create(modelClass: Class<T>): T = PaymentRefundViewModel(source) as T
    }
}
