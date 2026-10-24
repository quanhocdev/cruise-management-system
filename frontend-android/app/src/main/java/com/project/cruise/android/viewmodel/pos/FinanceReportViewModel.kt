package com.project.cruise.android.viewmodel.pos

import androidx.lifecycle.*
import com.project.cruise.android.data.dto.booking.FinanceReport
import com.project.cruise.android.data.repository.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import retrofit2.HttpException

data class FinanceReportState(val tours:List<ReportTour> = emptyList(),val tourLoading:Boolean=false,val tourError:String?=null,
    val selected:ReportTour?=null,val report:FinanceReport?=null,val busy:Boolean=false,val error:String?=null,
    val page:Int=0,val status:String="ALL",val sort:String="newest")
class FinanceReportViewModel(private val source:FinanceReportSource):ViewModel() {
    private val mutable=MutableStateFlow(FinanceReportState())
    val state=mutable.asStateFlow()
    private var request:Job?=null
    fun loadTours() {
        if(mutable.value.tourLoading)return
        mutable.value=mutable.value.copy(tourLoading=true,tourError=null)
        viewModelScope.launch {
            try{mutable.value=mutable.value.copy(tours=source.tours(),tourLoading=false)}
            catch(e:CancellationException){throw e}
            catch(e:Exception){mutable.value=mutable.value.copy(tourLoading=false,tourError="Chưa tải được danh sách chuyến. Vui lòng thử lại.")}
        }
    }
    fun select(tour:ReportTour){mutable.value=mutable.value.copy(selected=tour,page=0);refresh()}
    fun filter(status:String){mutable.value=mutable.value.copy(status=status,page=0);refresh()}
    fun sort(value:String){mutable.value=mutable.value.copy(sort=value,page=0);refresh()}
    fun page(value:Int){if(value<0)return;mutable.value=mutable.value.copy(page=value);refresh()}
    fun refresh(){
        val selected=mutable.value.selected?:return
        request?.cancel()
        val query=mutable.value
        mutable.value=query.copy(report=null,busy=true,error=null)
        request=viewModelScope.launch {
            try{
                val result=source.report(selected.id,query.page,query.status,query.sort)
                currentCoroutineContext().ensureActive()
                require(result.tourId==selected.id && result.currency=="VND"){"Mismatched report"}
                mutable.value=mutable.value.copy(report=result,busy=false)
            }catch(e:CancellationException){throw e}
            catch(e:Exception){mutable.value=mutable.value.copy(busy=false,error=if(e is HttpException && e.code() in setOf(401,403))
                "Vui lòng đăng nhập tài khoản Finance để xem thống kê." else "Chưa tải được thống kê. Vui lòng thử lại.")}
        }
    }
    class Factory(private val source:FinanceReportSource):ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST") override fun <T:ViewModel> create(modelClass:Class<T>):T=FinanceReportViewModel(source) as T
    }
}
