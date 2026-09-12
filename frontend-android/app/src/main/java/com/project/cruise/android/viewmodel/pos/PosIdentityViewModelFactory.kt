//package com.project.cruise.android.viewmodel.pos
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.ViewModelProvider
//import com.project.cruise.android.data.repository.PosIdentityRepository
//
//class PosIdentityViewModelFactory(
//    private val repository: PosIdentityRepository,
//    private val localId: String
//) : ViewModelProvider.Factory {
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        require(modelClass.isAssignableFrom(PosIdentityViewModel::class.java)) {
//            "Unknown ViewModel class"
//        }
//        @Suppress("UNCHECKED_CAST")
//        return PosIdentityViewModel(repository, localId) as T
//    }
//}