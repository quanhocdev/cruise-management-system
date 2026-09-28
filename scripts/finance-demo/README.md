# Finance demo local

Bộ dữ liệu giả lập, tạo ngày 28/09/2026. Không chạy seed trên database dùng chung/production.

## Dữ liệu

- Tour: **DEMO - Finance QR va NFC** (`DEMO-POS-2026`).
- Tàu: DEMO - Tau kiem thu POS; gói phòng đôi, sức chứa 2.
- `DEMO-POS-001`: khách Demo 1, đã check-in trong kiểm thử, phòng DEMO-101 / vòng DEMO-NFC-001.
- `DEMO-POS-002`: khách Demo 2, giữ trạng thái PENDING để thao tác bằng Android/web, chọn phòng DEMO-102 / vòng DEMO-NFC-002.
- Mã NFC là giá trị giả lập trong catalog; không phải UID của một vòng vật lý. Chưa nghiệm thu đọc NFC phần cứng.
- Booking được seed ở CONFIRMED để kiểm thử Finance; không phải bằng chứng đã chạy đặt chỗ/thanh toán VNPay.
- IDs UUID cố định trong `ids.json`; ID số âm dành riêng demo, không tác động sequence tạo dữ liệu bình thường.
- Seed chỉ INSERT ON CONFLICT DO NOTHING, không reset check-in hay ghi đè dữ liệu hiện có.

## Thử trực tiếp

1. Mở `http://localhost:5173/finance/check-in`, đăng nhập `finance` / `admin@123` (tài khoản local).
2. Chọn tour DEMO - Finance QR va NFC, chờ trạng thái realtime đã kết nối.
3. Trên Android, đăng nhập POS bằng Finance. Với máy ảo đang dùng localhost, chạy `adb reverse tcp:8080 tcp:8080` nếu vừa khởi động lại máy ảo.
4. Chọn Nhập mã booking, nhập **DEMO-POS-002** rồi gửi. Có thể dùng camera thật quét ảnh `finance-demo-002.png`; camera máy ảo chưa được nghiệm thu.
5. Web nhận mã và tải khách Demo 2. Nhấn Check-in, chọn DEMO-102 và DEMO-NFC-002, xác nhận.
6. Khách phải hiển thị CHECKED_IN; danh sách hành khách lưu roomId, nfcCardUid, checkedInAt. Android chỉ xác nhận đã gửi mã, không giả lập thông báo hoàn tất check-in.

## Tái tạo trên môi trường local khác

Từ thư mục gốc repository, sau khi backend đã tạo schema:

```powershell
Get-Content -Raw scripts/finance-demo/tour.sql | docker compose exec -T postgres-tour psql -v ON_ERROR_STOP=1 -U postgres -d tour_service_db
Get-Content -Raw scripts/finance-demo/booking.sql | docker compose exec -T postgres-booking psql -v ON_ERROR_STOP=1 -U postgres -d booking_service_db
node scripts/finance-demo/verify.cjs
```

Script verify cần Node có global fetch/WebSocket, tài khoản Finance và Convenience local. Có thể cung cấp mật khẩu bằng biến môi trường DEMO_PASSWORD. Không in token ra log. Script chủ động check-in booking 001; không hoàn tất booking 002. Sau khi tự check-in booking 002, một số kiểm thử âm của script không còn phù hợp trạng thái fixture ban đầu.

## Đã kiểm chứng

- Finance login, đọc tour/booking và API qua gateway 8080.
- Gửi mã QR qua HTTP → nhận đúng booking trên STOMP/WebSocket qua gateway.
- Check-in → đọc lại đúng trạng thái, phòng và NFC trong API hành khách.
- Gửi lặp cùng yêu cầu giữ nguyên thời điểm check-in; thay phòng/vòng sau check-in bị từ chối.
- Thiếu trường bị từ chối; vòng đã dùng, vòng không tồn tại và phòng của booking khác bị từ chối.
- Danh sách vòng trên form check-in loại vòng đã liên kết với khách CHECKED_IN.
- Convenience không được gọi API scan của Finance.
- Maven test lifecycle của booking và compile tour thành công; web production build thành công (còn cảnh báo bundle lớn). Kiểm chứng hành vi dựa trên verify.cjs; không khẳng định có bộ unit test mới.

## Giới hạn còn lại

- Đây là kiểm thử API và kênh realtime, chưa phải thao tác UI tự động trên Android/web, chưa kiểm tra vòng NFC thật.
- Room endpoint trả danh sách phòng đúng loại/đúng tàu; server check-in kiểm tra xung đột phòng, số phòng booking và sức chứa tại thời điểm xác nhận. Form chưa loại sẵn mọi phòng đang được booking khác sử dụng.
- Tình trạng vòng đang được khách sử dụng được xác định từ booking_passengers. Chưa đồng bộ enum ASSIGNED trong catalog tour.nfc_cards; form check-in dùng endpoint tổng hợp để loại vòng đã dùng.
- Kiểm tra đồng thời dùng khóa giao dịch PostgreSQL chung cho check-in; phù hợp demo nhưng tuần tự hóa các yêu cầu. Cần thiết kế khóa theo tài nguyên và cơ chế giữ/gán tài nguyên giữa service trước khi mở rộng vận hành.
- Chưa nghiệm thu checkout, chuyển phòng, đổi/thu hồi vòng, các chuyến thời gian chồng lấn hay quyền lợi dịch vụ.
- Android vẫn có đường gửi trực tiếp và worker tự gửi. Đã kiểm chứng check-in gửi lặp an toàn; chưa coi thông điệp scan realtime là exactly-once.

Không tự động commit/push các thay đổi.
