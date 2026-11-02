package com.project.cruise.android.navigation

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.project.cruise.android.data.auth.TokenManager
import com.project.cruise.android.data.dto.staff.StaffRole
import com.project.cruise.android.data.network.RetrofitClient
import com.project.cruise.android.data.repository.StaffAssignmentRepository
import com.project.cruise.android.ui.screens.staff.StaffAssignmentsScreen
import com.project.cruise.android.viewmodel.auth.AuthViewModel
import com.project.cruise.android.viewmodel.auth.SessionState
import com.project.cruise.android.viewmodel.staff.StaffAssignmentsViewModel

fun NavGraphBuilder.staffGraph(navController: NavHostController, auth: AuthViewModel, tokens: TokenManager) {
    composable(Routes.STAFF_ASSIGNMENTS) {
        val session by auth.sessionState.collectAsState()
        val user = session as? SessionState.Authenticated
        val role = StaffRole.fromApiRole(user?.role)
        if (role == null || user == null) {
            LaunchedEffect(session) {
                if (session != SessionState.Checking) navController.navigate(Routes.SPLASH) {
                    popUpTo(navController.graph.id) { inclusive = false }
                }
            }
        } else {
            val repository = remember(role, user.username) {
                StaffAssignmentRepository(RetrofitClient.createApiService(tokens, retryOnConnectionFailure = false), role)
            }
            val factory = remember(repository) {
                object : ViewModelProvider.Factory {
                    @Suppress("UNCHECKED_CAST")
                    override fun <T : ViewModel> create(modelClass: Class<T>): T = StaffAssignmentsViewModel(repository) as T
                }
            }
            val model: StaffAssignmentsViewModel = viewModel(key = "${role.name}:${user.username}", factory = factory)
            val state by model.state.collectAsState()
            StaffAssignmentsScreen(role, user.username, state, model::refresh, model::select, model::respond) {
                auth.logout {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(navController.graph.id) { inclusive = false }
                        launchSingleTop = true
                    }
                }
            }
        }
    }
}
