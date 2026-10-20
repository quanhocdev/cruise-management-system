package com.project.cruise.android.data.repository

import android.content.Context
import com.project.cruise.android.data.auth.TokenManager
import com.project.cruise.android.data.dto.booking.*
import com.project.cruise.android.data.network.RetrofitClient

interface CheckoutSource {
    suspend fun find(code: String): Long
    suspend fun bill(id: Long,finance: Boolean): ServiceBill
    suspend fun preview(id: Long): CheckoutPreview
    suspend fun confirm(id: Long,version: String): CheckoutSnapshot
    suspend fun payment(id: Long): ServiceBillPayment
    suspend fun receipt(id: Long,finance: Boolean): ByteArray
}
class CheckoutRepository(context: Context): CheckoutSource {
    private val api=RetrofitClient.createApiService(TokenManager(context.applicationContext))
    private val commands=OnlinePosCommands(context.applicationContext)
    override suspend fun find(code: String)=api.getFinanceSpending(code).bookingId
    override suspend fun bill(id: Long,finance: Boolean)=api.getServiceBill(if(finance)"finance" else "passengers",id)
    override suspend fun preview(id: Long)=api.getCheckoutPreview(id)
    override suspend fun confirm(id: Long,version: String): CheckoutSnapshot {
        val draft=CheckoutConfirm(version)
        return commands.execute("CHECKOUT:$id",draft){key->api.confirmCheckout(id,draft.copy(clientRequestId=key))}
    }
    override suspend fun payment(id: Long)=commands.execute("BILL_PAYMENT:$id",mapOf("bookingId" to id)){key->api.createBillPayment(id,BillPaymentCommand(key))}
    override suspend fun receipt(id: Long,finance: Boolean)=api.getBillReceipt(if(finance)"finance" else "passengers",id).use{it.bytes()}
}
