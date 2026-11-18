
package com.project.booking.mapper.passenger;

import com.project.booking.dto.passenger.bill.PassengerBillDTO;
import com.project.booking.dto.passenger.bill.PassengerBillItemDTO;
import com.project.booking.model.Bill;
import com.project.booking.model.BillItem;

import java.util.List;

public class BillMapper {

    public PassengerBillItemDTO toBillItemDTO(BillItem item) {
        if (item == null) {
            return null;
        }

        return new PassengerBillItemDTO(
                item.getId(),
                item.getUsageType(),
                item.getUsageId(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getDiscountAmount(),
                item.getFinalAmount());
    }

    public List<PassengerBillItemDTO> toBillItemDTOs(
            List<BillItem> items) {
        return items.stream()
                .map(this::toBillItemDTO)
                .toList();
    }

    public PassengerBillDTO toDTO(Bill bill, List<BillItem> items) {
        if (bill == null) {
            return null;
        }

        return new PassengerBillDTO(
                bill.getId(),
                bill.getBooking().getId(),
                bill.getTotalAmount(),
                bill.getStatus(),
                toBillItemDTOs(items),
                bill.getCreatedAt(),
                bill.getUpdatedAt());
    }

    public List<PassengerBillDTO> toDTOs(
            List<Bill> bills,
            List<BillItem> items) {
        return bills.stream()
                .map(bill -> {
                    List<BillItem> billItems = items.stream()
                            .filter(item -> item.getBill().getId()
                                    .equals(bill.getId()))
                            .toList();

                    return toDTO(bill, billItems);
                })
                .toList();
    }
}
