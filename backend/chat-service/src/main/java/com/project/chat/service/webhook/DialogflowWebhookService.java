
package com.project.chat.service.webhook;

import com.project.chat.client.TourClient;
import com.project.chat.dto.internal.TourPriceSearchResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class DialogflowWebhookService {

    private static final String INTENT_SEARCH_TOUR_BY_PRICE = "TimKiemTourTheoGia";

    private static final int MAX_TOUR_RESULTS = 3;

    private final TourClient tourClient;

    public DialogflowWebhookService(TourClient tourClient) {
        this.tourClient = tourClient;
    }

    public String handleDialogflowIntent(
            String intentName,
            Map<String, Object> params) {

        if (INTENT_SEARCH_TOUR_BY_PRICE.equals(intentName)) {
            return handleSearchTourByPrice(params);
        }

        return "Xin lỗi, hệ thống chưa hỗ trợ nhánh này.";
    }

    /**
     * Xử lý intent tìm tour theo giá.
     *
     * Dialogflow:
     * Parameter: gia_tien
     * Entity: @sys.number
     *
     * Giá người dùng nhập được hiểu là triệu VNĐ.
     */
    private String handleSearchTourByPrice(
            Map<String, Object> params) {

        if (params == null || params.get("gia_tien") == null) {
            return "Dạ, bạn vui lòng cho mình biết mức giá tour mong muốn nhé.";
        }

        BigDecimal giaTienTrieu;

        try {
            Object giaObj = params.get("gia_tien");

            if (giaObj instanceof Number number) {
                giaTienTrieu = new BigDecimal(number.toString());
            } else {
                giaTienTrieu = new BigDecimal(giaObj.toString());
            }

        } catch (NumberFormatException ex) {
            return "Dạ, mình chưa hiểu mức giá bạn cung cấp. "
                    + "Bạn vui lòng nhập lại mức giá mong muốn nhé.";
        }

        if (giaTienTrieu.signum() <= 0) {
            return "Dạ, mức giá tour cần lớn hơn 0 triệu đồng ạ.";
        }

        // Dialogflow nhận giá theo triệu VNĐ.
        // Tour Service sử dụng giá theo VNĐ.
        BigDecimal minPrice = giaTienTrieu.multiply(
                BigDecimal.valueOf(1_000_000));

        List<TourPriceSearchResponse> tours = tourClient.searchReadyToursByMinimumPrice(
                minPrice,
                MAX_TOUR_RESULTS);

        if (tours.isEmpty()) {
            return "Dạ hiện tại không tìm thấy tour nào có mức giá từ "
                    + formatPrice(giaTienTrieu)
                    + " triệu trở lên ạ.";
        }

        StringBuilder response = new StringBuilder();

        response.append("Dạ, các tour có mức giá từ ")
                .append(formatPrice(giaTienTrieu))
                .append(" triệu trở lên:\n");

        for (TourPriceSearchResponse tour : tours) {

            response.append("- ")
                    .append(tour.tourName())
                    .append(" (Giá thấp nhất: ")
                    .append(formatVnd(tour.lowestPrice()))
                    .append(" VNĐ)\n");
        }

        return response.toString();
    }

    private String formatPrice(BigDecimal price) {
        return price.stripTrailingZeros().toPlainString();
    }

    private String formatVnd(BigDecimal price) {
        return NumberFormat.getNumberInstance(
                Locale.forLanguageTag("vi-VN"))
                .format(price);
    }
}
