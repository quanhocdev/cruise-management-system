package com.project.cruise.android.data.repository

import android.content.Context
import com.project.cruise.android.data.auth.TokenManager
import com.project.cruise.android.data.dto.booking.FinanceReport
import com.project.cruise.android.data.network.RetrofitClient

data class ReportTour(val id:String,val name:String,val code:String?,val startDate:String?)
interface FinanceReportSource {
    suspend fun tours():List<ReportTour>
    suspend fun report(tourId:String,page:Int,status:String,sort:String):FinanceReport
}
class FinanceReportRepository(context:Context):FinanceReportSource {
    private val api=RetrofitClient.createApiService(TokenManager(context.applicationContext))
    override suspend fun tours()=api.getPublicTours().map{ReportTour(it.id,it.name?:it.code?:"Chuyến chưa có tên",it.code,it.startDate)}
    override suspend fun report(tourId:String,page:Int,status:String,sort:String)=api.getFinanceReport(tourId,page,20,status,sort)
}
