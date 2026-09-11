# Thông báo Passenger Android

Dashboard → Thông báo → chọn thông báo để xem nội dung.

- Số chưa đọc trên Dashboard lấy từ `GET /api/v1/notifications/unread-count`
  khi màn trở lại trạng thái RESUMED. Lỗi tải không hiển thị thành số 0.
- Danh sách lấy từ `GET /api/v1/notifications`, có lọc tất cả/chưa đọc.
- Xem chi tiết không tự đánh dấu đã đọc. Bấm nút để gọi
  `PATCH /api/v1/notifications/{id}/read`.
- Đánh dấu tất cả có xác nhận, gọi `PATCH /api/v1/notifications/read-all`,
  sau đó tải lại danh sách để lấy trạng thái từ server.
- Chỉ hiện nút mở booking khi `referenceType` là `BOOKING` và ID dương.
  Trang booking hiện có vẫn kiểm tra quyền truy cập qua backend.
- Không đoán ID booking từ payment/itinerary; tham chiếu chưa hỗ trợ chỉ xem nội dung.
- State danh sách thuộc màn, không lưu ra đĩa hoặc dùng chung giữa tài khoản.

## Test tay

1. Chạy app mới, đăng nhập Passenger, bấm Thông báo trên Dashboard.
2. Với tài khoản có dữ liệu, kiểm tra tiêu đề, thời gian và nội dung chi tiết.
3. Bấm đánh dấu đã đọc, kiểm tra số chưa đọc giảm sau khi server trả thành công.
4. Lọc chưa đọc; thử xác nhận và hủy thao tác đánh dấu tất cả.
5. Tắt mạng, thử tải lại/đánh dấu: phải báo lỗi, không báo thành công giả.
6. Mở booking liên quan rồi quay lại; không tự mở URL từ nội dung thông báo.
7. Quay Dashboard để cập nhật số chưa đọc, đăng xuất và đổi tài khoản để kiểm tra
   danh sách thuộc đúng tài khoản mới.

Backend không có dữ liệu thì hiển thị danh sách rỗng. Thay đổi này không tạo thông
báo mẫu, không sửa backend, chưa có push notification/FCM khi ứng dụng tắt.
