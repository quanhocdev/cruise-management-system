package com.project.booking.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/finance/pos/check-in")
public class CheckPassengerController {
    // Get: máy pos android sẽ giải mã qr code, lấy ra booking code, gửi dto lên endpoint, dựa vào code đó lấy toàn bộ thôn tin booking, danh sách hành khách
    
    // Get lấy dánh vòng nfc đang hoạt động, không quan tâm trạng thái, ở fe sẽ lọc theo trạng thái vòng nfc
    // Post để gán vòng nfc đang rãnh cho khách
    // Patch để đổi vòng cho khách
    
    // Get lấy danh sách phòng tất cả phòng, tất cả trạng thái, ở fe sẽ lọc theo trạng thái phòng
    // Post để gán phòng cho khách, tức chọn phòng xong gán hành khách của booking đó mà chưa có phòng, chỉ gán đủ giới hạn của kiểu phòng đó
    // Patch để đổi phòng cho khách, tức đổi phòng cho khách đã có phòng, chỉ đổi đủ giới hạn của kiểu phòng đó
    
    // Patch: endpoint được kích hoạt khi đã chọn phòng và vòng, cập nhật trạng thái check-in của hành khách từ pending -> checked-in
}