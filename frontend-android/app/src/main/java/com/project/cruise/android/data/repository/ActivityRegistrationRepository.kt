package com.project.cruise.android.data.repository

import android.content.Context
import com.project.cruise.android.data.auth.TokenManager
import com.project.cruise.android.data.dto.booking.*
import com.project.cruise.android.data.network.RetrofitClient

interface ActivityRegistrationSource {
    suspend fun catalog(bookingId: Long): RegistrationCatalog
    suspend fun mine(bookingId: Long,page: Int): List<ActivityRegistration>
    suspend fun register(request: RegisterActivityRequest): ActivityRegistration
    suspend fun cancel(id: Long): ActivityRegistration
    suspend fun staff(role: String,code: String,search: String,page: Int): List<ActivityRegistration>
    suspend fun attend(role: String,id: Long,reason: String): ActivityRegistration
    suspend fun notices(page: Int): List<ActivityNotice>
}
class ActivityRegistrationRepository(context: Context): ActivityRegistrationSource {
    private val api=RetrofitClient.createApiService(TokenManager(context.applicationContext))
    private val commands=OnlinePosCommands(context.applicationContext)
    override suspend fun catalog(bookingId: Long)=api.getRegistrationCatalog(bookingId)
    override suspend fun mine(bookingId: Long,page: Int)=api.getRegistrations(bookingId,page)
    override suspend fun register(request: RegisterActivityRequest)=commands.execute("REGISTER_ACTIVITY",request){key->api.registerActivity(request.copy(clientRequestId=key))}
    override suspend fun cancel(id: Long)=commands.execute("CANCEL_REGISTRATION",mapOf("id" to id)){key->api.cancelRegistration(id,RegistrationCommand(key))}
    override suspend fun staff(role: String,code: String,search: String,page: Int): List<ActivityRegistration> {
        val (department,usage)=route(role);return api.getStaffRegistrations(department,usage,code,search,page)
    }
    override suspend fun attend(role: String,id: Long,reason: String): ActivityRegistration {
        val (department,usage)=route(role)
        return commands.execute("ATTEND_REGISTRATION",mapOf("id" to id,"reason" to reason)){key->api.attendRegistration(department,usage,id,RegistrationAttendance(reason,key))}
    }
    override suspend fun notices(page: Int)=api.getActivityNotices(page)
    private fun route(role: String)=when(role){"ONBOARD"->"onboard" to "activity-cruise-usages";"SHORE"->"shore" to "activity-visit-usages";else->error("Bộ phận không được xác nhận hoạt động.")}
}
