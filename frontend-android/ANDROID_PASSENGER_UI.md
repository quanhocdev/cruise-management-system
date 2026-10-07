# Giao diện hành khách Android

Các màn đang dùng trong navigation đã được nâng cấp cùng bảng màu navy, teal và nền sáng của OceanCruise:

- Khám phá tour: ảnh du thuyền từ API, ảnh minh họa dự phòng, tìm theo tên/mã/du thuyền và lọc tour mở bán.
- Chi tiết tour: gói phòng, giá, quyền lợi, lịch trình, điểm dừng, hoạt động, sản phẩm và dịch vụ từ public tour API. Chỉ cho chọn gói khi trạng thái mở bán là `OPEN`.
- Đặt chỗ: nhóm thông tin liên hệ, số phòng, hành khách và tổng tạm tính. Giữ nguyên kiểm tra dữ liệu, chống gửi lặp và xử lý kết quả chưa xác định của luồng hiện có.
- Chuyến đi: tìm booking, lọc trạng thái, số lượng booking thực tế và chi tiết hành khách.
- Tài khoản: thông tin từ API, lối vào tour/booking, thử lại khi tải lỗi và xác nhận đăng xuất.
- Thanh điều hướng: Khám phá / Chuyến đi / Tài khoản. Thay nút thông báo chưa có route hoạt động.

Danh sách dài dùng `LazyColumn`; ngày và trạng thái được trình bày bằng tiếng Việt. Giá chưa được cung cấp không bị hiển thị thành 0 đồng. Tổng giá trị booking không được gọi là tiền đã thanh toán. Không tạo QR hay trạng thái check-in giả.

Giữ giao diện đăng nhập, đăng ký, OTP, màn chào và POS đã hoàn thiện. Không sửa backend, DTO hay cách gọi API; không bổ sung luồng thanh toán mới trong thay đổi giao diện này.

## Kiểm tra

Build với JBR đi kèm Android Studio:

```powershell
$env:JAVA_HOME='E:\Program Files\Android\Android Studio\jbr'
.\gradlew.bat :app:assembleDebug
```

Các test UI mới dùng fixture riêng, không tạo booking trong database. Kiểm tra tìm kiếm, bộ lọc, chọn gói, yêu cầu đăng nhập, trạng thái đóng bán, nhập thông tin, khóa gửi lại, trạng thái thành công, đăng xuất và chữ lớn 1.5 lần trên máy ảo 360dp:

Kết quả ngày 07/10/2026: debug build thành công; 18 test UI mới qua, không có lỗi hoặc test bị bỏ qua. Ảnh các màn đã được kiểm tra trực quan.

```powershell
.\gradlew.bat -I scripts\passenger-ui.init.gradle :app:connectedDebugAndroidTest '-Pandroid.testInstrumentationRunnerArguments.class=com.project.cruise.android.passengerpolish.PassengerPolishUiTest'
```

Script chỉ giới hạn source test cho lần chạy chủ động này. Bộ test passenger cũ còn tham chiếu các màn đã ngừng sử dụng, cần được chuyển đổi riêng; không coi đây là kết quả của toàn bộ instrumentation suite. Lệnh `:app:testDebugUnitTest` cũng chưa biên dịch được các test cũ về catalog, hoạt động và thông báo vì tham chiếu helper đã ngừng sử dụng. Test giao diện không thay cho kiểm thử thanh toán hoặc NFC bằng thiết bị thật.
