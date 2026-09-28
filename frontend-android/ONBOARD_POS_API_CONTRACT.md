# ONBOARD POS API contract

## Hiện trạng backend được kiểm tra

Backend hiện có các API `/api/onboard/activities-cruise` và
`/api/onboard/activity-cruise-tours` để quản lý danh mục/cấu hình hoạt động trên
tàu. Chưa có controller/service chạy thực tế cho các nghiệp vụ sau:

- nhận diện hành khách từ QR vé;
- trả thông tin hành khách và chuyến tàu cho ONBOARD POS;
- kiểm tra vé có đủ điều kiện lên tàu;
- xác nhận hành khách đã lên tàu.

File `backend/booking-service/POS-IDENTITY.md` có nhắc tới
`/api/v1/pos/identify` và `/api/v1/pos/check-in`, nhưng source hiện tại không có
controller/service tương ứng và Gateway cũng không có route booking-service cho
hai URL này. Android không gọi các URL chỉ tồn tại trong tài liệu.

## Contract đề nghị cho partner

Các endpoint đi qua Gateway, yêu cầu JWT role `ONBOARD`. QR được coi là chuỗi
opaque; client không tách booking ID hoặc passenger ID từ QR.

### 1. Nhận diện và kiểm tra vé

`POST /api/onboard/boarding/identify`

```json
{
  "qrValue": "POS:OPAQUE_RANDOM_VALUE",
  "terminalCode": "POS-DEMO-001"
}
```

Response `200` khi nhận diện được:

```json
{
  "verificationId": "8ba6b08b-1fd4-42d4-a5aa-9e46a8816935",
  "status": "ELIGIBLE",
  "passenger": {
    "passengerVoyageId": 7,
    "displayName": "Nguyen Van A",
    "roomCode": "A-203"
  },
  "voyage": {
    "id": "4ac06ec7-60a8-4df6-894c-73f41481cb43",
    "tourName": "Ha Long 3 ngay 2 dem",
    "shipName": "Ocean Star",
    "departureAt": "2026-10-02T08:00:00+07:00"
  },
  "ticket": {
    "bookingCode": "CR20260001",
    "checkinStatus": "CHECKED_IN",
    "boardingStatus": "NOT_BOARDED",
    "boardedAt": null
  },
  "message": "Ve hop le"
}
```

`status` cần là một trong:

- `ELIGIBLE`: đúng chuyến, booking hợp lệ, đã check-in và chưa lên tàu;
- `ALREADY_BOARDED`: đã xác nhận trước đó, trả `boardedAt` ban đầu;
- `NOT_CHECKED_IN`: chưa hoàn tất check-in;
- `WRONG_VOYAGE`: vé không thuộc chuyến được gán cho POS;
- `BOOKING_INVALID`: booking hủy, hết hiệu lực hoặc chưa xác nhận;
- `QR_REVOKED`: QR đã bị khóa;
- `REJECTED`: mã không tồn tại hoặc không được phép trả danh tính.

Với mã không hợp lệ, không trả tên, phòng hay dữ liệu cá nhân. Có thể dùng HTTP
`404` cho QR không tồn tại, `409` cho trạng thái nghiệp vụ không hợp lệ và `403`
cho POS sai chuyến, nhưng body vẫn cần `status` và `message` ổn định cho client.

### 2. Xác nhận lên tàu

`POST /api/onboard/boarding/confirm`

Header:

```text
Idempotency-Key: <UUID do Android tao cho moi lan nhan nut>
```

Body:

```json
{
  "verificationId": "8ba6b08b-1fd4-42d4-a5aa-9e46a8816935",
  "terminalCode": "POS-DEMO-001"
}
```

Response `200`:

```json
{
  "status": "BOARDED",
  "passengerVoyageId": 7,
  "boardedAt": "2026-10-02T07:42:15+07:00",
  "terminalCode": "POS-DEMO-001",
  "message": "Da xac nhan khach len tau"
}
```

Backend phải khóa bản ghi hành khách trong transaction và kiểm tra lại toàn bộ
điều kiện khi confirm. Gửi lại cùng `Idempotency-Key` phải trả cùng kết quả. Nếu
đã boarding bằng giao dịch khác, trả `ALREADY_BOARDED` cùng thời gian ban đầu,
không ghi đè.

`verificationId` nên hết hạn ngắn (ví dụ 2 phút), ràng buộc với user ONBOARD,
terminal và chuyến. Client phải identify lại khi hết hạn.

## Quy tắc cho Android

- Không hiển thị danh tính từ dữ liệu tự giải mã trong QR.
- Không bật nút xác nhận khi status khác `ELIGIBLE`.
- Mất mạng không được hiển thị thành công và không tự xác nhận boarding offline.
- Lịch sử cục bộ chỉ là lịch sử quét, không phải bằng chứng khách đã lên tàu.
- Không log hoặc hiển thị toàn bộ QR sau khi đã quét.

Cho tới khi hai API trên tồn tại, Android chỉ lưu QR cục bộ, che bớt mã, hiển thị
`Chờ API` cho hành khách/chuyến/trạng thái vé và khóa nút xác nhận. Không có thay
đổi backend nào được thực hiện trong branch Android này.
