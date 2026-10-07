# Cruise POS — giao diện Android

Cập nhật 07/10/2026. Tham chiếu các màn HTML/PNG trong `stitch_cruise_booking_android_app.zip`, triển khai bằng Jetpack Compose native.

## Giao diện

- Bộ màu navy, nền sáng, card trắng; màu riêng Finance teal, Convenience tím, Onboard cam, Shore xanh lá. Bộ icon nét đồng nhất, thay các ký tự QR/NFC và biểu tượng bằng chữ trước đây.
- Dashboard gồm thẻ phiên làm việc, tác vụ chính, số bản ghi thực tế trên thiết bị, hai lượt gần nhất, lịch sử và tài khoản. Tên nhân viên lấy từ phiên đăng nhập; không thêm tên khách, ca làm, tàu, doanh thu hoặc trạng thái online giả.
- Đăng nhập có form, icon, hiện/ẩn mật khẩu, gửi bằng bàn phím; role vẫn do máy chủ quyết định.
- Camera có khung QR, đèn hỗ trợ khi phần cứng có flash, chuyển sang nhập mã. Màn NFC có hướng dẫn trực quan, trạng thái thiết bị, mở cài đặt và nhập UID trong bản debug Convenience.
- Màn kết quả phân biệt gửi booking, chỉ lưu UID, đang xử lý và chưa xác nhận. Không báo hoàn tất check-in chỉ vì gửi QR thành công.
- Convenience có tìm kiếm sản phẩm/dịch vụ, giá, mô tả và metadata từ API danh mục hiện hữu. Đọc NFC và xem lại từ lịch sử mở đúng màn Convenience.
- Onboard có thẻ vé, thông tin đối chiếu chưa xác minh và nút xác nhận bị khóa khi chưa có nghiệp vụ hỗ trợ.
- Shore có tìm kiếm, chọn chuyến, thẻ chuyến đang đối chiếu, giờ dễ đọc và trạng thái đi/về chưa xác minh. Chọn chuyến không mở khóa xác nhận điểm danh.
- Lịch sử có tìm mã, lọc, thời gian, trạng thái và đường tới chi tiết theo role. QR định danh được che mã.

## Phạm vi nghiệp vụ

Không sửa backend. Convenience chưa tra được hành khách/quyền lợi từ UID; Onboard chưa xác nhận boarding; Shore chưa xác nhận đi/về. Giao diện chỉ trình bày các dữ liệu và trạng thái hiện có, không giả lập thành công cho các nghiệp vụ này.

Chức năng đọc NFC thật và flash vẫn cần kiểm tra trên điện thoại có phần cứng tương ứng. UI test máy ảo không thay thế kiểm thử QR/NFC ngoài thực tế hoặc kiểm thử giao dịch với backend.

## Chạy kiểm tra giao diện POS

Một số test Passenger cũ trong repository tham chiếu lớp đã bỏ nên toàn bộ `androidTest` hiện chưa biên dịch được. Runner dưới đây chỉ lựa chọn thư mục test POS cho lần chạy được yêu cầu; không xóa hoặc vô hiệu hóa test cũ trong cấu hình mặc định.

Từ thư mục `frontend-android`, dùng JDK của Android Studio và máy ảo đã kết nối ADB:

```powershell
.\gradlew.bat :app:assembleDebug
.\gradlew.bat -I scripts\pos-ui.init.gradle :app:connectedDebugAndroidTest '-Pandroid.testInstrumentationRunnerArguments.class=com.project.cruise.android.pos.PosPolishUiTest'
```

`PosPolishUiTest` kiểm tra tác vụ cả bốn role, lịch sử/tài khoản/đăng xuất, đăng nhập, tìm kiếm/tab tiện ích, chọn tham quan vẫn khóa điểm danh, kết quả Finance, cỡ chữ 150%, NFC debug và chuyển camera sang nhập mã. Ảnh dùng fixture nằm trong test, không đưa dữ liệu mẫu vào app vận hành.

Android test runner thu ảnh vào `app/build/outputs/connected_android_test_additional_output/`. Báo cáo test nằm tại `app/build/reports/androidTests/connected/debug/`.

## Kết quả kiểm tra 07/10/2026

- APK debug và APK androidTest build thành công.
- 14/14 test POS pass trên Android 16/API 36, 1080×2400 px, density 480 (rộng 360dp); kiểm tra thêm font scale 150% trong test Finance.
- Đã xem ảnh dashboard bốn role, đăng nhập, danh mục Convenience, Shore, NFC, camera, lịch sử và kết quả để kiểm tra bố cục.
- `git diff --check` không có lỗi whitespace. Chưa kiểm thử NFC phần cứng, flash thật hoặc xác nhận giao dịch với backend trong đợt chỉnh giao diện này.
