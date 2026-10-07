package com.project.cruise.android.passengerpolish

import android.graphics.Bitmap
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.Density
import androidx.test.platform.app.InstrumentationRegistry
import com.project.cruise.android.data.dto.booking.*
import com.project.cruise.android.data.dto.tour.*
import com.project.cruise.android.ui.components.*
import com.project.cruise.android.ui.screens.*
import com.project.cruise.android.ui.screens.passenger.*
import com.project.cruise.android.viewmodel.auth.MeState
import com.project.cruise.android.viewmodel.passenger.*
import com.project.cruise.android.viewmodel.tour.*
import java.io.File
import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class PassengerPolishUiTest {
    @get:Rule val compose = createComposeRule()

    private fun screenshot(name: String) {
        compose.waitForIdle()
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val output = InstrumentationRegistry.getArguments().getString("additionalTestOutputDir")
        val dir =
            (output?.let(::File) ?: File(context.getExternalFilesDir(null), "passenger-ui")).apply {
                mkdirs()
            }
        File(dir, "$name.png").outputStream().use {
            compose
                .onRoot()
                .captureToImage()
                .asAndroidBitmap()
                .compress(Bitmap.CompressFormat.PNG, 100, it)
        }
    }

    private fun scrollTo(text: String) {
        compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText(text))
    }

    private fun tour(id: String, name: String, status: TourBookingStatus = TourBookingStatus.OPEN) =
        PublicTourSummaryResponse(
            id,
            "SEA-$id",
            name,
            "Hành trình khám phá vịnh Hạ Long.",
            "2026-11-20",
            "2026-11-22",
            "Ocean Pearl",
            null,
            status,
            TourStatusTrip.READY,
            null,
            null,
            BigDecimal(2500000),
        )

    private val pkg =
        PublicTourDetailResponse.TourPackageRecord(
            "pkg1",
            "Ocean Suite",
            "Phòng hướng biển, bữa ăn trong hành trình.",
            BigDecimal(2500000),
            2,
            emptyList(),
        )

    private fun detail(status: TourBookingStatus = TourBookingStatus.OPEN) =
        PublicTourDetailResponse(
            "t1",
            "SEA-01",
            "Hạ Long · Hành trình di sản",
            "Ba ngày khám phá vịnh cùng Ocean Pearl.",
            "2026-11-20",
            "2026-11-22",
            status,
            null,
            null,
            PublicTourDetailResponse.CruiseDetailRecord("c1", "Ocean Pearl", "OP", null, 120, null),
            listOf(
                PublicTourDetailResponse.ScheduleDetailRecord(
                    "sc1",
                    "Khám phá vịnh",
                    null,
                    1,
                    "2026-11-20",
                    emptyList(),
                )
            ),
            listOf(pkg),
            emptyList(),
            emptyList(),
            listOf(
                PublicTourDetailResponse.ServiceRecord(
                    "s1",
                    "Spa thư giãn",
                    "Chăm sóc sức khỏe trên biển.",
                    BigDecimal(350000),
                    2,
                    45,
                    null,
                )
            ),
        )

    private fun booking(status: BookingStatus = BookingStatus.PENDING_PAYMENT) =
        BookingResponse(
            1,
            2,
            "hanhkhach.halong.dulich@example.com",
            "t1",
            "pkg1",
            "SEA-BOOKING-001",
            2,
            "Trịnh Quốc Đạt",
            "0901234567",
            BigDecimal(5000000),
            status,
            listOf(
                BookingPassengerInfoResponse(
                    3,
                    "Nguyễn Thị Minh Anh",
                    "FEMALE",
                    "0902345678",
                    null,
                    null,
                    "PENDING",
                    null,
                )
            ),
            "2026-10-07T08:00:00",
            null,
        )

    @Test
    fun catalogSearchAndNavigation() {
        var selected = ""
        compose.setContent {
            TourCatalogContent(
                TourListState.Success(
                    listOf(
                        tour("1", "Hạ Long · Hành trình di sản"),
                        tour("2", "Đà Nẵng · Biển xanh"),
                    )
                ),
                { selected = it },
                {},
                bottomBar = { OceanBottomBar(true, "passenger_tours", {}, {}, {}, {}) },
            )
        }
        screenshot("catalog")
        compose.onNode(hasSetTextAction()).performTextInput("Đà Nẵng")
        scrollTo("Đà Nẵng · Biển xanh")
        compose.onNodeWithText("Đà Nẵng · Biển xanh").performClick()
        compose.runOnIdle { assertEquals("2", selected) }
        compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasSetTextAction())
        compose.onNode(hasSetTextAction()).performTextClearance()
        compose.onNode(hasSetTextAction()).performTextInput("không tồn tại")
        scrollTo("Chưa có hành trình phù hợp")
        compose.onNodeWithText("Xóa bộ lọc").performClick()
        scrollTo("Hạ Long · Hành trình di sản")
        compose.onNodeWithText("Hạ Long · Hành trình di sản").assertIsDisplayed()
    }

    @Test
    fun saleFilterExcludesClosedTours() {
        compose.setContent {
            TourCatalogContent(
                TourListState.Success(
                    listOf(tour("1", "Tour mở"), tour("2", "Tour đóng", TourBookingStatus.CLOSED))
                ),
                {},
                {},
            )
        }
        compose.onAllNodesWithText("Đang mở bán")[0].performClick()
        scrollTo("1 hành trình phù hợp")
        compose.onNodeWithText("1 hành trình phù hợp").assertIsDisplayed()
    }

    @Test
    fun detailPackagesAndRealServices() {
        var selected = ""
        compose.setContent {
            TourDetailContent(
                TourDetailState.Success(detail()),
                true,
                { selected = it },
                {},
                {},
                {},
            )
        }
        screenshot("tour-detail")
        scrollTo("Chọn gói này")
        screenshot("tour-package")
        compose.onNodeWithText("Chọn gói này").performClick()
        compose.runOnIdle { assertEquals("pkg1", selected) }
        scrollTo("Spa thư giãn")
        compose.onNodeWithText("Spa thư giãn").assertIsDisplayed()
        screenshot("tour-services")
    }

    @Test
    fun closedTourDoesNotCreateBooking() {
        compose.setContent {
            TourDetailContent(
                TourDetailState.Success(detail(TourBookingStatus.CLOSED)),
                true,
                {},
                {},
                {},
                {},
            )
        }
        scrollTo("Chọn gói này")
        compose.onNodeWithText("Chọn gói này").assertIsNotEnabled()
    }

    @Test
    fun guestBookingRequiresLogin() {
        var login = false
        compose.setContent {
            TourDetailContent(
                TourDetailState.Success(detail()),
                false,
                {},
                { login = true },
                {},
                {},
            )
        }
        scrollTo("Đăng nhập để đặt tour")
        compose.onNodeWithText("Đăng nhập để đặt tour").performClick()
        compose.runOnIdle { assertEquals(true, login) }
    }

    @Test
    fun bookingsFilterAndOpen() {
        var selected = 0L
        compose.setContent {
            MyBookingsContent(
                BookingListState.Success(listOf(booking())),
                { selected = it },
                {},
                {},
            )
        }
        screenshot("bookings")
        scrollTo("SEA-BOOKING-001")
        screenshot("booking-card")
        compose.onNodeWithText("SEA-BOOKING-001").performClick()
        compose.runOnIdle { assertEquals(1L, selected) }
        scrollTo("Đã hủy")
        compose.onNodeWithText("Đã hủy").performClick()
        scrollTo("Chưa có booking phù hợp")
        compose.onNodeWithText("Chưa có booking phù hợp").assertIsDisplayed()
    }

    @Test
    fun bookingDetailDoesNotClaimPaymentOrCheckin() {
        compose.setContent { MyBookingDetailContent(BookingDetailState.Success(booking()), {}, {}) }
        screenshot("booking-detail")
        scrollTo("hanhkhach.halong.dulich@example.com")
        compose.onNodeWithText("hanhkhach.halong.dulich@example.com").assertIsDisplayed()
        scrollTo("Chưa check-in")
        compose.onNodeWithText("Chưa check-in").assertIsDisplayed()
        screenshot("booking-passengers")
    }

    @Test
    fun profileLogoutConfirmation() {
        var logout = false
        compose.setContent {
            ProfileContent(
                MeState.Success("Trịnh Quốc Đạt", "ROLE_PASSENGER"),
                {},
                {},
                { logout = true },
                {},
            )
        }
        screenshot("profile")
        scrollTo("Đăng xuất")
        compose.onNodeWithText("Đăng xuất").performClick()
        compose.onNodeWithText("Ở lại").performClick()
        compose.runOnIdle { assertEquals(false, logout) }
        compose.onNodeWithText("Đăng xuất").performClick()
        compose.onNode(hasText("Đăng xuất") and hasAnyAncestor(isDialog())).performClick()
        compose.runOnIdle { assertEquals(true, logout) }
    }

    @Test
    fun profileErrorHasRetryInsteadOfInfiniteSpinner() {
        var retry = false
        compose.setContent {
            ProfileContent(
                MeState.Error("Phiên đăng nhập đã hết hạn"),
                {},
                {},
                {},
                { retry = true },
            )
        }
        compose.onNodeWithText("Thử lại").performClick()
        compose.runOnIdle { assertEquals(true, retry) }
        screenshot("profile-error")
    }

    @Test
    fun profileWithLargeText() {
        compose.setContent {
            val density = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(density.density, 1.5f)) {
                ProfileContent(
                    MeState.Success("nguyen.thi.minh.anh.halong", "PASSENGER"),
                    {},
                    {},
                    {},
                    {},
                )
            }
        }
        screenshot("profile-large-text")
        scrollTo("Đăng xuất")
        compose.onNodeWithText("Đăng xuất").assertIsDisplayed()
    }

    @Test
    fun bookingFormKeepsCallbacksAndRoomCount() {
        var draft = BookingDraft()
        compose.setContent {
            var form by remember {
                mutableStateOf(PassengerBookingState(loading = false, tourPackage = pkg))
            }
            CreateBookingScreen(
                form,
                {
                    draft = it
                    form = form.copy(draft = it)
                },
                {},
                {},
                {},
                {},
            )
        }
        screenshot("booking-form")
        compose
            .onAllNodesWithText("Họ và tên *")[0]
            .performScrollTo()
            .performTextInput("Trịnh Quốc Đạt")
        compose.runOnIdle { assertEquals("Trịnh Quốc Đạt", draft.contactName) }
        compose.onNodeWithText("+").performScrollTo().performClick()
        compose.runOnIdle { assertEquals(2, draft.numberOfRooms) }
        compose.onNodeWithText("Kiểm tra đặt chỗ").performScrollTo().assertIsDisplayed()
        screenshot("booking-review")
    }

    @Test
    fun catalogWithLargeTextAndLongTitle() {
        compose.setContent {
            val density = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(density.density, 1.5f)) {
                TourCatalogContent(
                    TourListState.Success(
                        listOf(
                            tour("1", "Hạ Long · Hành trình khám phá di sản thiên nhiên thế giới")
                        )
                    ),
                    {},
                    {},
                )
            }
        }
        scrollTo("Hạ Long · Hành trình khám phá di sản thiên nhiên thế giới")
        screenshot("catalog-large-text")
        scrollTo(passengerMoney(BigDecimal(2500000)))
        compose.onNodeWithText(passengerMoney(BigDecimal(2500000))).assertIsDisplayed()
    }

    @Test
    fun bookingFormWithLargeText() {
        compose.setContent {
            val density = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(density.density, 1.5f)) {
                CreateBookingScreen(
                    PassengerBookingState(loading = false, tourPackage = pkg),
                    {},
                    {},
                    {},
                    {},
                    {},
                )
            }
        }
        compose.onNodeWithText("Giới tính *").performScrollTo().assertIsDisplayed()
        screenshot("booking-form-large-text")
        compose.onNodeWithText("Xác nhận đặt tour").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun navigationUsesWorkingBookingDestination() {
        var booked = false
        var account = false
        compose.setContent {
            OceanBottomBar(true, "passenger_tours", {}, {}, { account = true }, { booked = true })
        }
        compose.onNodeWithText("Chuyến đi").performClick()
        compose.onNodeWithText("Tài khoản").performClick()
        compose.runOnIdle {
            assertEquals(true, booked)
            assertEquals(true, account)
        }
    }

    @Test
    fun unknownSubmissionCannotBeRetriedAccidentally() {
        compose.setContent {
            CreateBookingScreen(
                PassengerBookingState(
                    loading = false,
                    tourPackage = pkg,
                    outcomeUnknown = true,
                    error = "Kiểm tra booking trước khi gửi lại.",
                ),
                {},
                {},
                {},
                {},
                {},
            )
        }
        compose.onNodeWithText("Xác nhận đặt tour").performScrollTo().assertIsNotEnabled()
    }

    @Test
    fun tourWithMissingMetadataRemainsVisible() {
        compose.setContent {
            TourCatalogContent(
                TourListState.Success(
                    listOf(
                        tour("1", "Tour")
                            .copy(
                                name = null,
                                code = null,
                                cruiseName = null,
                                statusBooking = null,
                                startingPrice = null,
                            )
                    )
                ),
                {},
                {},
            )
        }
        scrollTo("Chưa rõ trạng thái")
        compose.onNodeWithText("Chưa rõ trạng thái").assertIsDisplayed()
        scrollTo("Chưa có giá")
        compose.onNodeWithText("Chưa có giá").assertIsDisplayed()
    }

    @Test
    fun bookingWithMissingContactRemainsVisible() {
        compose.setContent {
            MyBookingsContent(
                BookingListState.Success(
                    listOf(booking().copy(bookingCode = null, primaryContactName = null))
                ),
                {},
                {},
                {},
            )
        }
        scrollTo("#1")
        compose.onNodeWithText("#1").assertIsDisplayed()
    }

    @Test
    fun successShowsActualStatus() {
        var done = false
        compose.setContent {
            CreateBookingScreen(
                PassengerBookingState(loading = false, booking = booking(BookingStatus.CONFIRMED)),
                {},
                {},
                {},
                {},
                { done = true },
            )
        }
        compose.onNodeWithText("Đã xác nhận").assertIsDisplayed()
        screenshot("booking-success")
        compose.onNodeWithText("Xem chuyến đi của tôi").performScrollTo().performClick()
        compose.runOnIdle { assertEquals(true, done) }
    }
}
