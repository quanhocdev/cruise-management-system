# Dữ liệu du thuyền SEA

Bộ dữ liệu mô phỏng cho web và Android, bổ sung độc lập với bộ FLOW trước đó. Tên người, tàu, giá và chương trình là dữ liệu test; không phải tour thương mại thực tế.

## Chạy

Từ thư mục gốc repository, khi Docker đang chạy:

```powershell
powershell -ExecutionPolicy Bypass -File scripts/coastal-demo/seed.ps1
powershell -ExecutionPolicy Bypass -File scripts/coastal-demo/verify.ps1
```

Seed chỉ thêm bản ghi chưa tồn tại; không reset trạng thái đã thao tác. Ngày khởi hành tính theo ngày nạp lần đầu ở múi giờ Việt Nam. Chạy khi không có người khác đang đặt tour: bước cuối tính lại tồn kho Redis theo số phòng vật lý trừ mọi booking không bị hủy. Mỗi tàu có 8 phòng mỗi hạng, không đặt tùy ý 10 phòng như bộ FLOW cũ. Chạy lại không tự gia hạn ngày tour.

Không chạy cleanup FLOW để xóa SEA. Bộ này không cung cấp lệnh xóa hàng loạt vì booking mới tạo bằng API có thể tham chiếu vào dữ liệu SEA.

## Tour

| Mã | Hành trình | Trạng thái / mục đích |
|---|---|---|
| SEA-01 | Hạ Long, 3 ngày 2 đêm | READY / OPEN — đặt tour, check-in |
| SEA-02 | Lan Hạ, 3 ngày 2 đêm | READY / OPEN |
| SEA-03 | Bái Tử Long, 3 ngày 2 đêm | READY / OPEN |
| SEA-04 | Đà Nẵng, 3 ngày 2 đêm | READY / OPEN |
| SEA-05 | Nha Trang, 3 ngày 2 đêm | READY / OPEN |
| SEA-06 | Phú Quốc, 3 ngày 2 đêm | READY / OPEN |
| SEA-07 | Hạ Long | APPROVAL_PENDING — Operation duyệt chuyến |
| SEA-08 | Lan Hạ | APPROVED — cấu hình sản phẩm, dịch vụ, hoạt động đang WAITING_CONFIG |
| SEA-09 | Hạ Long | IN_PROGRESS / CLOSED — khách đã check-in, NFC đang gán |
| SEA-10 | Phú Quốc | COMPLETED / CLOSED — xem lịch sử, khách đã checkout |

Mỗi tour có 3 gói Deluxe/Suite/Family cho 2/3/4 người một phòng; 3 ngày lịch trình, 3 điểm dừng và 3 chương trình tham quan; 12 sản phẩm, 8 dịch vụ, 3 hoạt động trên tàu. Mỗi gói có 4 quyền lợi tham chiếu sản phẩm, dịch vụ, hoạt động trên tàu và tham quan bờ. Giá booking bằng giá gói nhân số phòng.

## Dữ liệu theo vai trò

| Role | Tài khoản demo | Dữ liệu để test |
|---|---|---|
| ADMIN | demo.admin | 10 tàu, 30 tầng, 60 khu vực, 240 phòng, 3 hạng phòng, 10 điểm cảng, 200 vòng NFC, danh mục sản phẩm/dịch vụ và tài khoản nhân viên |
| SCHEDULER | demo.scheduler | 10 tour, 30 ngày lịch trình, 30 điểm dừng, trạng thái chờ duyệt đến hoàn tất |
| OPERATION | demo.operation | SEA-07 chờ duyệt; SEA-08 chờ cấu hình; 30 gói và 120 quyền lợi |
| FINANCE | demo.finance | 80 booking, 160 hành khách; chưa thanh toán, đã xác nhận, hủy, đã check-in, đã checkout |
| CONVENIENCE | demo.convenience | 12 sản phẩm, 8 dịch vụ; 120 phân bổ sản phẩm, 80 phân bổ dịch vụ; SEA-08 chờ cấu hình |
| ONBOARD | demo.onboard | 3 hoạt động gốc, 30 lịch hoạt động trên tàu, QR booking mẫu |
| SHORE | demo.shore | 30 chương trình tham quan gắn đúng điểm dừng và giờ trong lịch trình |
| PASSENGER | demo.passenger | 6 tour công khai mở bán, 80 booking mẫu của chính tài khoản, thông tin gói đầy đủ |

Mật khẩu các tài khoản demo kế thừa từ tài khoản cùng role hiện có lúc nạp lần đầu. Trên máy hiện tại đã kiểm tra cả 8 tài khoản bằng `admin@123`. Không đổi mật khẩu tài khoản cũ.

80 bản ghi thanh toán mô phỏng khớp số tiền booking, gồm SUCCESS/PENDING/FAILED/REFUNDED. Đây là fixture database, không phải giao dịch VNPay thực; muốn kiểm tra cổng thanh toán cần tạo booking mới và dùng cấu hình sandbox hợp lệ.

## Mã dùng ngay

- `SEA-BOOK-001`: khách Hạ Long đã xác nhận, chờ check-in. Booking ID `-950001`, passenger IDs `-951001`, `-951002`; phòng Deluxe `SEA-1-01`, NFC `SEA-NFC-001` và `SEA-NFC-002` còn trống lúc seed.
- `SEA-BOOK-008`: Hạ Long chờ thanh toán.
- `SEA-BOOK-010`: Hạ Long đã hủy.
- `SEA-BOOK-061` đến `SEA-BOOK-070`: chuyến SEA-09, đã check-in, vòng `SEA-NFC-121` đến `SEA-NFC-140` đã gán.
- `SEA-BOOK-071` đến `SEA-BOOK-080`: chuyến SEA-10, đã checkout, vòng đã được tháo khỏi hồ sơ.
- `SEA-NFC-191` đến `SEA-NFC-200`: vòng INACTIVE.

ID âm dành riêng cho fixture để không làm lệch sequence tạo booking/hành khách thật. UUID dùng MD5 của chuỗi `coastal-v1-{loại}-{số}` để chạy lặp không trùng. Dữ liệu hiện hữu được giữ nguyên.

Backend hiện vẫn chưa có đầy đủ API nhận diện NFC, xác nhận boarding và xác nhận quyền tham quan theo QR. Seed hỗ trợ danh mục và trạng thái sẵn có; không thay thế các API còn thiếu. Android cũng chỉ hiển thị những phần màn hình đã được triển khai.

## Đã kiểm tra

- Cả 8 role đăng nhập và đọc endpoint tương ứng thành công.
- 6 tour mở bán đều có đủ gói, lịch trình, sản phẩm, dịch vụ, hoạt động và quyền lợi qua API public.
- Tạo booking thật bằng multipart API cho SEA-01 thành công: `BK-4EF4977A`, trạng thái PENDING_PAYMENT, 1 phòng Deluxe, 5.000.000 đồng. Booking này giữ lại trong tài khoản demo.passenger để kiểm tra tiếp.
