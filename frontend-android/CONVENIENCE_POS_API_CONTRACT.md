# Contract còn thiếu cho Convenience POS

Ngày kiểm tra: 29/09/2026. Đây là đề xuất để partner backend review; Android không sửa backend trong nhánh này.

## API backend hiện có và Android đã sử dụng

- `GET /api/convenience/products`: đọc danh mục sản phẩm đang hoạt động.
- `GET /api/convenience/services`: đọc danh mục dịch vụ đang hoạt động.

Hai API trên trả danh mục chung. Chưa thấy API tra hành khách từ `nfcCardUid`, chưa có dữ liệu quyền lợi theo tour/package và chưa có API ghi nhận sử dụng.

## 1. Nhận diện hành khách qua NFC

Đề xuất:

```http
GET /api/convenience/passengers/by-nfc/{nfcUid}
Authorization: Bearer <CONVENIENCE JWT>
```

Response tối thiểu:

```json
{
  "passengerId": 123,
  "bookingPassengerId": 456,
  "fullName": "Nguyễn Văn A",
  "roomCode": "A-204",
  "tourId": "uuid",
  "tourName": "Hạ Long 3N2Đ",
  "bookingStatus": "CONFIRMED",
  "passengerStatus": "CHECKED_IN",
  "nfcStatus": "ASSIGNED"
}
```

Backend phải từ chối: UID không tồn tại/chưa gán, khách chưa check-in, booking không còn hiệu lực, vòng bị khóa và nhân viên không có quyền trên chuyến. Không trả số giấy tờ hoặc dữ liệu cá nhân không cần cho quầy tiện ích.

## 2. Danh mục theo chuyến và quyền lợi

Danh mục hiện tại là danh mục chung. POS cần danh mục áp dụng cho `tourId`, kèm thông tin khách được dùng trong gói hay phát sinh phí.

```http
GET /api/convenience/tours/{tourId}/catalog?bookingPassengerId=456
```

Mỗi mục nên có `itemType`, `itemId`, `name`, `price`, `available`, `includedQuantity`, `usedQuantity`, `remainingQuantity` và lý do khi không khả dụng. Không dùng `stockQuantity` cấu hình chung làm số tồn thực tế nếu backend chưa quản lý tồn kho giao dịch.

## 3. Ghi nhận sử dụng

```http
POST /api/convenience/usages
Idempotency-Key: <UUID do Android tạo>
```

```json
{
  "bookingPassengerId": 456,
  "tourId": "uuid",
  "nfcUid": "04A1B2C3D4",
  "items": [
    { "itemType": "SERVICE", "itemId": "uuid", "quantity": 1 }
  ],
  "terminalCode": "POS-001",
  "occurredAt": "2026-09-29T10:30:00+07:00"
}
```

Response cần trả mã giao dịch, từng khoản được tính trong gói hay phát sinh, đơn giá do server chốt, tổng phát sinh và trạng thái xác nhận. Backend phải chống gửi lặp bằng `Idempotency-Key`; client không tự quyết giá, quyền lợi hoặc trừ tồn.

## Quy tắc offline

Khi chưa xác minh được với server, Android chỉ lưu “lượt đọc NFC chưa xác nhận”. Không hiển thị tên khách từ cache cũ, không cho ghi nhận quyền lợi hoặc báo sử dụng thành công. Nếu sau này cho phép offline, backend cần định nghĩa snapshot quyền lợi, thời hạn và cách xử lý xung đột.
