package com.project.booking.mapper;

import com.project.booking.dto.finance.CheckoutResponse;
import com.project.booking.model.Bill;
import com.project.booking.model.BillItem;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CheckoutMapper {

    public CheckoutResponse toResponse(
            Bill bill,
            List<BillItem> items) {

        List<CheckoutResponse.BillItemResponse> itemResponses = items.stream()
                .map(this::toItemResponse)
                .toList();

        return new CheckoutResponse(
                bill.getId(),
                bill.getBillCode(),
                bill.getBooking().getId(),
                bill.getTotalAmount(),
                bill.getCreatedAt(),
                itemResponses);
    }

    private CheckoutResponse.BillItemResponse toItemResponse(
            BillItem item) {

        return new CheckoutResponse.BillItemResponse(
                item.getId(),
                item.getBookingPassengerId(),
                item.getUsageType(),
                item.getUsageId(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getDiscountAmount(),
                item.getFinalAmount());
    }
}