package com.project.cruise.android.viewmodel.passenger

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonParser
import com.project.cruise.android.data.dto.booking.*
import com.project.cruise.android.data.repository.ActivityRegistrationSource
import com.project.cruise.android.data.time.CruiseTime
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.time.LocalDateTime

data class ActivityRegistrationState(val catalog: RegistrationCatalog?=null,val items: List<ActivityRegistration> = emptyList(),
    val page: Int=0,val busy: Boolean=false,val error: String?=null,val message: String?=null)
object RegistrationDraft {
    fun canRegister(choice: RegistrationChoice,guest: RegistrationGuest?,now: LocalDateTime=CruiseTime.now()): Boolean =
        guest!=null && guest.status!="CHECKED_OUT" && choice.remainingSeats>0 && choice.target.status in listOf("CONFIGURED","NOT_STARTED") &&
            runCatching { LocalDateTime.parse(choice.target.startTime).isAfter(now) }.getOrDefault(false)
    fun status(value: String)=when(value){"CONFIRMED"->"Đã giữ chỗ";"CANCELLED"->"Đã hủy";"ATTENDED"->"Đã tham gia";else->"Đang cập nhật"}
    fun validReason(reason: String)=reason.isNotBlank() && reason.trim().length<=1000
}
class ActivityRegistrationViewModel(private val source: ActivityRegistrationSource): ViewModel() {
    private val mutable=MutableStateFlow(ActivityRegistrationState())
    val state=mutable.asStateFlow()
    fun load(bookingId: Long,page: Int=0){
        if(mutable.value.busy)return
        mutable.value=mutable.value.copy(busy=true,error=null,page=page)
        viewModelScope.launch {
            try {
                val items=source.mine(bookingId,page)
                mutable.value=mutable.value.copy(items=items,catalog=source.catalog(bookingId),busy=false)

            }catch(ex: CancellationException){throw ex}
            catch(ex: Exception){mutable.value=mutable.value.copy(busy=false,error=error(ex))}
        }
    }
    fun register(bookingId: Long,guest: RegistrationGuest,choice: RegistrationChoice){
        if(!RegistrationDraft.canRegister(choice,guest))return
        command("Đã giữ chỗ. Phí và quyền lợi được tính khi tham gia.",bookingId){source.register(RegisterActivityRequest(guest.id,choice.target.type,choice.target.id))}
    }
    fun cancel(bookingId: Long,id: Long)=command("Đã hủy đăng ký.",bookingId){source.cancel(id)}
    private fun command(message: String,bookingId: Long,action: suspend ()->ActivityRegistration){
        if(mutable.value.busy)return
        mutable.value=mutable.value.copy(busy=true,error=null,message=null)
        viewModelScope.launch {
            try {
                action();mutable.value=mutable.value.copy(message=message)
                try {mutable.value=mutable.value.copy(items=source.mine(bookingId,0),catalog=source.catalog(bookingId),page=0)}
                catch(ex: CancellationException){throw ex}
                catch(ex: Exception){mutable.value=mutable.value.copy(message="$message Bấm cập nhật để lấy danh sách mới.")}
                mutable.value=mutable.value.copy(busy=false)
            }catch(ex: CancellationException){throw ex}
            catch(ex: Exception){mutable.value=mutable.value.copy(busy=false,error=error(ex))}
        }
    }
    fun staff(role: String,code: String,search: String,page: Int=0){
        if(mutable.value.busy || code.isBlank())return
        mutable.value=mutable.value.copy(busy=true,error=null,page=page,items=emptyList())
        viewModelScope.launch {
            try {mutable.value=mutable.value.copy(items=source.staff(role,code.trim(),search.trim(),page),busy=false)}
            catch(ex: CancellationException){throw ex}
            catch(ex: Exception){mutable.value=mutable.value.copy(busy=false,error=error(ex))}
        }
    }
    fun attend(role: String,id: Long,reason: String){
        if(mutable.value.busy || !RegistrationDraft.validReason(reason))return
        mutable.value=mutable.value.copy(busy=true,error=null,message=null)
        viewModelScope.launch {
            try {val result=source.attend(role,id,reason.trim());mutable.value=mutable.value.copy(busy=false,
                items=mutable.value.items.map{if(it.id==id)result else it},message="Đã xác nhận tham gia. Chi phí đã được ghi theo quyền lợi hiện tại.")}
            catch(ex: CancellationException){throw ex}
            catch(ex: Exception){mutable.value=mutable.value.copy(busy=false,error=error(ex))}
        }
    }
    private fun error(ex: Exception): String {
        if(ex is HttpException){
            val message=runCatching{JsonParser().parse(ex.response()?.errorBody()?.string()).asJsonObject.get("message")?.asString}.getOrNull()
            if(!message.isNullOrBlank() && message.length<300)return message
            if(ex.code() in listOf(401,403))return "Phiên đăng nhập không có quyền truy cập thông tin này."
        }
        return "Chưa nhận được kết quả. Kiểm tra kết nối và thử lại đúng nội dung cũ."
    }
    class Factory(private val source: ActivityRegistrationSource): ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST") override fun <T: ViewModel> create(modelClass: Class<T>): T=ActivityRegistrationViewModel(source) as T
    }
}
