
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

    private static final String INTENT_SEARCH_TOUR_BY_MIN_PRICE = "TimKiemTourTheoGia";

    private static final String INTENT_SEARCH_TOUR_BY_MAX_PRICE = "TimKiemTourTheoGiaCaoNhat";

    private static final String INTENT_SEARCH_TOUR_BY_PRICE_RANGE = "TimKiemTourTheoKhoangGia";

    private static final int MAX_TOUR_RESULTS = 3;

    private static final BigDecimal ONE_MILLION = BigDecimal.valueOf(1_000_000);

    private final TourClient tourClient;

    public DialogflowWebhookService(TourClient tourClient) {
        this.tourClient = tourClient;
    }

    public String handleDialogflowIntent(
            String intentName,
            Map<String, Object> params) {

        if (INTENT_SEARCH_TOUR_BY_MIN_PRICE.equals(intentName)) {
            return handleSearchTourByMinimumPrice(params);
        }

        if (INTENT_SEARCH_TOUR_BY_MAX_PRICE.equals(intentName)) {
            return handleSearchTourByMaximumPrice(params);
        }

        if (INTENT_SEARCH_TOUR_BY_PRICE_RANGE.equals(intentName)) {
            return handleSearchTourByPriceRange(params);
        }

        return "Xin lỗi, hệ thống chưa hỗ trợ nhánh này.";
    }

    /**
     * Tìm tour có giá khởi điểm >= giá người dùng nhập.
     */
    private String handleSearchTourByMinimumPrice(
            Map<String, Object> params) {

        BigDecimal price = getPriceInVnd(params, "gia_tien");

        if (price == null) {
            return "Dạ, bạn vui lòng cho mình biết mức giá tối thiểu mong muốn nhé.";
        }

        BigDecimal priceInMillion = price.divide(ONE_MILLION);

        List<TourPriceSearchResponse> tours = tourClient.searchReadyToursByMinimumPrice(
                price,
                MAX_TOUR_RESULTS);

        if (tours.isEmpty()) {
            return "Dạ hiện tại không tìm thấy tour nào có giá khởi điểm từ "
                    + formatPrice(priceInMillion)
                    + " triệu trở lên ạ.";
        }

        return buildTourResponse(
                "Dạ, các tour có giá khởi điểm từ "
                        + formatPrice(priceInMillion)
                        + " triệu trở lên:",
                tours);
    }

    /**
     * Tìm tour có giá khởi điểm <= giá người dùng nhập.
     */
    private String handleSearchTourByMaximumPrice(
            Map<String, Object> params) {

        BigDecimal price = getPriceInVnd(params, "gia_tien");

        if (price == null) {
            return "Dạ, bạn vui lòng cho mình biết mức giá tối đa mong muốn nhé.";
        }

        BigDecimal priceInMillion = price.divide(ONE_MILLION);

        List<TourPriceSearchResponse> tours = tourClient.searchReadyToursByMaximumPrice(
                price,
                MAX_TOUR_RESULTS);

        if (tours.isEmpty()) {
            return "Dạ hiện tại không tìm thấy tour nào có giá khởi điểm không quá "
                    + formatPrice(priceInMillion)
                    + " triệu ạ.";
        }

        return buildTourResponse(
                "Dạ, các tour có giá khởi điểm không quá "
                        + formatPrice(priceInMillion)
                        + " triệu:",
                tours);
    }

    /**
     * Tìm tour có giá khởi điểm nằm trong khoảng người dùng nhập.
     */
    private String handleSearchTourByPriceRange(
            Map<String, Object> params) {

        BigDecimal minPrice = getPriceInVnd(
                params,
                "gia_thap_nhat");

        BigDecimal maxPrice = getPriceInVnd(
                params,
                "gia_cao_nhat");

        if (minPrice == null || maxPrice == null) {
            return "Dạ, bạn vui lòng cho mình biết cả giá thấp nhất và giá cao nhất nhé.";
        }

        if (minPrice.compareTo(maxPrice) > 0) {
            return "Dạ, mức giá thấp nhất không được lớn hơn mức giá cao nhất. "
                    + "Bạn vui lòng kiểm tra lại khoảng giá nhé.";
        }

        BigDecimal minMillion = minPrice.divide(ONE_MILLION);
        BigDecimal maxMillion = maxPrice.divide(ONE_MILLION);

        List<TourPriceSearchResponse> tours = tourClient.searchReadyToursByPriceRange(
                minPrice,
                maxPrice,
                MAX_TOUR_RESULTS);

        if (tours.isEmpty()) {
            return "Dạ hiện tại không tìm thấy tour nào có giá khởi điểm trong khoảng "
                    + formatPrice(minMillion)
                    + " đến "
                    + formatPrice(maxMillion)
                    + " triệu ạ.";
        }

        return buildTourResponse(
                "Dạ, các tour có giá khởi điểm từ "
                        + formatPrice(minMillion)
                        + " đến "
                        + formatPrice(maxMillion)
                        + " triệu:",
                tours);
    }

    /**
     * Lấy parameter từ Dialogflow.
     * Dialogflow trả giá theo triệu VNĐ.
     * Chuyển sang VNĐ để gọi Tour Service.
     */

    private BigDecimal getPriceInVnd(
            Map<String, Object> params,
            String parameterName) {

        if (params == null || params.get(parameterName) == null) {
            return null;
        }

        try {
            Object value = params.get(parameterName);

            BigDecimal price = value instanceof Number number
                    ? new BigDecimal(number.toString())
                    : new BigDecimal(value.toString());

            if (price.signum() <= 0) {
                return null;
            }

            // Số nhỏ hơn 1 triệu được hiểu là đơn vị triệu VNĐ.
            // Số từ 1 triệu trở lên được hiểu là VNĐ.
            if (price.compareTo(ONE_MILLION) < 0) {
                return price.multiply(ONE_MILLION);
            }

            return price;

        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private String buildTourResponse(
            String title,
            List<TourPriceSearchResponse> tours) {

        StringBuilder response = new StringBuilder();

        response.append(title).append("\n");

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
