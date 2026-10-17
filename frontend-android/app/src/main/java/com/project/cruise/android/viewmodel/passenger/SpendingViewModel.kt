package com.project.cruise.android.viewmodel.passenger

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.project.cruise.android.data.dto.booking.SpendingSummary
import com.project.cruise.android.data.network.ApiService
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SpendingState(val summary: SpendingSummary? = null, val loading: Boolean = false, val error: String? = null)

class SpendingViewModel(private val api: ApiService, private val bookingId: Long) : ViewModel() {
    private val mutable = MutableStateFlow(SpendingState())
    val state = mutable.asStateFlow()
    init { refresh() }

    fun refresh() = load(0)
    fun nextPage() {
        val summary = mutable.value.summary ?: return
        if (summary.items.size < summary.totalItems) load(summary.page + 1)
    }

    private fun load(page: Int) {
        if (mutable.value.loading) return
        mutable.value = mutable.value.copy(loading = true, error = null)
        viewModelScope.launch {
            try {
                val response = api.getBookingSpending(bookingId, page)
                val items = if (page == 0) response.items else mutable.value.summary.orEmptyItems() + response.items
                mutable.value = SpendingState(response.copy(items = items.distinctBy { "${it.type}:${it.usageId}" }))
            } catch (cancelled: CancellationException) { throw cancelled }
            catch (ex: Exception) {
                mutable.value = mutable.value.copy(loading = false, error = "Chưa tải được chi tiêu. Vui lòng thử lại.")
            }
        }
    }

    private fun SpendingSummary?.orEmptyItems() = this?.items.orEmpty()

    class Factory(private val api: ApiService, private val bookingId: Long) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = SpendingViewModel(api, bookingId) as T
    }
}
