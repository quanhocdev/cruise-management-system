# Dữ liệu mẫu Passenger trên máy local

`passenger-demo.sql` bổ sung 3 ngày lịch trình, 1 hoạt động Yoga miễn phí và
1 chuyến tham quan giá 250.000 đồng cho tour demo
`55555555-5555-5555-5555-555555555555`. Tour và phòng demo
`44444444-4444-4444-4444-444444444444` phải có sẵn.

Chạy PowerShell tại thư mục gốc repository, chỉ với database local dùng để test:

```powershell
Get-Content -Raw backend/tour-service/src/test/resources/manual/passenger-demo.sql |
    docker compose exec -T postgres-tour sh -c 'psql -v ON_ERROR_STOP=1 -U "${POSTGRES_USER:-postgres}" -d tour_service_db'
```

Script dùng transaction, ID cố định và `ON CONFLICT DO NOTHING` để chạy lại
không tạo trùng. Không sửa bản ghi booking, payment hay user; không ghi đè
lịch trình người dùng. Đây không phải migration hay dữ liệu production.

Hoạt động được chèn trực tiếp vào bảng snapshot của tour-service để kiểm tra
API đọc và giao diện. Không kiểm thử đồng bộ Kafka hoặc đăng ký hoạt động
ở service nguồn; các mã hoạt động nguồn trong fixture chỉ là mã mẫu.

## Kiểm tra trên Android

1. Đăng nhập tài khoản có booking thuộc tour demo.
2. Mở chi tiết booking, tải lại dữ liệu.
3. Mở lịch trình: có đủ 3 ngày mang nhãn `[TEST]`.
4. Mở hoạt động: thấy Yoga trên tàu (miễn phí, tối đa 20 người) và tham quan
   Hạ Long (250.000 đồng, tối đa 12 người).
5. Kiểm tra chi tiết thời gian, địa điểm và trạng thái; số người tối đa không
   phải số chỗ còn lại.

Không cần thanh toán lại hoặc tạo thêm booking để kiểm tra fixture này.

## Kết quả kiểm tra local

- Chạy SQL lần đầu thành công; chạy lần hai không chèn thêm bản ghi.
- API nội bộ trip-details trả HTTP 200, 3 ngày và 2 hoạt động đúng fixture.
- Chưa xác nhận trực quan trên thiết bị Android trong lần kiểm tra này.
