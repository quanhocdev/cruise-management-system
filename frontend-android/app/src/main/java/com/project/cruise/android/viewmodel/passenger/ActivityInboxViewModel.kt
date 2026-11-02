package com.project.cruise.android.viewmodel.passenger

import androidx.lifecycle.*
import com.project.cruise.android.data.dto.booking.ActivityNotice
import com.project.cruise.android.data.repository.ActivityInboxSource
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import retrofit2.HttpException

data class ActivityInboxState(
    val items: List<ActivityNotice> = emptyList(),val unreadCount: Long? = null,
    val page: Int = 0,val unreadOnly: Boolean = false,val loading: Boolean = false,
    val reading: String? = null,val error: String? = null,val selected: ActivityNotice? = null
)

class ActivityInboxViewModel(private val source: ActivityInboxSource): ViewModel() {
    private val mutable=MutableStateFlow(ActivityInboxState())
    val state=mutable.asStateFlow()
    private var loadJob: Job?=null
    fun refresh(page: Int=mutable.value.page,unreadOnly: Boolean=mutable.value.unreadOnly) {
        if(page<0 || mutable.value.reading!=null)return
        loadJob?.cancel()
        val changed=page!=mutable.value.page || unreadOnly!=mutable.value.unreadOnly
        mutable.value=mutable.value.copy(page=page,unreadOnly=unreadOnly,loading=true,error=null,
            items=if(changed)emptyList() else mutable.value.items)
        loadJob=viewModelScope.launch {
            try {
                val items=source.list(page,unreadOnly)
                val count=source.unreadCount()
                currentCoroutineContext().ensureActive()
                mutable.value=mutable.value.copy(items=items,unreadCount=count,loading=false)
            } catch(e: CancellationException) { throw e }
            catch(e: Exception) {
                currentCoroutineContext().ensureActive()
                val denied=e is HttpException && e.code() in setOf(401,403)
                mutable.value=mutable.value.copy(loading=false,unreadCount=null,
                    items=if(denied)emptyList() else mutable.value.items,
                    error=if(denied)"Vui lòng đăng nhập tài khoản hành khách." else "Chưa cập nhật được thông báo. Vui lòng thử lại.")
            }
        }
    }
    fun open(notice: ActivityNotice) {
        if(mutable.value.reading!=null)return
        loadJob?.cancel()
        mutable.value=mutable.value.copy(reading=notice.id,loading=false,error=null,selected=null)
        viewModelScope.launch {
            try {
                val result=source.markRead(notice.id)
                check(result.id==notice.id)
                val read=notice.copy(readAt=result.readAt)
                val wasUnread=notice.readAt==null
                mutable.value=mutable.value.copy(reading=null,selected=read,
                    unreadCount=mutable.value.unreadCount?.let { (it-if(wasUnread)1 else 0).coerceAtLeast(0) },
                    items=mutable.value.items.map { if(it.id==notice.id)read else it }
                        .filter { !mutable.value.unreadOnly || it.readAt==null })
                refresh()
            } catch(e: CancellationException) { throw e }
            catch(e: Exception) {
                mutable.value=mutable.value.copy(reading=null,error="Chưa xác nhận đã đọc. Kiểm tra kết nối và mở lại thông báo để thử lại.")
            }
        }
    }
    fun dismiss() { mutable.value=mutable.value.copy(selected=null) }
    class Factory(private val source: ActivityInboxSource): ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T:ViewModel> create(modelClass:Class<T>):T=ActivityInboxViewModel(source) as T
    }
}
