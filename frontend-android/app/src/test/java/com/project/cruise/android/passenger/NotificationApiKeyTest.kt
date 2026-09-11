package com.project.cruise.android.passenger

import com.project.cruise.android.data.network.ApiService
import com.project.cruise.android.ui.screens.passenger.NotificationApiKey
import org.junit.Assert.*
import org.junit.Test
import retrofit2.Retrofit

class NotificationApiKeyTest {
    @Test fun retrofitProxyNeedsReferentialComposeKey() {
        val retrofit = Retrofit.Builder().baseUrl("http://localhost/").build()
        val api = retrofit.create(ApiService::class.java)
        // Reproduces the cause with the actual Retrofit version used by the app.
        assertFalse(api.equals(api))
        assertEquals(NotificationApiKey(api), NotificationApiKey(api))
        assertEquals(NotificationApiKey(api).hashCode(), NotificationApiKey(api).hashCode())
        assertNotEquals(NotificationApiKey(api), NotificationApiKey(retrofit.create(ApiService::class.java)))
    }
}
