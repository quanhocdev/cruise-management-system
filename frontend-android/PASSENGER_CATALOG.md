# Xem dịch vụ và sản phẩm theo booking

Trong Android: **Booking của tôi → Chi tiết booking → Dịch vụ & sản phẩm**.
Chọn nhóm, bấm một mục để xem ảnh, mô tả, giá, khu vực và thông tin dịch vụ.
Đây là màn chỉ đọc, không tạo đơn hay giao dịch thanh toán.

## Nguồn dữ liệu

- Dùng trường `catalog` của `GET /api/v1/bookings/{id}/trip-details`.
- Booking-service kiểm tra chủ booking trước khi lấy dữ liệu từ tour-service.
- Tour-service đọc snapshot `assignment_product` và `assignment_service` vốn
  được đồng bộ từ convenience-service. Không mở quyền API nhân viên cho Passenger.
- Ẩn mục `WAITING_CONFIG`, thiếu mã nguồn, thiếu tên hoặc trạng thái không hỗ trợ.
- Giữ thông tin ở các trạng thái đã cấu hình, chưa bắt đầu, đang chạy, kết thúc
  và hết hàng. Trạng thái này là thông tin đồng bộ, không đảm bảo tồn kho trực tiếp.
- Không trả số lượng cấu hình sản phẩm như số lượng còn bán. `maxPassengers`
  chỉ là sức chứa tối đa dịch vụ, không phải chỗ còn trống.

Danh mục rỗng nghĩa là chuyến chưa có mục phù hợp được đồng bộ. Bộ phận vận hành
phải phân công khu vực và bộ phận Convenience cấu hình sản phẩm/dịch vụ cho đúng
tour theo luồng hiện có. Danh mục toàn cục của Admin chưa đủ để xuất hiện ở đây.

## Test tay

1. Cập nhật booking-service và tour-service, cài APK debug mới.
2. Đăng nhập, mở booking của mình và tải lại.
3. Kiểm tra cả hai nhóm; mục có dữ liệu phải hiện đúng tên, giá và mô tả.
4. Ảnh HTTP(S) hợp lệ được tải bằng [Coil](https://github.com/coil-kt/coil/tree/2.7.0).
   URL thiếu/không hỗ trợ hoặc ảnh lỗi có thông báo dự phòng; không gửi JWT tới host ảnh.
5. Kiểm tra danh mục rỗng, mất mạng → tải lại, quay lại danh sách từ chi tiết.
6. Giá thiếu phải hiện “Đang cập nhật”, chỉ giá bằng 0 mới hiện “Miễn phí”.

Không cần thanh toán lại. Các thay đổi lần này không tự thêm dữ liệu mẫu.
