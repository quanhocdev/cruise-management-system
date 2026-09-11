package com.project.cruise.android

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import com.project.cruise.android.data.network.ApiService
import com.project.cruise.android.ui.screens.passenger.NotificationsScreen
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

class NotificationsUiTest {
    @get:Rule val compose = createComposeRule()

    @Test fun recompositionDoesNotRestartInboxAndRealRetrofitDisplaysMessages() {
        val requests = AtomicInteger()
        val release = CountDownLatch(1)
        val client = OkHttpClient.Builder().addInterceptor { chain ->
            requests.incrementAndGet()
            check(release.await(10, TimeUnit.SECONDS))
            Response.Builder().request(chain.request()).protocol(Protocol.HTTP_1_1)
                .code(200).message("OK")
                .body("""[{"id":1,"title":"Thông báo kiểm thử","message":"Nội dung kiểm thử","type":"BOOKING_CONFIRMED","readAt":null}]"""
                    .toResponseBody("application/json".toMediaType())).build()
        }.build()
        val api = Retrofit.Builder().baseUrl("http://localhost/").client(client)
            .addConverterFactory(GsonConverterFactory.create()).build().create(ApiService::class.java)
        var back = false
        try {
            compose.setContent { NotificationsScreen(api, { back = true }, {}) }
            compose.waitUntil(5_000) { requests.get() > 0 }
            compose.onNodeWithText("Chưa đọc", substring = false).performClick()
            compose.onNodeWithText("Tất cả").performClick()
            compose.onNodeWithText("← Quay lại Dashboard").assertIsEnabled().assertHeightIsAtLeast(56.dp)
            compose.runOnIdle { assertEquals(1, requests.get()) }
            release.countDown()
            compose.waitUntil(5_000) {
                compose.onAllNodesWithText("Thông báo kiểm thử").fetchSemanticsNodes().isNotEmpty()
            }
            compose.onNodeWithText("Tải lại").assertIsEnabled()
            compose.onNodeWithText("Thông báo kiểm thử").performClick()
            compose.onNodeWithText("Nội dung kiểm thử").assertIsDisplayed()
            compose.onNodeWithText("← Quay lại danh sách").performClick()
            compose.onNodeWithText("Thông báo kiểm thử").assertIsDisplayed()
            compose.onNodeWithText("← Quay lại Dashboard").performClick()
            compose.runOnIdle { assertEquals(true, back); assertEquals(1, requests.get()) }
        } finally {
            release.countDown()
            client.dispatcher.executorService.shutdown()
            client.connectionPool.evictAll()
        }
    }
}
