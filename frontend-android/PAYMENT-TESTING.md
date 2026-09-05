# Test VNPay Sandbox trên Android thật

Android không chứa `vnp_TmnCode` hoặc `vnp_HashSecret`. Hai giá trị này chỉ nằm
trong biến môi trường của payment-service.

Khi test bằng điện thoại cùng Wi-Fi với máy chạy Docker, cấu hình `.env`:

```properties
VNPAY_RETURN_URL=http://IP_LAN_CUA_MAY_TIN:8080/api/v1/payments/vnpay/return
VNPAY_FRONTEND_RESULT_URL=cruiseapp://payment/result
```

Ví dụ `IP_LAN_CUA_MAY_TIN` là `192.168.1.45`; không dùng `localhost`, `10.0.2.2`
hoặc IP Docker. Sau khi sửa biến môi trường, tạo lại payment-service và gateway:

```powershell
docker compose up -d --build --force-recreate payment-service gateway-service
```

Điện thoại và máy tính phải cùng mạng, Windows Firewall phải cho phép cổng 8080.
Với mạng ngoài hoặc khi kiểm thử IPN server-to-server, cần HTTPS public (ví dụ
tunnel); địa chỉ LAN chỉ phù hợp cho luồng return trên điện thoại trong cùng mạng.

Luồng test: Booking của tôi → booking chờ thanh toán → Thanh toán VNPay Sandbox →
hoàn tất giao dịch → trình duyệt mở lại ứng dụng → Xem booking của tôi. Trạng thái
phải chuyển sang `CONFIRMED` và QR booking xuất hiện.
