# FLOW demo data

Bo du lieu nay phuc vu kiem thu luong web va Android POS ma khong sua logic backend. Tat ca ma nghiep vu deu co tien to `FLOW-`; script co the chay lai nhieu lan va co script don dep rieng.

## Nap va xoa du lieu

Mo PowerShell tai thu muc goc du an, dam bao Docker Compose dang chay, sau do chay:

```powershell
powershell -ExecutionPolicy Bypass -File .\dev\test-data\seed-demo.ps1
```

Xoa rieng bo du lieu nay:

```powershell
powershell -ExecutionPolicy Bypass -File .\dev\test-data\cleanup-demo.ps1
```

## Quy mo

- 3 du thuyen, 6 tang, 12 khu vuc, 3 hang phong va 30 phong.
- 6 tour, 18 ngay lich trinh, 36 diem dung va 12 goi tour.
- 8 san pham, 6 dich vu, 6 hoat dong tren tau va 12 tour tham quan bo.
- 50 vong NFC: `FLOW-NFC-001` den `FLOW-NFC-035` kha dung; `036` den `047` da gan; `048` den `050` ngung su dung.
- 24 booking, 48 hanh khach va 24 giao dich thanh toan.

## Ma dung de test nhanh

| Luong | Du lieu nhap | Ket qua mong doi |
|---|---|---|
| Finance quet QR | `FLOW-BOOK-001` | Booking da xac nhan, 2 khach dang cho check-in |
| Finance check-in | Booking ID `920001`, passenger ID `921001`, NFC `FLOW-NFC-001` | Co the chon phong Standard cua `FLOW-SHIP-01` va check-in |
| Booking da check-in | `FLOW-BOOK-009` | Hai khach da co phong va NFC |
| Booking da checkout | `FLOW-BOOK-011` | Hai khach o trang thai `CHECKED_OUT` |
| Booking hon hop | `FLOW-BOOK-013` | Mot khach da check-in, mot khach dang cho |
| Chua thanh toan | `FLOW-BOOK-015` | Booking `PENDING_PAYMENT`, khong du dieu kien check-in |
| Da huy | `FLOW-BOOK-020` | Booking `CANCELLED`, dung de test thong bao loi |
| Convenience NFC | `FLOW-NFC-036` | Vong da gan cho hanh khach demo |
| Convenience NFC trong | `FLOW-NFC-010` | Vong co trang thai `AVAILABLE` |
| Convenience NFC vo hieu | `FLOW-NFC-049` | Vong co trang thai `INACTIVE` |
| Onboard QR | `FLOW-BOOK-001` hoac `FLOW-BOOK-009` | App luu va hien ma QR; backend hien tai chua co API boarding |
| Shore QR | `FLOW-BOOK-001` | App tai danh sach tham quan `FLOW`; backend hien tai chua xac nhan quyen theo QR |

Voi buoc Finance check-in, package `FLOW - Tieu chuan 1` dung hang phong `FLOW - Standard`. Cac phong hop le tren tau dau tien gom `FLOW-01-01`, `FLOW-01-04`, `FLOW-02-02` va `FLOW-02-05`.

Tai khoan POS giu nguyen du lieu khoi tao cua du an: `finance`, `convenience`, `onboard`, `shore`. Mat khau da xac nhan tren moi truong Docker hien tai la `admin@123`. Bo seed nay khong doi mat khau tai khoan.
