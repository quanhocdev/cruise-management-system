# POS: nhận diện hành khách (mô phỏng)

Phạm vi: nhận diện QR/NFC theo hành khách của một chuyến. Không thu tiền,
không đổi trạng thái check-in, không tự cấp quyền sử dụng dịch vụ.
`SYNCED` của hàng đợi vẫn chỉ có nghĩa máy chủ đã nhận bản ghi quét.

## Chuẩn bị

- Booking đã `CONFIRMED`, hành khách có trạng thái `REGISTERED`.
- Máy đã đăng ký qua `POST /api/admin/pos-terminals`; dùng key đã cấp.
- Chạy lại booking-service và gateway-service với code mới, build lại Android.
- Gọi các API dưới đây qua Gateway `http://localhost:8080`.
- API `/api/admin/pos-terminals/**` yêu cầu JWT/cookie ADMIN.
  Không gửi JWT hoặc secret lên Git/ảnh chụp. Không tự đặt ID giả.

### 1. Gán máy vào chuyến

Lấy `voyageId` và `passengers[].passengerVoyageId` từ response booking
(`GET /api/v1/bookings/{bookingId}` bằng chủ booking hoặc Admin).

`PUT /api/admin/pos-terminals/POS-DEMO-001/voyage`

```json
{"voyageId":"UUID_CHUYEN_THUC_TE"}
```

Thành công: HTTP 204. Chuyến phải có hành khách booking trong hệ thống.

### 2. Cấp QR riêng cho một hành khách

`POST /api/admin/pos-terminals/credentials`

```json
{"passengerVoyageId":7,"scanType":"QR"}
```

Thay `7` bằng passengerVoyageId thực tế. Response HTTP 201 có `id`,
`passengerVoyageId`, `scanType`, `scannedValue`. Dùng **nguyên scannedValue**
để tạo hình QR bằng công cụ offline. QR bắt đầu bằng `POS:` và có mã ngẫu nhiên;
backend chỉ lưu fingerprint SHA-256, không lưu chuỗi QR gốc.
Không dùng `BOOKING:CR...` hoặc số booking để thay thế.
Chưa có màn Admin cấp mã/in QR ở giai đoạn này.

### 3. Gắn thẻ NFC

Đọc UID của thẻ bằng điện thoại rồi gọi cùng endpoint:

```json
{"passengerVoyageId":7,"scanType":"NFC","nfcUid":"04A1B2C3"}
```

UID mẫu chỉ minh họa: thay bằng UID thật dài 4, 7 hoặc 10 byte hex.
Thẻ đã được đăng ký (kể cả đã khóa) không được gán lại trong phiên bản này.
UID không phải bí mật và có thể bị clone. Không dùng nó để xác thực thanh toán thật.

### 4. Khóa thẻ/mã

`PATCH /api/admin/pos-terminals/credentials/{id}/revoke` → HTTP 204.
Lưu ID trả về lúc cấp mã để thu hồi khi mất thẻ. Không có API mở khóa ở giai đoạn này.

## Kiểm tra trên POS Android

Quét camera/chạm NFC → lưu Room → mở màn nhận diện. Endpoint:

`POST /api/v1/pos/identify`

Headers: `X-Terminal-Code`, `X-POS-Key` của máy.

```json
{"scanType":"QR","scannedValue":"POS:MA_NGAU_NHIEN_DA_CAP"}
```

- Đúng mã, đúng chuyến, booking xác nhận: `IDENTIFIED`, tên, mã booking,
  ID chuyến/phòng và trạng thái lên tàu. Không trả email/điện thoại/ngày sinh.
- Sai/khóa/chưa xác nhận/khác chuyến: `REJECTED` với reason, không trả danh tính.
- Máy chưa gán chuyến: `TERMINAL_NOT_ASSIGNED`.
- Sai key hoặc máy bị vô hiệu hóa: HTTP 401.
- Mất mạng: Android giữ bản ghi quét nhưng không xác nhận danh tính offline.
  Khi có mạng, mở lịch sử → Xác minh hành khách hoặc bấm Xác minh lại.
  WorkManager gửi bản ghi tự động, còn xác minh hiện do người dùng yêu cầu.

## Dữ liệu mới và giới hạn

- Bảng `pos_passenger_credentials`: fingerprint duy nhất, loại quét,
  passenger_voyage_id, active, created_at.
- Cột nullable `pos_terminals.assigned_voyage_id`; máy cũ phải được gán chuyến.
- Môi trường dev hiện dùng Hibernate `ddl-auto=update`: bổ sung bảng/cột,
  không yêu cầu xóa database hay volume. Production cần migration được rà riêng.
- Đây là bước nhận diện; chưa có giỏ hàng, thu tiền, check-in, hoàn/hủy giao dịch,
  chứng thực thẻ chống sao chép, quản lý ca hay xác minh danh tính offline.
- Chưa hỗ trợ thu hồi/cấp lại key máy POS và tái sử dụng thẻ cho chuyến mới.

## Kiểm thử

Backend: `mvn test` ở booking-service và gateway-service.
Android: `gradlew.bat assembleDebug testDebugUnitTest`.
Test thiết bị: mã đúng, mã booking cũ, thẻ lạ, khóa thẻ, gán sai chuyến,
booking chưa xác nhận, tắt mạng rồi xác minh lại. Test tự động không thay thế
kiểm tra camera/NFC thật và cấu hình Docker/database trên máy triển khai.
