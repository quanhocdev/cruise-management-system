package com.project.booking.service.shore;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Component;

@Component
public class ActivityVisitUsageCalculator {

        private static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);

        public UsageAmount calculate(
                        BigDecimal price,
                        BigDecimal discountPercent,
                        int quantity,
                        int benefitQuantity,
                        long usedBenefitQuantity) {

                BigDecimal percent = discountPercent == null ? BigDecimal.ZERO : discountPercent;

                long remainingFree = Math.max(benefitQuantity - usedBenefitQuantity, 0);
                long freeQuantity = Math.min(remainingFree, quantity);
                long paidQuantity = quantity - freeQuantity;

                BigDecimal unitPrice = price.setScale(2, RoundingMode.HALF_UP);
                BigDecimal grossAmount = unitPrice.multiply(BigDecimal.valueOf(quantity));

                BigDecimal freeDiscount = unitPrice.multiply(BigDecimal.valueOf(freeQuantity));

                BigDecimal percentDiscount = unitPrice
                                .multiply(BigDecimal.valueOf(paidQuantity))
                                .multiply(percent)
                                .divide(ONE_HUNDRED, 2, RoundingMode.HALF_UP);

                BigDecimal discountAmount = freeDiscount
                                .add(percentDiscount)
                                .setScale(2, RoundingMode.HALF_UP);

                BigDecimal finalAmount = grossAmount
                                .subtract(discountAmount)
                                .max(BigDecimal.ZERO)
                                .setScale(2, RoundingMode.HALF_UP);

                return new UsageAmount(
                                unitPrice,
                                freeQuantity,
                                paidQuantity,
                                grossAmount,
                                discountAmount,
                                finalAmount);
        }

        public record UsageAmount(
                        BigDecimal unitPrice,
                        long freeQuantity,
                        long paidQuantity,
                        BigDecimal grossAmount,
                        BigDecimal discountAmount,
                        BigDecimal finalAmount) {
        }
}