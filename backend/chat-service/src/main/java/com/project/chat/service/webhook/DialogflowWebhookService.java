
package com.project.chat.service.webhook;

import com.project.chat.client.TourClient;
import com.project.chat.dto.internal.TourPriceSearchResponse;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ArrayList;

@Service
public class DialogflowWebhookService {

    private static final String INTENT_MIN_PRICE = "TimKiemTourTheoGiaThapNhat";

    private static final String INTENT_MAX_PRICE = "TimKiemTourTheoGiaCaoNhat";

    private static final String INTENT_PRICE_RANGE = "TimKiemTourTheoKhoangGia";

    private static final String INTENT_DEPARTURE = "TimKiemTourTheoDiemKhoiHanh";

    private static final String INTENT_DESTINATION = "TimKiemTourTheoDiemDen";

    private static final String INTENT_TIME = "TimKiemTourTheoThoiGian";

    private static final String INTENT_GENERAL = "TimKiemTour";

    private static final int MAX_TOUR_RESULTS = 3;

    private static final BigDecimal ONE_MILLION = BigDecimal.valueOf(1_000_000);

    private final TourClient tourClient;

    public DialogflowWebhookService(TourClient tourClient) {
        this.tourClient = tourClient;
    }

    /**
     * Điều phối 7 Intent tìm kiếm tour.
     */
    public String handleDialogflowIntent(
            String intentName,
            Map<String, Object> params) {

        if (params == null) {
            params = Map.of();
        }

        BigDecimal minPrice = null;
        BigDecimal maxPrice = null;

        String departurePoint = null;
        String destination = null;

        Integer month = null;
        Integer year = null;

        switch (intentName) {

            case INTENT_MIN_PRICE -> {
                BigDecimal price = getPositivePrice(params, "gia_tien");

                if (price == null) {
                    return "Dạ, bạn vui lòng cho mình biết mức giá tối thiểu mong muốn nhé.";
                }

                minPrice = convertToVnd(price);
            }

            case INTENT_MAX_PRICE -> {
                BigDecimal price = getPositivePrice(params, "gia_tien");

                if (price == null) {
                    return "Dạ, bạn vui lòng cho mình biết mức ngân sách tối đa nhé.";
                }

                maxPrice = convertToVnd(price);
            }

            case INTENT_PRICE_RANGE -> {
                BigDecimal lower = getPositivePrice(
                        params, "gia_thap_nhat");

                // Hỗ trợ tạm tên cũ nếu Dialogflow chưa đổi.
                if (lower == null) {
                    lower = getPositivePrice(
                            params, "gia_thap_nhap");
                }

                BigDecimal upper = getPositivePrice(
                        params, "gia_cao_nhat");

                if (lower == null || upper == null) {
                    return "Dạ, bạn vui lòng cho mình biết cả mức giá tối thiểu và tối đa nhé.";
                }

                if (lower.compareTo(upper) > 0) {
                    return "Dạ, mức giá tối thiểu không được lớn hơn mức giá tối đa ạ.";
                }

                minPrice = convertToVnd(lower);
                maxPrice = convertToVnd(upper);
            }

            case INTENT_DEPARTURE -> {
                departurePoint = getText(params, "diem_khoi_hanh");

                if (departurePoint == null) {
                    return "Dạ, bạn muốn khởi hành từ địa điểm nào ạ?";
                }
            }

            case INTENT_DESTINATION -> {
                destination = getText(params, "diem_den");

                if (destination == null) {
                    return "Dạ, bạn muốn đi đến địa điểm nào ạ?";
                }
            }

            case INTENT_TIME -> {
                TimeFilter timeFilter = parseTimeFilter(
                        params.get("thoi_gian"));

                if (timeFilter == null) {
                    return "Dạ, bạn muốn tìm tour khởi hành vào tháng hoặc năm nào ạ?";
                }

                month = timeFilter.month();
                year = timeFilter.year();
            }

            case INTENT_GENERAL -> {
                minPrice = getPositivePrice(
                        params, "gia_thap_nhat");

                // Hỗ trợ tên cũ trong lúc sửa Dialogflow.
                if (minPrice == null) {
                    minPrice = getPositivePrice(
                            params, "gia_thap_nhap");
                }

                maxPrice = getPositivePrice(
                        params, "gia_cao_nhat");

                if (minPrice != null && maxPrice != null
                        && minPrice.compareTo(maxPrice) > 0) {
                    return "Dạ, mức giá tối thiểu không được lớn hơn mức giá tối đa ạ.";
                }

                if (minPrice != null) {
                    minPrice = convertToVnd(minPrice);
                }

                if (maxPrice != null) {
                    maxPrice = convertToVnd(maxPrice);
                }

                departurePoint = getText(
                        params, "diem_khoi_hanh");

                destination = getText(
                        params, "diem_den");

                Object timeValue = params.get("thoi_gian");

                if (timeValue != null && !isEmptyValue(timeValue)) {
                    TimeFilter timeFilter = parseTimeFilter(timeValue);

                    if (timeFilter == null) {
                        return "Dạ, mình chưa xác định được thời gian khởi hành. Bạn vui lòng cho biết tháng hoặc năm cụ thể nhé.";
                    }

                    month = timeFilter.month();
                    year = timeFilter.year();
                }
            }

            default -> {
                return "Xin lỗi, hiện tại mình chưa hỗ trợ yêu cầu này.";
            }
        }

        return searchAndFormatTours(
                minPrice,
                maxPrice,
                departurePoint,
                destination,
                month,
                year);
    }

    /**
     * Gọi Tour Service và định dạng kết quả.
     */
    private String searchAndFormatTours(
            BigDecimal minPrice,
            BigDecimal maxPrice,
            String departurePoint,
            String destination,
            Integer month,
            Integer year) {

        List<TourPriceSearchResponse> tours = tourClient.searchReadyToursByCriteria(
                minPrice,
                maxPrice,
                departurePoint,
                destination,
                month,
                year,
                MAX_TOUR_RESULTS);

        if (tours.isEmpty()) {
            return "Dạ, hiện tại mình chưa tìm thấy tour phù hợp với các tiêu chí bạn đưa ra ạ.";
        }

        StringBuilder response = new StringBuilder();

        response.append("Dạ, mình tìm thấy ")
                .append(tours.size())
                .append(" tour phù hợp với yêu cầu của bạn:\n");

        for (TourPriceSearchResponse tour : tours) {
            response.append("- ")
                    .append(tour.tourName())
                    .append(" (Giá thấp nhất: ")
                    .append(formatVnd(tour.lowestPrice()))
                    .append(" VNĐ)\n");
        }

        return response.toString();
    }

    /**
     * Lấy giá từ Dialogflow.
     *
     * Hỗ trợ cả:
     * 30
     * 30.0
     * [30]
     */
    private BigDecimal getPositivePrice(
            Map<String, Object> params,
            String parameterName) {

        Object value = getFirstValue(params.get(parameterName));

        if (value == null) {
            return null;
        }

        try {
            BigDecimal price = new BigDecimal(value.toString());

            return price.signum() > 0 ? price : null;

        } catch (NumberFormatException ex) {
            return null;
        }
    }

    /**
     * Lấy chuỗi parameter, hỗ trợ cả giá trị dạng danh sách.
     */
    private String getText(
            Map<String, Object> params,
            String parameterName) {

        Object value = getFirstValue(params.get(parameterName));

        if (value == null) {
            return null;
        }

        String result = value.toString().trim();

        return result.isEmpty() ? null : result;
    }

    private Object getFirstValue(Object value) {
        if (value instanceof Collection<?> collection) {
            return collection.isEmpty()
                    ? null
                    : collection.iterator().next();
        }

        return value;
    }

    /**
     * Chuyển giá từ triệu VNĐ sang VNĐ.
     */
    private BigDecimal convertToVnd(BigDecimal price) {
        return price.multiply(ONE_MILLION);
    }

    /**
     * Đọc thoi_gian từ Dialogflow @sys.date-period.
     *
     * Ví dụ tháng:
     * [
     * {
     * "startDate": "2026-12-01T00:00:00+07:00",
     * "endDate": "2026-12-31T23:59:59+07:00"
     * }
     * ]
     *
     * Ví dụ năm:
     * [
     * {
     * "startDate": "2027-01-01T00:00:00+07:00",
     * "endDate": "2027-12-31T23:59:59+07:00"
     * }
     * ]
     */
    private TimeFilter parseTimeFilter(Object timeValue) {

        Object value = getFirstValue(timeValue);

        if (!(value instanceof Map<?, ?> dateMap)) {
            return null;
        }

        Object startValue = dateMap.get("startDate");
        Object endValue = dateMap.get("endDate");

        if (startValue == null || endValue == null) {
            return null;
        }

        LocalDate startDate = parseDate(startValue.toString());
        LocalDate endDate = parseDate(endValue.toString());

        if (startDate == null || endDate == null
                || endDate.isBefore(startDate)) {
            return null;
        }

        // Cả năm.
        if (startDate.getMonthValue() == 1
                && startDate.getDayOfMonth() == 1
                && endDate.getMonthValue() == 12
                && endDate.getDayOfMonth() == 31
                && startDate.getYear() == endDate.getYear()) {

            return new TimeFilter(
                    null,
                    startDate.getYear());
        }

        // Một tháng hoàn chỉnh.
        YearMonth startMonth = YearMonth.from(startDate);
        YearMonth endMonth = YearMonth.from(endDate);

        if (startMonth.equals(endMonth)
                && startDate.equals(startMonth.atDay(1))
                && endDate.equals(startMonth.atEndOfMonth())) {

            return new TimeFilter(
                    startMonth.getMonthValue(),
                    startMonth.getYear());
        }

        return null;
    }

    /**
     * Chuyển ISO date-time thành LocalDate.
     */
    private LocalDate parseDate(String value) {

        try {
            return OffsetDateTime.parse(value).toLocalDate();

        } catch (DateTimeParseException ex) {
            try {
                return LocalDate.parse(value.substring(0, 10));

            } catch (Exception ignored) {
                return null;
            }
        }
    }

    private boolean isEmptyValue(Object value) {
        if (value instanceof Collection<?> collection) {
            return collection.isEmpty();
        }

        return false;
    }

    private String formatVnd(BigDecimal price) {
        if (price == null) {
            return "Chưa cập nhật";
        }

        return NumberFormat.getNumberInstance(
                Locale.forLanguageTag("vi-VN"))
                .format(price);
    }

    private record TimeFilter(
            Integer month,
            Integer year) {
    }
}
