package com.project.cruise.android.viewmodel.tour

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.project.cruise.android.data.repository.TourRepository

class TourViewModelFactory(
    private val repository: TourRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TourViewModel::class.java)) {
            return TourViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}