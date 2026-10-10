package com.project.cruise.android.navigation

import android.net.Uri
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.project.cruise.android.ui.screens.pos.PosRole

// =================================================
// ROLE
// =================================================

/** Argument "role" dùng chung cho các route POS. */
internal val posRoleArguments: List<NamedNavArgument> = listOf(
    navArgument("role") { type = NavType.StringType }
)

/** Đọc [PosRole] từ argument "role". Trả về null nếu không hợp lệ. */
internal fun NavBackStackEntry.posRole(): PosRole? =
    PosRole.fromApiRole(arguments?.getString("role"))

// =================================================
// USAGE (convenience / onboard / shore)
// =================================================

/** Phần path giống nhau của 3 route usage. */
internal const val USAGE_PATH =
    "{bookingPassengerId}/{passengerName}/{bookingId}/{tourId}/{tourPackageId}/{nfcCardUid}"

internal val usageArguments: List<NamedNavArgument> = listOf(
    navArgument("bookingPassengerId") { type = NavType.LongType },
    navArgument("passengerName") { type = NavType.StringType },
    navArgument("bookingId") { type = NavType.LongType },
    navArgument("tourId") { type = NavType.StringType },
    navArgument("tourPackageId") { type = NavType.StringType },
    navArgument("nfcCardUid") { type = NavType.StringType },
)

internal data class UsageArgs(
    val bookingPassengerId: Long,
    val passengerName: String,
    val bookingId: Long,
    val tourId: String,
    val tourPackageId: String,
    val nfcCardUid: String
)

internal fun NavBackStackEntry.toUsageArgs(): UsageArgs? {
    val a = arguments ?: return null
    return UsageArgs(
        bookingPassengerId = a.getLong("bookingPassengerId"),
        passengerName = a.getString("passengerName") ?: "",
        bookingId = a.getLong("bookingId"),
        tourId = a.getString("tourId") ?: return null,
        tourPackageId = a.getString("tourPackageId") ?: return null,
        nfcCardUid = a.getString("nfcCardUid") ?: return null,
    )
}

internal fun buildUsageRoute(
    prefix: String,
    bookingPassengerId: Long,
    passengerName: String?,
    bookingId: Long,
    tourId: String,
    tourPackageId: String,
    nfcCardUid: String
): String = "$prefix/" +
        "$bookingPassengerId/" +
        "${Uri.encode(passengerName ?: "")}/" +
        "$bookingId/" +
        "$tourId/" +
        "$tourPackageId/" +
        Uri.encode(nfcCardUid)