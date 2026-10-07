package com.project.cruise.android.pos

import android.graphics.Bitmap
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.Density
import androidx.test.platform.app.InstrumentationRegistry
import com.project.cruise.android.data.network.ConvenienceProductResponse
import com.project.cruise.android.data.network.ConvenienceServiceResponse
import com.project.cruise.android.data.network.ShoreVisitTourResponse
import com.project.cruise.android.data.network.ScanResponse
import com.project.cruise.android.ui.screens.pos.*
import com.project.cruise.android.viewmodel.pos.*
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.io.File

/** Screenshots use explicit UI fixtures, never fake operational data in the app. */
class PosPolishUiTest {
    @get:Rule val compose = createComposeRule()

    private fun screenshot(name: String) {
        compose.waitForIdle()
        val output = InstrumentationRegistry.getArguments().getString("additionalTestOutputDir")
        val dir = (output?.let(::File) ?: File(InstrumentationRegistry.getInstrumentation().targetContext.getExternalFilesDir(null), "pos-ui")).apply { mkdirs() }
        File(dir, "$name.png").outputStream().use { compose.onRoot().captureToImage().asAndroidBitmap().compress(Bitmap.CompressFormat.PNG, 100, it) }
    }

    private fun dashboard(role: PosRole, action: String) {
        var scanned = false
        var history = false
        var loggedOut = false
        compose.setContent { PosDashboardScreen(role, "Trịnh Quốc Đạt", { loggedOut = true }, { scanned = true }, { scanned = true }, {}, { history = true }) }
        screenshot(role.apiRole.lowercase())
        compose.onNodeWithText(action).performScrollTo().performClick()
        compose.runOnIdle { assertEquals(true, scanned) }
        compose.onNodeWithText("Lịch sử", useUnmergedTree = true).performClick()
        compose.runOnIdle { assertEquals(true, history) }
        compose.onNodeWithText("Tài khoản", useUnmergedTree = true).performClick()
        compose.onNodeWithText("Đăng xuất").performScrollTo().performClick()
        compose.onNodeWithText("Kết thúc phiên làm việc?").assertIsDisplayed()
        compose.onNode(hasText("Đăng xuất") and hasAnyAncestor(isDialog())).performClick()
        compose.runOnIdle { assertEquals(true, loggedOut) }
    }

    @Test fun financeActions() = dashboard(PosRole.FINANCE, "Quét QR booking")
    @Test fun convenienceActions() = dashboard(PosRole.CONVENIENCE, "Chạm vòng tay NFC")
    @Test fun onboardActions() = dashboard(PosRole.ONBOARD, "Quét QR vé khách")
    @Test fun shoreActions() = dashboard(PosRole.SHORE, "Quét QR tham quan")

    @Test fun loginKeepsCredentials() {
        var result = ""
        compose.setContent { PosLoginScreen({}, { user, pass -> result = "$user:$pass" }, false, null) }
        screenshot("login")
        compose.onNodeWithText("Đăng nhập máy POS").performScrollTo().assertIsNotEnabled()
        compose.onNodeWithText("Tên đăng nhập").performScrollTo().performTextInput("finance")
        compose.onNodeWithText("Mật khẩu").performScrollTo().performTextInput("example-password")
        compose.onNodeWithText("Đăng nhập máy POS").performScrollTo().performClick()
        compose.runOnIdle { assertEquals("finance:example-password", result) }
    }

    @Test fun convenienceSearchAndTabs() {
        val state = ConveniencePosState(false, "SEA-NFC-001",
            listOf(ConvenienceProductResponse("p1", "Nước khoáng", "Chai 500 ml", 20000.0, 20, null, "ACTIVE")),
            listOf(ConvenienceServiceResponse("s1", "Massage thư giãn", "Chăm sóc tại spa", 350000.0, 45, 4, null)))
        compose.setContent { ConvenienceCustomerScreen(state, {}, {}) }
        screenshot("convenience-catalog")
        compose.onNodeWithText("Nước khoáng").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Dịch vụ · 1").performScrollTo().performClick()
        compose.onNodeWithText("Massage thư giãn").performScrollTo().assertIsDisplayed()
        compose.onNode(hasSetTextAction()).performScrollTo().performTextInput("không tồn tại")
        compose.onNodeWithText("Chưa có dịch vụ phù hợp").performScrollTo().assertIsDisplayed()
    }

    @Test fun shoreSelectionDoesNotEnableAttendance() {
        val visit = ShoreVisitTourResponse("v1", "t1", "stop1", "Khám phá hang Sửng Sốt", "Tham quan cùng hướng dẫn viên tại Hạ Long.", "2026-10-20T09:00:00", "2026-10-20T11:30:00", 40, 350000.0, "ACTIVE")
        compose.setContent { ShoreVisitScreen(ShoreVisitState(false, "SEA-BOOKING-001", listOf(visit)), {}, {}) }
        screenshot("shore-visits")
        compose.onNodeWithText(visit.name).performScrollTo().performClick()
        compose.onNodeWithText("CHUYẾN ĐANG ĐỐI CHIẾU").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Xác nhận khách rời tàu").performScrollTo().assertIsNotEnabled()
        compose.onNodeWithText("Xác nhận khách đã trở về").performScrollTo().assertIsNotEnabled()
    }

    @Test fun onboardRemainsUnverified() {
        compose.setContent { OnboardTicketScreen(OnboardTicketState(false, "SEA-BOOKING-001"), {}) }
        screenshot("onboard-ticket")
        compose.onNodeWithText("Chưa xác minh").assertIsDisplayed()
        compose.onNodeWithText("Xác nhận khách lên tàu").performScrollTo().assertIsNotEnabled()
    }

    @Test fun financeErrorAllowsReviewBeforeRetry() {
        var retried = false
        compose.setContent { PosIdentityScreen(PosRole.FINANCE, PosScanState(error = "Mất kết nối. Kiểm tra kết quả trước khi gửi lại."), { retried = true }, {}) }
        screenshot("finance-error")
        compose.onNodeWithText("Thử lại sau khi kiểm tra").performScrollTo().performClick()
        compose.runOnIdle { assertEquals(true, retried) }
    }

    @Test fun financeSuccessDoesNotClaimCheckIn() {
        compose.setContent { PosIdentityScreen(PosRole.FINANCE, PosScanState(response = ScanResponse(true, "Đã nhận mã", 950001)), {}, {}) }
        screenshot("finance-success")
        compose.onNodeWithText("Đã gửi mã booking").assertIsDisplayed()
        compose.onNodeWithText("Gửi mã thành công chưa phải hoàn tất check-in.").performScrollTo().assertIsDisplayed()
    }

    @Test fun qrOffersManualFallback() {
        var manual = false
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        instrumentation.uiAutomation.grantRuntimePermission(instrumentation.targetContext.packageName, android.Manifest.permission.CAMERA)
        compose.setContent { QrScanScreen(PosRole.FINANCE, {}, {}, { manual = true }) }
        screenshot("qr")
        compose.onNodeWithText("Nhập mã thay thế").performScrollTo().performClick()
        compose.runOnIdle { assertEquals(true, manual) }
    }

    @Test fun largeTextStillReachesPrimaryAction() {
        var scanned = false
        compose.setContent {
            val density = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(density.density, 1.5f)) {
                FinancePosDashboardScreen("Nhân viên lễ tân với tên dài", {}, { scanned = true }, {}, {}, {})
            }
        }
        compose.onNodeWithText("Quét QR booking").performScrollTo().performClick()
        screenshot("finance-large-text")
        compose.runOnIdle { assertEquals(true, scanned) }
    }

    @Test fun nfcDebugScreen() {
        compose.setContent { NfcScanScreen(PosRole.CONVENIENCE, {}, {}) }
        screenshot("nfc")
        compose.onNodeWithText("Đọc UID giả lập").performScrollTo().assertIsNotEnabled()
    }

    @Test fun historyEmptyScreen() {
        compose.setContent { PosHistoryScreen(PosRole.SHORE, {}, {}) }
        screenshot("history")
        compose.onNodeWithText("Chưa có thao tác").performScrollTo().assertIsDisplayed()
    }
}
