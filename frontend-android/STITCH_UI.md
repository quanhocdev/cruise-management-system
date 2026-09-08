# UI Cruise Management từ thiết kế Stitch

Nguồn: `stitch_cruise_passenger_ui_redesign.zip` do người dùng cung cấp.
Chuyển sang Compose native, không chạy HTML/JavaScript hoặc lấy dữ liệu mẫu làm dữ liệu app.

## Phạm vi

- Màn chào, đăng nhập, đăng ký, OTP.
- Dashboard Passenger, nút số thông báo chưa đọc.
- Danh sách và chi tiết thông báo.
- Bộ thành phần dùng chung trong `OceanUi.kt`: nền #FAF8FF, navy #0B2545,
  teal #006A69, mint #98F2F0, card trắng/lavender và bo góc 16–24dp.

Theme được giới hạn vào các màn trên. POS và các màn booking/tour chưa có mẫu
trong ZIP không bị thay theme toàn cục. Dùng font hệ thống sans-serif hỗ trợ tiếng
Việt thay vì tải Inter từ mạng. Biểu tượng tàu vẽ bằng Canvas, không phụ thuộc URL.

## Khác biệt có chủ đích với ảnh Stitch

- Loại bỏ bảng mô phỏng loading/error/success: app dùng trạng thái thật.
- Không thêm bottom navigation lịch trình/phòng/dịch vụ ngoài luồng booking cũ.
- Không dùng tên khách, số chưa đọc, phiên bản app và ảnh du thuyền minh họa.
- Không thêm mô tả mở khóa phòng, chuẩn an ninh quốc tế hoặc mật khẩu tối thiểu
  từ mẫu khi chưa khớp chức năng/quy tắc backend.
- OTP vẫn dùng một ô nhập hỗ trợ dán mã thay vì sáu ô độc lập.
- Các callback, API và điều kiện gửi OTP giữ nguyên; mật khẩu được che và có nút hiện/ẩn.

## Kiểm tra giao diện

Preview màn chào/login/register/OTP có trong `OceanPreviews.kt`, gồm màn 360dp
và fontScale 1.5. Preview chỉ chứa dữ liệu thiết kế và không gọi API.

Trên thiết bị: kiểm tra màn nhỏ, xoay màn, bàn phím mở, chữ lớn; thử đăng nhập lỗi,
đăng ký → OTP, đường vào POS, đăng xuất, Dashboard và các trạng thái thông báo.
Không cần thay backend hoặc database để dùng UI mới.
