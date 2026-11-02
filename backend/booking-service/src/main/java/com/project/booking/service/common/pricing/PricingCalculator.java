
package com.project.booking.service.common.pricing;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class PricingCalculator {

    private static final BigDecimal ZERO = BigDecimal.ZERO;
    private static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);

    /**
     * Tính giá sử dụng benefit chung cho Product, Service,
     * Activity Cruise và Activity Visit.
     *
     * @param price               Đơn giá
     * @param discountPercent     Phần trăm giảm giá
     * @param quantity            Số lượng sử dụng lần này
     * @param benefitQuantity     Tổng số lượng tối đa được giảm giá
     * @param usedBenefitQuantity Tổng số lượng đã được giảm giá trước đó
     */
    public PricingResult calculate(
            BigDecimal price,
            BigDecimal discountPercent,
            long quantity,
            long benefitQuantity,
            long usedBenefitQuantity) {

        validate(price, discountPercent, quantity,
                benefitQuantity, usedBenefitQuantity);

        BigDecimal unitPrice = price.setScale(2, RoundingMode.HALF_UP);

        BigDecimal percent = discountPercent == null
                ? ZERO
                : discountPercent;

        // Số lượng benefit còn có thể được hưởng giảm giá.
        long remainingDiscountQuantity = Math.max(
                benefitQuantity - usedBenefitQuantity,
                0L);

        // Số lượng được giảm giá trong lần sử dụng hiện tại.
        long discountedQuantity = Math.min(
                quantity,
                remainingDiscountQuantity);

        // Tổng tiền trước giảm giá.
        BigDecimal grossAmount = unitPrice
                .multiply(BigDecimal.valueOf(quantity))
                .setScale(2, RoundingMode.HALF_UP);

        // Chỉ giảm phần trăm trên số lượng đủ điều kiện.
        BigDecimal discountAmount = unitPrice
                .multiply(BigDecimal.valueOf(discountedQuantity))
                .multiply(percent)
                .divide(ONE_HUNDRED, 2, RoundingMode.HALF_UP)
                .setScale(2, RoundingMode.HALF_UP);

        // Tổng tiền sau giảm giá.
        BigDecimal finalAmount = grossAmount
                .subtract(discountAmount)
                .max(ZERO)
                .setScale(2, RoundingMode.HALF_UP);

        return new PricingResult(
                unitPrice,
                quantity,
                grossAmount,
                discountedQuantity,
                discountAmount,
                finalAmount);
    }

    private void validate(
            BigDecimal price,
            BigDecimal discountPercent,
            long quantity,
            long benefitQuantity,
            long usedBenefitQuantity) {

        if (price == null || price.compareTo(ZERO) < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Đơn giá không hợp lệ");
        }

        if (discountPercent != null
                && (discountPercent.compareTo(ZERO) < 0
                        || discountPercent.compareTo(ONE_HUNDRED) > 0)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Discount percent không hợp lệ");
        }

        if (quantity <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Số lượng sử dụng phải lớn hơn 0");
        }

        if (benefitQuantity < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Benefit quantity không hợp lệ");
        }

        if (usedBenefitQuantity < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Số lượng benefit đã sử dụng không hợp lệ");
        }
    }
}
