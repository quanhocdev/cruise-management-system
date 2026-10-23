package com.project.tour.service.webhook;

import com.project.tour.model.enums.tour.TourStatusTrip;
import com.project.tour.model.scheduler.Tour;
import com.project.tour.repository.tour.TourRepository;
import com.project.tour.repository.tour.TourPackageRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class DialogflowWebhookService {

    private final TourRepository tourRepository;
    private final TourPackageRepository tourPackageRepository;

    public DialogflowWebhookService(TourRepository tourRepository, TourPackageRepository tourPackageRepository) {
        this.tourRepository = tourRepository;
        this.tourPackageRepository = tourPackageRepository;
    }

    public String handleDialogflowIntent(String intentName, Map<String, Object> params) {
        if ("TimKiemTourTheoGia".equals(intentName)) {
            Object giaObj = params.get("gia_tien");
            double giaTienTrieu = giaObj != null ? ((Number) giaObj).doubleValue() : 0;
            BigDecimal targetPrice = BigDecimal.valueOf(giaTienTrieu * 1_000_000); // Quy đổi ra VNĐ

            // Lấy danh sách tour ở trạng thái READY hoặc APPROVED đang sẵn sàng mở bán
            // (Bạn có thể điều chỉnh lại giá trị TourStatusTrip tùy theo business logic
            // thực tế của bạn)
            List<Tour> tours = tourRepository.findAllByStatusTripOrderByNameAsc(TourStatusTrip.READY);

            // Nếu không tìm thấy tour ở trạng thái READY, có thể lấy thêm APPROVED hoặc lấy
            // tất cả rồi lọc
            if (tours.isEmpty()) {
                tours = tourRepository.findAllByStatusTripOrderByNameAsc(TourStatusTrip.APPROVED);
            }

            StringBuilder sb = new StringBuilder(
                    "Dạ, các tour có mức giá từ " + (int) giaTienTrieu + " triệu trở lên:\n");
            int count = 0;

            for (Tour t : tours) {
                // Tận dụng hàm sẵn có trong TourPackageRepository để lấy giá thấp nhất của tour
                BigDecimal lowestPrice = tourPackageRepository.findLowestPriceByTourId(t.getId());
                if (lowestPrice != null && lowestPrice.compareTo(targetPrice) >= 0) {
                    sb.append("- ").append(t.getName()).append(" (Giá thấp nhất: ").append(lowestPrice)
                            .append(" VNĐ)\n");
                    count++;
                    if (count >= 3)
                        break; // Giới hạn top 3 tour đầu tiên cho gọn khung chat
                }
            }

            if (count == 0) {
                return "Dạ hiện tại không tìm thấy tour nào có mức giá từ " + (int) giaTienTrieu + " triệu trở lên ạ.";
            }

            return sb.toString();
        }

        return "Xin lỗi, hệ thống chưa hỗ trợ nhánh này.";
    }
}