package com.project.cruise.android

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.project.cruise.android.ui.screens.GuestScreen
import com.project.cruise.android.ui.screens.auth.LoginScreen
import com.project.cruise.android.ui.screens.auth.OtpScreen
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class OceanAuthUiTest {
    @get:Rule val compose = createComposeRule()

    @Test fun loginPreservesSubmittedValues() {
        var submitted = ""
        compose.setContent { LoginScreen({}, { user, pass -> submitted = "$user:$pass" }) }
        compose.onNodeWithText("Tên đăng nhập").performScrollTo().performTextInput("passenger")
        compose.onNodeWithText("Mật khẩu").performScrollTo().performTextInput("secret")
        compose.onNodeWithText("Đăng nhập  →").performScrollTo().performClick()
        compose.runOnIdle { assertEquals("passenger:secret", submitted) }
    }

    @Test fun otpKeepsSixDigitRequirement() {
        var submitted = ""
        compose.setContent { OtpScreen(7, {}, { id, otp -> submitted = "$id:$otp" }) }
        compose.onNodeWithText("Xác thực  ✓").performScrollTo().assertIsNotEnabled()
        compose.onNodeWithText("Mã OTP xác thực").performScrollTo().performTextInput("123456")
        compose.onNodeWithText("Xác thực  ✓").performScrollTo().performClick()
        compose.runOnIdle { assertEquals("7:123456", submitted) }
    }

    @Test fun guestKeepsPosEntry() {
        var opened = false
        compose.setContent { GuestScreen({}, {}, { opened = true }) }
        compose.onNodeWithText("Chế độ máy POS").performScrollTo().performClick()
        compose.runOnIdle { assertEquals(true, opened) }
    }
}
