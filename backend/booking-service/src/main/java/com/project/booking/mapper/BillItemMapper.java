package com.project.booking.mapper;

import com.project.booking.model.ActivityCruiseUsage;
import com.project.booking.model.ActivityVisitUsage;
import com.project.booking.model.Bill;
import com.project.booking.model.BillItem;
import com.project.booking.model.ProductUsage;
import com.project.booking.model.ServiceUsage;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Chuyển từng loại Usage thành BillItem.
 * Viết tay (không dùng MapStruct) vì cần thêm ngữ cảnh: bill +
 * bookingPassengerId + usageType.
 */
@Component
public class BillItemMapper {

    public static final String ACTIVITY_VISIT = "ACTIVITY_VISIT";
    public static final String ACTIVITY_CRUISE = "ACTIVITY_CRUISE";
    public static final String SERVICE = "SERVICE";
    public static final String PRODUCT = "PRODUCT";

    // Mỗi ServiceUsage tương ứng 1 passenger
    private static final int SERVICE_QUANTITY = 1;

    public BillItem fromActivityVisit(Bill bill, Long bookingPassengerId, ActivityVisitUsage usage) {
        return build(bill, bookingPassengerId, ACTIVITY_VISIT, usage.getId(), usage.getQuantity(),
                usage.getUnitPrice(), usage.getDiscountAmount(), usage.getFinalAmount());
    }

    public BillItem fromActivityCruise(Bill bill, Long bookingPassengerId, ActivityCruiseUsage usage) {
        return build(bill, bookingPassengerId, ACTIVITY_CRUISE, usage.getId(), usage.getQuantity(),
                usage.getUnitPrice(), usage.getDiscountAmount(), usage.getFinalAmount());
    }

    public BillItem fromService(Bill bill, Long bookingPassengerId, ServiceUsage usage) {
        return build(bill, bookingPassengerId, SERVICE, usage.getId(), SERVICE_QUANTITY,
                usage.getUnitPrice(), usage.getDiscountAmount(), usage.getFinalAmount());
    }

    public BillItem fromProduct(Bill bill, Long bookingPassengerId, ProductUsage usage) {
        return build(bill, bookingPassengerId, PRODUCT, usage.getId(), usage.getQuantity(),
                usage.getUnitPrice(), usage.getDiscountAmount(), usage.getFinalAmount());
    }

    private BillItem build(
            Bill bill,
            Long bookingPassengerId,
            String usageType,
            Long usageId,
            Integer quantity,
            BigDecimal unitPrice,
            BigDecimal discountAmount,
            BigDecimal finalAmount) {

        BillItem item = new BillItem();
        item.setBill(bill);
        item.setBookingPassengerId(bookingPassengerId);
        item.setUsageType(usageType);
        item.setUsageId(usageId);
        item.setQuantity(quantity);
        item.setUnitPrice(unitPrice);
        item.setDiscountAmount(discountAmount);
        item.setFinalAmount(finalAmount);
        return item;
    }
}