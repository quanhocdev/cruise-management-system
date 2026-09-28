# Brief nghiệp vụ và prompt Stitch — Android POS 4 role

Ngày đối chiếu: 28/09/2026. Code Android local `07a1861`, đối chiếu thêm backend trên `origin/main` tại `4939014` bằng Git, chưa merge vào local.

## 1. Căn cứ và giới hạn

Hai tài liệu gốc `056205011070-TrinhQuocDat-TTTN-BAOCAOSRS.pdf` và `TTTN.docx` không tồn tại tại đường dẫn Downloads đã cung cấp. Đây là bản thiết kế dựa trên tài liệu repository và mã nguồn; chưa phải đặc tả đã xác nhận với SRS. Cần đối chiếu lại khi có tài liệu gốc.

Nguồn đối chiếu chính:
- `frontend-android/STITCH_UI.md`: màu sắc, hướng chuyển thiết kế sang Compose.
- Android `PosRole.kt`, `PosLoginScreen.kt`, bốn `*PosDashboardScreen.kt`, `PosIdentityScreen.kt`, `PosIdentityRepository.kt`, `PosApiService.kt`: quyền vào POS, thao tác quét và kết quả hiện tại.
- Backend `FinanceController`, `BookingFinanceController`, frontend web `modules/finance`: tour, booking, hành khách, phòng, vòng NFC và luồng quầy lễ tân.
- Backend các controller/dto Convenience, Onboard, Shore trên main mới: cấu hình sản phẩm, dịch vụ, hoạt động theo chuyến, địa điểm và thời gian.

Ba mức phạm vi:
- **A — Android hiện có:** đăng nhập theo role, dashboard riêng, màn quét/nhập mã, lưu lịch sử local. Luồng gửi booking code dùng `/api/finance/scan`; không suy ra NFC của ba role khác đã tích hợp thành công.
- **B — dữ liệu/nghiệp vụ web hoặc backend đã có:** có thể làm cơ sở thiết kế màn đọc trên Android, nhưng vẫn cần nối API và xác minh phạm vi truy cập. Endpoint tồn tại không chứng minh đã giới hạn dữ liệu theo phân công nhân viên.
- **C — đề xuất phát triển:** tương tác vận hành hợp lý cho POS nhưng chưa được xác nhận trong SRS hoặc API hiện tại. Vẽ thành nhóm concept riêng, không coi là chức năng đã chạy.

## 2. Mỗi role được xem và làm gì?

| Role | Mục tiêu trên POS | Dữ liệu để xem | Thao tác chính | Phạm vi chưa hoàn tất |
|---|---|---|---|---|
| FINANCE — Lễ tân & tài chính | Tiếp nhận khách, hỗ trợ thủ tục | A: mã booking, kết quả gửi, lịch sử quét. B: tour/lịch trình, booking, hành khách, phòng, vòng NFC | A: quét QR booking, nhập mã booking; gửi tới quầy web. Android có màn NFC nhưng cần nghiệp vụ nhận diện phù hợp | C: thủ tục độc lập trên Android, gán phòng/vòng, checkout, tổng hợp phát sinh và quyết toán |
| CONVENIENCE — Tiện ích | Nhận diện khách sử dụng sản phẩm/dịch vụ | B: sản phẩm, dịch vụ, giá và cấu hình theo chuyến. C: khách sau nhận diện NFC, quyền lợi còn lại, lượt sử dụng | A: giao diện quét NFC và lịch sử. C: chọn món/dịch vụ, số lượng, xem tóm tắt rồi ghi nhận | Chưa có luồng POS ghi nhận sử dụng/chi phí hoàn chỉnh; không được coi số lượng cấu hình là tồn kho thực tế |
| ONBOARD — Hoạt động trên tàu | Kiểm tra và ghi nhận người tham gia | B: hoạt động theo chuyến, khu vực, thời gian, giá/cấu hình. C: danh sách và trạng thái tham gia | A: giao diện quét NFC và lịch sử. C: chọn phiên hoạt động, nhận diện khách, xác nhận tham gia | Chưa hoàn thiện API điểm danh, chống ghi nhận trùng, số người thực tế và quyền tham gia |
| SHORE — Tham quan bờ | Kiểm tra người tham gia tại điểm dừng | B: hoạt động tham quan, lịch/điểm dừng, thời gian và giá. C: danh sách khách, trạng thái đã điểm danh | A: giao diện quét NFC và lịch sử. C: chọn chuyến tham quan, nhận diện khách, xác nhận tham gia | Điểm danh đi/về, khách chưa trở lại và cảnh báo trễ giờ là đề xuất cần chốt nghiệp vụ, không phải chức năng đã xác nhận |

**Ranh giới thiết kế:** chức năng tạo/sửa danh mục, cấu hình giá, phân công, duyệt tour hiện thuộc luồng quản lý web. Bản POS này ưu tiên thao tác phục vụ khách tại chỗ; không tự đưa toàn bộ CRUD web vào mobile. Việc chỉ xem chuyến/khu vực được phân công là yêu cầu UX đề xuất và cần backend thực thi.

**Finance cần phân biệt:** quét được mã → máy chủ nhận mã → quầy web xử lý → hoàn tất thủ tục. Hiện Android chưa có bằng chứng xác nhận web đã nhận/đã mở màn hình; chỉ hiển thị điều API thực sự xác nhận. Không hiển thị “Đã check-in” ngay sau khi gửi QR.

## 3. Cách đưa vào Stitch

Tạo cùng một project thiết kế. Gửi prompt nền bên dưới trước, sau đó lần lượt gửi từng prompt role. Yêu cầu Stitch giữ cùng component và theme. Mỗi role có bộ màn core và nhóm concept riêng; chưa đưa concept vào bản app chạy thật khi chưa có nghiệp vụ tương ứng.

## Prompt 0 — Nền thiết kế và đăng nhập dùng chung

```text
Thiết kế một ứng dụng Android native “Cruise POS” dành cho nhân viên phục vụ trên du thuyền. Đây là công cụ làm việc tại quầy và khi di chuyển. Toàn bộ nội dung UI bằng tiếng Việt tự nhiên. Thiết kế có thể triển khai bằng Kotlin Jetpack Compose / Material 3.

Có 4 role do hệ thống trả về sau đăng nhập:
FINANCE — Lễ tân & tài chính.
CONVENIENCE — Tiện ích.
ONBOARD — Hoạt động trên tàu.
SHORE — Tham quan bờ.
Nhân viên đăng nhập bằng tài khoản được cấp; không tự chọn role, không đăng ký tài khoản trong POS. Mỗi phiên chỉ có một role; đổi tài khoản cần đăng xuất.

Phong cách: gọn, chuyên nghiệp, dễ thao tác nhanh. Dùng nền #FAF8FF, chữ navy #0B2545, primary teal #006A69, mint #98F2F0, surface trắng, card bo góc 16–20dp. Accent role: Finance teal #126A70, Convenience tím #7C3AED, Onboard cam #DB6B32, Shore xanh #18794E. Accent dùng cho biểu tượng/badge/đường viền, kiểm tra tương phản trước khi dùng làm nền nút. Luôn có tên role, không phân biệt role chỉ bằng màu.

Khung chính 390×844, có biến thể rộng 360dp, thích ứng tablet 800dp. Tôn trọng safe area/status bar, bàn phím và font phóng lớn. Chữ nội dung khoảng 16sp, vùng bấm ít nhất 48dp, CTA chính cao khoảng 56dp. Ưu tiên danh sách/card đọc nhanh, nhãn trạng thái kèm icon; không dùng bảng desktop thu nhỏ, biểu đồ trang trí, ảnh du thuyền chiếm màn hình hay gradient lớn. Một màn có một thao tác chính rõ ràng.

Thiết kế component dùng chung: app bar, badge role, thẻ ngữ cảnh chuyến/khu vực, item danh sách, bộ lọc, trạng thái NFC/camera, bottom sheet xác nhận, banner mất mạng, trạng thái kết quả, empty state, skeleton, lỗi có nút thử lại, lịch sử thao tác và hộp thoại đăng xuất.

Thiết kế các màn chung:
1. Đăng nhập nhân viên: tên đăng nhập, mật khẩu có hiện/ẩn, CTA Đăng nhập, Quay lại. Có lỗi nhập thiếu, sai thông tin, đang xử lý và tài khoản không được phép dùng POS. Giữ username khi có lỗi. Không hiển thị tài khoản/mật khẩu test.
2. Tài khoản: tên nhân viên, bộ phận, mã thiết bị nếu có, trạng thái kết nối, đăng xuất. Không thêm đổi role hoặc thiết lập API/server cho người vận hành.
3. Lịch sử thao tác thuộc role hiện tại: thời gian, loại thao tác, mã tham chiếu, trạng thái; lọc theo trạng thái. Phân biệt “Đã lưu trên máy”, “Chưa xác nhận”, “Đã gửi”, “Bị từ chối”; không đánh đồng gửi thành công với hoàn tất nghiệp vụ. Không hiển thị thống kê doanh thu giả.

Quy tắc xuyên suốt: dữ liệu demo nhất quán giữa các màn và ghi “Dữ liệu minh họa” ở chú thích thiết kế ngoài khung app. Không đưa các câu “chờ API”, “backend chưa hỗ trợ”, tên endpoint hoặc thuật ngữ JWT lên UI vận hành. Chức năng tương lai nằm trên canvas concept riêng, không trộn vào menu core.

Mất mạng: dùng thông báo rõ ràng. Chỉ nói “Đã lưu trên máy” khi thao tác thực sự đã lưu; thao tác cần xác minh máy chủ không được báo thành công. Trạng thái chưa rõ sau timeout phải hướng dẫn kiểm tra kết quả trước khi gửi lại để tránh trùng.

Xuất màn hình high-fidelity có tên màn, flow nối giữa màn và component tái sử dụng. Các role sẽ được yêu cầu thiết kế ở những prompt tiếp theo trong cùng project.
```

## Prompt 1 — FINANCE

```text
Tiếp tục project Cruise POS với theme/component đã thiết lập. Thiết kế role FINANCE, nhãn “Lễ tân & tài chính”, accent teal. Người dùng cần quét nhanh mã booking từ email của khách để hỗ trợ quầy lễ tân.

Luồng core hiện tại: quét QR hoặc nhập booking code → gửi mã → máy chủ trả kết quả → nhân viên tiếp tục thủ tục trên quầy web. Gửi mã thành công KHÔNG đồng nghĩa check-in thành công; chưa có xác nhận web đã mở màn hình.

Bộ màn core:
F01 Trang chính: tên nhân viên, badge role, trạng thái kết nối; CTA nổi bật “Quét QR booking”, nút phụ “Nhập mã booking”, truy cập lịch sử và tài khoản. Không có biểu đồ doanh thu hoặc bộ đếm không có dữ liệu.
F02 Camera: khung quét rõ, hướng dẫn “Đưa mã QR trong email vào khung”, đèn pin, đóng camera, chuyển nhập mã. Thiết kế trạng thái chưa cấp quyền camera và đường tới cài đặt; không quét lặp liên tục khi đang gửi.
F03 Nhập mã booking: trường nhập có nhãn, hỗ trợ dán mã, lỗi mã trống, CTA “Gửi mã”. Không yêu cầu nhập ID nội bộ database.
F04 Đang gửi: mã vừa quét, chỉ báo đang xử lý, chặn gửi lặp.
F05 Kết quả thành công: tiêu đề “Đã gửi mã booking”, mã tham chiếu, lời nhắc “Tiếp tục làm thủ tục tại quầy lễ tân”, CTA “Quét khách tiếp theo”, liên kết xem lịch sử. Không tuyên bố khách đã nhận phòng hay đã check-in; không khẳng định quầy web đã nhận nếu không có xác nhận tương ứng.
F06 Kết quả lỗi: mã không hợp lệ/không tìm thấy, không đủ quyền, mất kết nối, phiên hết hạn. Cho hành động phù hợp như quét lại, sửa mã, đăng nhập lại. Với timeout chưa rõ kết quả, nhắc kiểm tra trước khi gửi lại.
F07 Lịch sử và chi tiết lần quét: QR hay nhập tay, thời gian, mã booking, trạng thái gửi và thông điệp kết quả. Dữ liệu chỉ trong phạm vi phiên/role được phép.

Bộ màn mở rộng, đặt canvas “Finance — Concept cần tích hợp”:
F08 Chọn chuyến và danh sách booking: tên chuyến, tàu, ngày; tìm mã booking; lọc trạng thái nếu nguồn dữ liệu có.
F09 Chi tiết booking: danh sách hành khách, trạng thái thủ tục, phòng/vòng NFC nếu đã được gán; chỉ thông tin cần phục vụ, không phơi bày số giấy tờ đầy đủ.
F10 Đọc vòng NFC: hướng dẫn đặt vòng gần thiết bị, trạng thái NFC tắt/thiết bị không hỗ trợ/không nhận diện được/sai chuyến. NFC đọc định danh khách, không phải quẹt thẻ ngân hàng.
F11 Concept làm thủ tục trên Android: chọn hành khách → chọn phòng phù hợp → gắn vòng NFC → xem lại → xác nhận; minh họa xung đột phòng và vòng đã gán. Đây là mở rộng từ luồng web, không có trong core Android hiện tại.
F12 Concept checkout: nhận diện khách → xem tổng hợp khoản phát sinh được hệ thống cung cấp → kiểm tra điều kiện hoàn tất → xác nhận. Không bịa ví điện tử, số dư NFC, hoàn tiền, máy in, thanh toán thẻ hoặc quy tắc thu tiền. Nếu chưa có dữ liệu đối soát, không thiết kế hành động xác nhận đã thanh toán.

Navigation core chỉ cần Trang chính, Lịch sử, Tài khoản. Bộ concept có thể thêm Booking. Dùng dữ liệu mẫu nhất quán, tránh nhét toàn bộ quản trị tour/tàu/phòng vào màn POS.
```

## Prompt 2 — CONVENIENCE

```text
Tiếp tục project Cruise POS, dùng cùng component và typography. Thiết kế CONVENIENCE — “Tiện ích”, accent tím. Mục tiêu là nhân viên tại quầy nhận diện khách bằng vòng NFC và ghi nhận khách sử dụng sản phẩm/dịch vụ.

Phạm vi: app hiện có giao diện NFC/lịch sử; dữ liệu sản phẩm, dịch vụ và cấu hình theo chuyến có ở backend/web. Luồng xác minh quyền lợi và ghi nhận sử dụng trên POS là thiết kế phát triển tiếp, chưa được coi là API đã hoàn chỉnh. Tách canvas “Core” và “Luồng nghiệp vụ đề xuất”, ghi chú ngoài khung app.

Core: trang chính với tên nhân viên, role, CTA “Quét vòng NFC”, lịch sử, tài khoản; màn đọc NFC và trạng thái thiết bị không hỗ trợ/NFC đang tắt/đọc thất bại; không tự báo nhận diện khách thành công chỉ vì đọc được UID. Không có QR hay nhập booking code trong role này.

Thiết kế luồng nghiệp vụ đề xuất:
C01 Ngữ cảnh làm việc: chuyến, tàu và quầy/khu vực được phép; chưa được phân công thì có empty state rõ. Không cho tự mở mọi chuyến của công ty.
C02 Danh mục theo chuyến: tab Sản phẩm/Dịch vụ, tìm kiếm, tên, ảnh nhỏ tùy chọn, giá và đơn vị theo dữ liệu. Không hiểu số lượng được cấu hình cho tour là tồn kho khả dụng. Bộ lọc đơn giản.
C03 Nhận diện khách sau NFC: họ tên, phòng nếu có, chuyến, thông tin quyền sử dụng liên quan. Không hiển thị giấy tờ, tổng tiền booking hay dữ liệu không cần thiết.
C04 Chi tiết sản phẩm/dịch vụ: mô tả ngắn, đơn giá, số lượng hoặc lượt sử dụng phù hợp; thêm vào danh sách sử dụng. Hỗ trợ một hoặc nhiều mục trong một lần ghi nhận nhưng không mặc định phải có chức năng giỏ hàng thương mại đầy đủ.
C05 Xem lại: khách, quầy, từng mục, số lượng, đơn giá; khi hệ thống cung cấp quyền lợi thì phân biệt “Trong quyền lợi gói” và “Phát sinh”. Nếu chưa có kết quả quyền lợi, hiển thị đang kiểm tra/không thể xác minh, không tự cho miễn phí. Không thêm VAT, tip, voucher, điểm thưởng, số dư ví hoặc giảm giá tùy ý.
C06 Xác nhận: CTA “Ghi nhận sử dụng”; có trạng thái đang gửi, thành công sau xác nhận máy chủ, từ chối và timeout chưa rõ kết quả. Kết quả hiển thị mã ghi nhận, giờ, khách, các mục; CTA “Phục vụ khách tiếp theo”. Không dùng “Thanh toán thành công” nếu chỉ ghi nhận khoản phát sinh.
C07 Lịch sử sử dụng và chi tiết theo quầy/role, tìm theo tên hoặc mã tham chiếu khi có; phân biệt dữ liệu local và đã được máy chủ xác nhận.

Thiết kế ngoại lệ: thẻ chưa gắn khách, sai chuyến, khách không đủ điều kiện, dịch vụ không còn khả dụng, số lượng không hợp lệ, mất mạng. Không cho chỉnh giá hay tạo/sửa danh mục trong POS. Việc hủy hoặc sửa giao dịch sau xác nhận cần quyền và quy tắc riêng, không tự thêm nút xóa giao dịch.

Navigation đề xuất: Tổng quan, Danh mục, Lịch sử, Tài khoản; Quét NFC là CTA nổi bật trong ngữ cảnh phục vụ. Vẽ flow từ chọn mục/nhận diện đến kết quả, dùng tên khách và món nhất quán giữa các màn.
```

## Prompt 3 — ONBOARD

```text
Tiếp tục project Cruise POS. Thiết kế ONBOARD — “Hoạt động trên tàu”, accent cam, dùng cùng design system. Nhân viên đứng tại khu vực hoạt động cần chọn đúng phiên hoạt động và xác nhận người tham gia bằng vòng NFC.

Phạm vi hiện có: Android có màn NFC/lịch sử; backend/web có hoạt động được cấu hình theo chuyến, khu vực, thời gian và giá. Danh sách người tham gia, số đã điểm danh và thao tác ghi nhận là đề xuất cần tích hợp. Tách canvas core và concept; chú thích phạm vi ngoài UI.

Core: dashboard role với Quét vòng NFC, lịch sử và tài khoản; màn NFC có hướng dẫn, trạng thái tắt/không hỗ trợ/đọc thất bại; không có QR booking hoặc nhập mã thủ công. Đọc vòng thành công chưa có nghĩa người đó được tham gia.

Bộ màn nghiệp vụ đề xuất:
O01 Tổng quan hoạt động theo chuyến: tên tàu/chuyến, khu vực đang phụ trách, danh sách hoạt động hôm nay. Mỗi card có tên, thời gian, khu vực và trạng thái theo dữ liệu. Không hiển thị sức chứa/số đã tham gia nếu chưa có nguồn dữ liệu.
O02 Chi tiết phiên: tên hoạt động, lịch, khu vực/tầng nếu có, giá hoặc điều kiện tham gia do hệ thống cung cấp; CTA “Quét NFC người tham gia”. Luôn giữ tên phiên ở màn quét để tránh ghi nhận nhầm phiên.
O03 Quét NFC trong phiên đã chọn: chỉ dẫn ngắn, dễ thao tác một tay, feedback trực quan khi đọc được vòng.
O04 Kết quả kiểm tra: tên khách, phòng nếu cần, phiên đang xác nhận, đủ điều kiện/không đủ điều kiện/chưa xác minh. Nếu đã tham gia, hiển thị thời gian ghi nhận trước đó và không thêm lượt lần nữa.
O05 Xem lại và CTA “Xác nhận tham gia”; cảnh báo rõ nếu phát sinh phí theo dữ liệu đã được hệ thống xác nhận. Không tự trừ tiền từ NFC hoặc coi giá niêm yết là khoản phải thu của mọi khách.
O06 Kết quả xác nhận thành công, mã tham chiếu, giờ; CTA lớn “Quét người tiếp theo”, hành động phụ xem danh sách.
O07 Danh sách người tham gia: tìm kiếm, trạng thái đã ghi nhận; nhóm chưa tham gia chỉ xuất hiện nếu có danh sách đăng ký hợp lệ, không lấy toàn bộ hành khách trên tàu làm danh sách đăng ký.
O08 Lịch sử và chi tiết thao tác theo phiên/khu vực.

Ngoại lệ cần vẽ: sai chuyến, thẻ không hợp lệ, phiên chưa mở/đã kết thúc khi có quy tắc tương ứng, ghi nhận trùng, mất mạng và timeout chưa rõ kết quả. Chỉ có “Đã xác nhận tham gia” sau phản hồi máy chủ. Không tự cấp quyền offline khi chưa có cơ chế kiểm tra tương ứng.

Navigation: Hoạt động, Lịch sử, Tài khoản. Không có tạo/sửa giá hoạt động, duyệt tour hoặc báo cáo doanh thu. Ưu tiên tốc độ xử lý hàng người chờ và chuyển sang khách tiếp theo rõ ràng.
```

## Prompt 4 — SHORE

```text
Tiếp tục project Cruise POS. Thiết kế SHORE — “Tham quan bờ”, accent xanh, cùng design system. Nhân viên làm việc ngoài trời tại cảng/điểm tập trung, cần chọn đúng chuyến tham quan và nhận diện người tham gia qua NFC. Dùng tương phản cao, chữ rõ, nút lớn; màu trạng thái luôn đi kèm chữ/icon.

Phạm vi: Android có giao diện NFC/lịch sử; backend/web có cấu hình hoạt động tham quan theo chuyến và lịch/điểm dừng, thời gian, giá. Điểm danh khách là luồng đề xuất chưa tích hợp hoàn chỉnh. Việc theo dõi rời tàu/trở lại tàu và cảnh báo người chưa về là một concept tùy chọn cần xác nhận nghiệp vụ, không mặc định là yêu cầu đã có.

Core: dashboard với Quét vòng NFC, lịch sử, tài khoản; NFC đang tắt/không hỗ trợ/đọc thất bại. Không có QR booking hay nhập booking code. Đọc NFC chỉ lấy định danh, phải kiểm tra máy chủ trước khi xác nhận nghiệp vụ.

Bộ màn nghiệp vụ đề xuất:
S01 Lịch tham quan theo chuyến/ngày/điểm dừng: tên hoạt động, điểm dừng, giờ bắt đầu/kết thúc; điểm tập trung chỉ hiển thị khi có dữ liệu. Phân biệt giờ hoạt động và giờ tàu rời cảng, không tự coi hai giờ giống nhau.
S02 Chi tiết chuyến tham quan: tên, lịch, địa điểm, mô tả ngắn và điều kiện hệ thống cung cấp; CTA “Quét NFC người tham gia”. Ngữ cảnh chuyến tham quan luôn hiện trong quá trình quét.
S03 NFC → kiểm tra khách: họ tên, chuyến đang đi, điều kiện tham gia; trạng thái hợp lệ/sai chuyến/chưa đủ điều kiện/đã ghi nhận. Không suy ra đã đăng ký chỉ từ việc khách có vòng NFC.
S04 Xem lại → “Xác nhận tham gia” → kết quả có thời gian, mã ghi nhận và CTA “Quét người tiếp theo”. Chỉ báo thành công khi máy chủ xác nhận.
S05 Danh sách người tham gia: tìm tên, lọc đã ghi nhận/chưa ghi nhận khi có danh sách đăng ký, đếm số dựa trên dữ liệu thật; không tự đưa toàn bộ khách trong booking vào chuyến tham quan.
S06 Lịch sử theo hoạt động/điểm dừng, chi tiết trạng thái gửi và kết quả.

Canvas tùy chọn “Concept — Điểm danh đi/về, cần chốt nghiệp vụ”:
S07 Hai chế độ rõ ràng “Điểm danh khởi hành” và “Điểm danh trở về”, tiêu đề và màu khác nhau để tránh ghi sai chiều.
S08 Tổng quan đã khởi hành/đã trở về/chưa trở về, danh sách cần kiểm tra. Dùng “Chưa ghi nhận trở về”, không kết luận hành khách mất tích hoặc vẫn ở ngoài chỉ từ việc chưa quét.
S09 Trường hợp quét trùng, ghi nhận trở về nhưng chưa có lượt đi, thao tác cần người phụ trách xử lý. Không tự thêm GPS theo dõi khách, bản đồ realtime, gọi khẩn cấp, tự động báo động hoặc tự đóng chuyến khi chưa có nghiệp vụ.

Mất mạng cần nổi bật nhưng không che thao tác. Bản ghi chờ xác minh không tính vào số đã xác nhận. Nếu bản thiết kế có danh sách offline, ghi rõ thời điểm dữ liệu cập nhật và trạng thái chưa được máy chủ xác nhận.

Navigation: Tham quan, Lịch sử, Tài khoản. Không thêm quản lý danh mục cảng, cấu hình giá, duyệt tour hay chỉnh lịch tàu. Thiết kế thao tác ngoài trời và đối chiếu tên khách nhanh.
```

## 4. Tiêu chí duyệt bản Stitch

- Bốn role có cùng hệ component nhưng khác mục tiêu, ngữ cảnh và CTA; không chỉ đổi màu dashboard.
- Finance phân biệt gửi mã với hoàn tất check-in; ba role còn lại không có QR booking.
- Tên nhân viên, role và ngữ cảnh đang xử lý luôn rõ; không có nút tự chuyển role.
- Mỗi flow có loading, empty, thành công, từ chối, thiếu quyền thiết bị và mất mạng phù hợp.
- Không giả định vòng NFC là ví tiền; không phát sinh số dư, quyền lợi, phí, tồn kho hoặc chỉ số chưa có dữ liệu.
- Concept phát triển tiếp được tách khỏi core; lời giải thích kỹ thuật nằm ngoài màn hình người dùng.
- Khi nhận tài liệu SRS gốc, đối chiếu lại quyền theo role, phạm vi POS/web, checkout, quyền lợi gói và điểm danh đi/về trước khi triển khai concept.
