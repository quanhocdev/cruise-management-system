package com.project.cruise.android.data.repository

import android.content.Context
import com.project.cruise.android.data.auth.TokenManager
import com.project.cruise.android.data.dto.booking.FinanceReport
import com.project.cruise.android.data.network.RetrofitClient

data class ReportTour(val id: String, val name: String, val code: String?, val startDate: String?)

interface FinanceReportSource {
    suspend fun tours(): List<ReportTour>
    suspend fun report(tourId: String, page: Int, status: String, sort: String): FinanceReport
}

class FinanceReportRepository(context: Context) : FinanceReportSource {
    private val tokenManager = TokenManager(context.applicationContext)

    // Tách dùng TourApiService cho public tours và FinanceApiService cho finance report
    private val tourApi = RetrofitClient.createTourApiService(tokenManager)
    private val financeApi = RetrofitClient.createFinanceApiService(tokenManager)

    override suspend fun tours() = tourApi.getPublicTours().map {
        ReportTour(it.id, it.name ?: it.code ?: "Chuyến chưa có tên", it.code, it.startDate)
    }

    override suspend fun report(tourId: String, page: Int, status: String, sort: String) =
        financeApi.getFinanceReport(tourId, page, 20, status, sort)
}