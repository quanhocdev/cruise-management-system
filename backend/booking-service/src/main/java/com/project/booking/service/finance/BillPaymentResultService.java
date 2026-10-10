
package com.project.booking.service.finance;

import com.project.booking.model.Bill;
import com.project.booking.model.enums.BillStatus;
import com.project.booking.repository.BillRepository;
import com.project.common.event.BillPaymentSuccessEvent;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BillPaymentResultService {

    private final BillRepository billRepository;

    public BillPaymentResultService(BillRepository billRepository) {
        this.billRepository = billRepository;
    }

    @Transactional
    public void processPaymentSuccess(BillPaymentSuccessEvent event) {
        if (event == null || event.billId() == null) {
            throw new IllegalArgumentException(
                    "Bill payment success event or bill ID must not be null");
        }

        Bill bill = billRepository.findById(event.billId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Bill not found with ID: " + event.billId()));

        // Idempotent: không xử lý lại hóa đơn đã thanh toán.
        if (bill.getStatus() == BillStatus.PAID) {
            return;
        }

        bill.setStatus(BillStatus.PAID);
        billRepository.save(bill);

        System.out.println(
                ">>> [BILL PAYMENT] Bill ID " + bill.getId()
                        + " đã được cập nhật trạng thái PAID.");
    }
}
