# Android POS — thiết kế Stitch ngày 28/09/2026

Nguồn: `stitch_cruise_booking_android_app.zip`, gồm 10 màn HTML/PNG. Đã xem ảnh tổng hợp và đối chiếu với các luồng Android hiện có. Chuyển giao diện sang Compose native; không chạy HTML trong WebView.

## Đã triển khai

- Đăng nhập nền sáng, form rõ ràng, chip bộ phận; role vẫn lấy từ kết quả đăng nhập. Không có lựa chọn tự cấp role.
- Bốn dashboard dùng hệ card, màu nhấn riêng, tên tài khoản thật, mã thiết bị; navigation Tác vụ/Lịch sử/Tài khoản và xác nhận đăng xuất.
- Finance dùng F01 làm căn cứ: QR booking, nhập mã và đọc NFC. Checkout chưa triển khai được loại khỏi giao diện hiển thị.
- Convenience, Onboard, Shore ưu tiên đọc NFC và lịch sử, không hiển thị khách, tour, tiền hoặc điểm danh mẫu trong bản thiết kế như dữ liệu thật.
- Màn QR, NFC, nhập mã, lịch sử và kết quả dùng theme POS riêng. Lịch sử có bộ lọc tất cả/chưa xác nhận, tôn trọng safe area và danh sách cuộn.
- Kết quả gửi booking phân biệt với check-in. Kết quả đọc NFC chỉ xác nhận lưu mã trên thiết bị. Nút về tác vụ trở lại dashboard thay vì camera.
- Quay lại từ cài đặt camera/NFC sẽ cập nhật quyền/trạng thái thiết bị. NFC ngừng reader mode khi app không ở foreground.
- Chặn gửi NFC vào endpoint Finance nhận booking code ở cả đường gửi trực tiếp và worker. NFC vẫn được lưu local; chưa có xác minh khách/quyền lợi/chi phí.

## Những phần mẫu chưa đưa vào app

Mẫu ZIP có trạng thái online, tên khách/nhân viên, ca làm, tour, số tiền, VIP, quầy đang kết nối, danh sách điểm danh và cảnh báo khách chưa về. Chưa có nguồn dữ liệu tương ứng cho POS nên không sao chép vào giao diện hoạt động. Không đưa ghi chú Room/WorkManager/API hay các panel mô phỏng lỗi vào màn người dùng.

Ghi nhận sử dụng tiện ích, tham gia Onboard, điểm danh đi/về Shore và checkout độc lập cần phát triển nghiệp vụ/API tiếp. Không coi việc đọc vòng là hoàn tất các nghiệp vụ đó.

## Xác minh

- `:app:assembleDebug`: thành công sau thay đổi cuối cùng.
- `git diff --check`: không có lỗi whitespace.
- Đã xem ảnh nguồn Stitch. Chưa kiểm chứng hình ảnh Compose chạy thật: điện thoại chưa kết nối; thử máy ảo Medium_Phone nhưng ADB báo unauthorized.
- Chưa nghiệm thu QR camera, NFC phần cứng, chuyển role và luồng gửi booking end-to-end trên thiết bị.
- Không chạy lại bộ unit test cũ đang bị lỗi biên dịch ở các test Passenger tham chiếu lớp đã bỏ; build APK không đồng nghĩa unit test đã pass.

## Kiểm tra trên thiết bị tiếp theo

1. Đăng nhập từng role, kiểm tra tên tài khoản, tác vụ và đăng xuất/đổi role.
2. Màn rộng 360dp và cỡ chữ lớn: bàn phím, cuộn, nhãn dài, thanh điều hướng, trạng thái rỗng.
3. Finance: cấp/từ chối quyền camera, trở về từ cài đặt, QR hợp lệ/sai, nhập mã, kết quả và trở lại dashboard.
4. NFC: thiết bị không hỗ trợ, NFC tắt/bật, đọc vòng và kết quả chỉ lưu local; không phát sinh xác nhận giả.
5. Lịch sử: đúng role, bộ lọc, trạng thái gửi, mất mạng. Cơ chế gửi tự động và gửi trực tiếp hiện hữu cần kiểm thử chống gửi trùng trước khi sử dụng thực tế.

Chưa commit hoặc push các thay đổi này.
