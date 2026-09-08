package com.project.cruise.android.ui.screens.passenger

import com.project.cruise.android.data.network.ApiService

/** Retrofit 2.9 proxies delegate equals to their handler, so api.equals(api) is false.
 * Compose effect keys must compare the service by reference, not proxy equality.
 */
internal class NotificationApiKey(private val api: ApiService) {
    override fun equals(other: Any?): Boolean = other is NotificationApiKey && api === other.api
    override fun hashCode(): Int = System.identityHashCode(api)
}
