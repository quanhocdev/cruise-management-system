package com.project.cruise.android.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.project.cruise.android.data.auth.TokenManager
import com.project.cruise.android.data.repository.chat.ChatRepository
import com.project.cruise.android.viewmodel.auth.AuthViewModel
import com.project.cruise.android.viewmodel.passenger.FeedbackViewModel

/**
 * Điểm vào duy nhất của nhóm passenger. NavGraph.kt vẫn gọi hàm này như cũ.
 * Muốn thêm màn mới: vào đúng file nhóm bên dưới, không cần đụng file này
 * (trừ khi tạo nhóm mới).
 */
fun NavGraphBuilder.passengerGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    tokenManager: TokenManager,
    chatRepository: ChatRepository,
    feedbackViewModel: FeedbackViewModel,
) {
    passengerProfileGraph(navController, authViewModel)
    passengerTourGraph(navController, authViewModel, tokenManager)
    passengerBookingGraph(navController, tokenManager)
    passengerNotificationGraph(navController, tokenManager)
    passengerFeedbackGraph(navController, tokenManager, feedbackViewModel)
    passengerMessagingGraph(navController, authViewModel, chatRepository)
}