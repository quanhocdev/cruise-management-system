# SHORE POS API contract

## Hiện trạng backend được kiểm tra

Android đang sử dụng API có thật:

`GET /api/shore/visit-tours`

API này trả danh sách chuyến tham quan bờ với `id`, `tourId`, `scheduleStopId`,
`name`, `startTime`, `endTime`, `maxPassengers`, `price` và `status`.

Backend chưa có API để:

- nhận diện hành khách từ QR;
- kiểm tra hành khách đã đăng ký đúng chuyến tham quan;
- ghi nhận hành khách rời tàu;
- ghi nhận hành khách quay lại;
- thống kê người đang ở bờ hoặc chưa quay lại.

Android không sửa backend và không coi lượt quét cục bộ là một lần điểm danh.

## Contract đề nghị cho partner

Các API đi qua Gateway và yêu cầu JWT role `SHORE`. QR là chuỗi opaque; Android
không tự tách booking ID hoặc passenger ID.

### 1. Nhận diện khách theo chuyến tham quan

`POST /api/shore/attendance/identify`

```json
{
  "visitTourId": "9006412c-b46d-4510-83dc-61a1b42a1a07",
  "qrValue": "POS:OPAQUE_RANDOM_VALUE",
  "terminalCode": "POS-DEMO-001"
}
```

Response `200`:

```json
{
  "verificationId": "8ba6b08b-1fd4-42d4-a5aa-9e46a8816935",
  "status": "ELIGIBLE_TO_DEPART",
  "passenger": {
    "passengerVoyageId": 7,
    "displayName": "Nguyen Van A",
    "roomCode": "A-203"
  },
  "voyage": {
    "id": "4ac06ec7-60a8-4df6-894c-73f41481cb43",
    "tourName": "Ha Long 3 ngay 2 dem",
    "shipName": "Ocean Star"
  },
  "visitTour": {
    "id": "9006412c-b46d-4510-83dc-61a1b42a1a07",
    "name": "Tham quan dao",
    "startTime": "2026-10-03T08:00:00+07:00",
    "endTime": "2026-10-03T12:00:00+07:00"
  },
  "attendance": {
    "registrationStatus": "REGISTERED",
    "attendanceStatus": "NOT_DEPARTED",
    "departedAt": null,
    "returnedAt": null
  },
  "message": "Khach du dieu kien tham gia"
}
```

`status` cần ổn định và thuộc một trong:

- `ELIGIBLE_TO_DEPART`: đăng ký hợp lệ, chưa rời tàu;
- `ELIGIBLE_TO_RETURN`: đã rời tàu và chưa quay lại;
- `ALREADY_RETURNED`: đã hoàn tất lượt tham quan;
- `NOT_REGISTERED`: không đăng ký chuyến được chọn;
- `NOT_CHECKED_IN`: chưa check-in chuyến tàu chính;
- `WRONG_VOYAGE`: QR không thuộc chuyến tàu của hoạt động;
- `VISIT_NOT_ACTIVE`: chuyến tham quan chưa mở hoặc đã đóng;
- `BOOKING_INVALID`, `QR_REVOKED`, `REJECTED`.

Không trả danh tính khi QR sai, bị khóa hoặc thuộc chuyến khác.

### 2. Ghi nhận rời tàu

`POST /api/shore/attendance/depart`

Header:

```text
Idempotency-Key: <UUID do Android tao khi nhan nut>
```

Body:

```json
{
  "verificationId": "8ba6b08b-1fd4-42d4-a5aa-9e46a8816935",
  "terminalCode": "POS-DEMO-001"
}
```

Response `200` gồm `status: DEPARTED`, `passengerVoyageId`, `visitTourId`,
`departedAt` và `terminalCode`.

### 3. Ghi nhận quay lại

`POST /api/shore/attendance/return`

Request dùng `verificationId`, `terminalCode` và `Idempotency-Key` giống bước
rời tàu. Response `200` gồm `status: RETURNED`, `departedAt` và `returnedAt`.

Backend phải khóa bản ghi điểm danh trong transaction, kiểm tra lại điều kiện và
không ghi đè thời gian đầu tiên. Gửi lại cùng idempotency key phải trả cùng kết
quả. `verificationId` phải hết hạn ngắn và ràng buộc user, terminal, voyage,
visit tour cùng hành khách.

### 4. Thống kê chuyến tham quan

`GET /api/shore/visit-tours/{visitTourId}/attendance-summary`

Response đề nghị:

```json
{
  "registered": 40,
  "departed": 35,
  "returned": 30,
  "notReturned": 5,
  "lastUpdatedAt": "2026-10-03T11:45:00+07:00"
}
```

Danh sách chi tiết người chưa quay lại nên dùng endpoint phân trang riêng và chỉ
trả dữ liệu cần cho vận hành.

## Quy tắc cho Android

- Chỉ bật **Xác nhận khách rời tàu** với `ELIGIBLE_TO_DEPART`.
- Chỉ bật **Xác nhận khách đã quay lại** với `ELIGIBLE_TO_RETURN`.
- Mất mạng không được hiển thị thành công hoặc tự điểm danh offline.
- Lịch sử quét cục bộ không phải bằng chứng khách đã rời tàu/quay lại.
- Không log hoặc hiển thị toàn bộ QR sau khi quét.

Cho tới khi API attendance tồn tại, Android chỉ gọi danh sách visit tour thật,
cho chọn chuyến, che QR, hiển thị `Chờ API` cho hành khách/trạng thái và khóa hai
nút điểm danh.
