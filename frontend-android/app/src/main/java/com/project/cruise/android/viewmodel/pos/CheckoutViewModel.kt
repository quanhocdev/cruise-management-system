package com.project.cruise.android.viewmodel.pos

import androidx.lifecycle.*
import com.project.cruise.android.data.dto.booking.*
import com.project.cruise.android.data.repository.CheckoutSource
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import retrofit2.HttpException
import java.time.Instant

data class CheckoutState(val bill: ServiceBill?=null,val preview: CheckoutPreview?=null,
    val busy: Boolean=false,val error: String?=null,val message: String?=null)
object CheckoutDisplay {
    fun title(state: String)=when(state){
        "NOT_ISSUED"->"Chưa chốt bill"
        "PAID"->"Đã quyết toán"
        "PENDING"->"Chờ thanh toán"
        "VERIFYING"->"Đang xác minh"
        "UNKNOWN"->"Cần đối chiếu giao dịch"
        "UNAVAILABLE"->"Chưa tải được trạng thái thanh toán"
        "FAILED"->"Thanh toán chưa thành công"
        else->"Chưa tạo thanh toán"
    }
    fun paymentUrl(bill: ServiceBill,now: Instant=Instant.now()): String? {
        if(bill.paymentState!="PENDING" || !bill.paymentAvailable || bill.billStatus=="PAID")return null
        return bill.payments.firstOrNull{it.status=="PENDING" && runCatching{Instant.parse(it.expiresAt).isAfter(now)}.getOrDefault(false)}
            ?.paymentUrl?.takeIf{runCatching{java.net.URI(it).scheme=="https" && java.net.URI(it).host!=null}.getOrDefault(false)}
    }
    fun canPay(bill: ServiceBill)=bill.billId!=null && bill.billStatus!="PAID" && bill.paymentAvailable &&
        bill.paymentState in setOf("NOT_STARTED","FAILED")
    fun kind(type: String)=when(type){"ACTIVITY_CRUISE"->"Hoạt động trên tàu";"ACTIVITY_VISIT"->"Tham quan bờ";"SERVICE"->"Dịch vụ";"PRODUCT"->"Sản phẩm";"VOID"->"Hủy khoản chi";"REFUND"->"Hoàn khoản chi";else->type}
}
class CheckoutViewModel(private val source: CheckoutSource,private val finance: Boolean): ViewModel(){
    private val mutable=MutableStateFlow(CheckoutState())
    val state=mutable.asStateFlow()
    private var bookingId: Long?=null
    private var searchCode: String?=null
    fun find(code: String){
        if(code.isBlank())return
        run(clear=true){
            searchCode=code.trim()
            bookingId=null
            bookingId=source.find(searchCode!!)
            load()
        }
    }
    fun open(id: Long){bookingId=id;refresh()}
    fun refresh(){
        if(bookingId!=null)run{load()}
        else searchCode?.let(::find)
    }
    private suspend fun load(){
        val id=bookingId?:return
        val bill=source.bill(id,finance)
        mutable.value=CheckoutState(bill=bill,busy=true)
        val preview=if(finance && bill.billId==null)source.preview(id) else null
        mutable.value=CheckoutState(bill=bill,preview=preview)
    }
    fun confirm(){
        val preview=mutable.value.preview?:return
        if(!finance)return
        run {
            source.confirm(preview.bookingId,preview.previewVersion)
            mutable.value=mutable.value.copy(preview=null,message="Đã chốt bill. Đang tải trạng thái…")
            load()
        }
    }
    fun pay(){
        val bill=mutable.value.bill?:return
        if(!finance || !CheckoutDisplay.canPay(bill))return
        run{source.payment(bill.bookingId);load()}
    }
    fun export(write: suspend (ByteArray)->Unit){
        val bill=mutable.value.bill?:return
        if(bill.billStatus!="PAID")return
        run{write(source.receipt(bill.bookingId,finance));mutable.value=mutable.value.copy(message="Đã lưu biên nhận PDF.")}
    }
    private fun run(clear: Boolean=false,block: suspend ()->Unit){
        if(mutable.value.busy)return
        mutable.value=if(clear)CheckoutState(busy=true) else mutable.value.copy(busy=true,error=null,message=null)
        viewModelScope.launch {
            try{block()}
            catch(e: CancellationException){throw e}
            catch(e: Exception){
                mutable.value=mutable.value.copy(error=when{
                    e is HttpException && e.code()==404->"Không tìm thấy booking hoặc bạn không có quyền xem."
                    e is HttpException && e.code() in setOf(401,403)->"Phiên đăng nhập hết hạn hoặc tài khoản không có quyền."
                    e is HttpException && e.code()==409->"Trạng thái đã thay đổi. Cập nhật bill và kiểm tra yêu cầu đang chờ."
                    e is IllegalStateException->e.message
                    else->"Chưa nhận được kết quả. Cập nhật bill hoặc thử lại đúng thao tác; chưa thể coi là đã thanh toán."
                })
            }finally{mutable.value=mutable.value.copy(busy=false)}
        }
    }
    class Factory(private val source: CheckoutSource,private val finance: Boolean):ViewModelProvider.Factory{
        @Suppress("UNCHECKED_CAST") override fun <T:ViewModel> create(modelClass: Class<T>)=CheckoutViewModel(source,finance) as T
    }
}
