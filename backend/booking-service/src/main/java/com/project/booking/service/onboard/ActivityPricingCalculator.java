package com.project.booking.service.onboard;

import com.project.booking.client.ActivityCruiseClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class ActivityPricingCalculator {

    public record PriceCalculationResult(
            BigDecimal unitPrice,
            BigDecimal discountAmount,
            BigDecimal finalAmount,
            long freeQuantityThisUsage) {
    }

    public PriceCalculationResult calculate(ActivityCruiseClient.ActivityCruiseUsageInfo activityInfo,
            long usedBenefitQuantity) {
        int quantity = 1;

        if (activityInfo.price() == null || activityInfo.price().compareTo(BigDecimal.ZERO) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Giá Activity Cruise không hợp lệ");
        }

        int benefitQuantity = activityInfo.benefitQuantity() == null ? 0 : Math.max(activityInfo.benefitQuantity(), 0);
        long remainingFreeQuantity = Math.max((long) benefitQuantity - usedBenefitQuantity, 0);
        long freeQuantityThisUsage = Math.min(remainingFreeQuantity, quantity);
        long paidQuantityThisUsage = quantity - freeQuantityThisUsage;

        BigDecimal unitPrice = activityInfo.price().setScale(2, RoundingMode.HALF_UP);
        BigDecimal grossAmount = unitPrice.multiply(BigDecimal.valueOf(quantity));

        BigDecimal freeDiscountAmount = unitPrice.multiply(BigDecimal.valueOf(freeQuantityThisUsage));

        BigDecimal discountPercent = activityInfo.discountPercent() == null ? BigDecimal.ZERO
                : activityInfo.discountPercent();
        if (discountPercent.compareTo(BigDecimal.ZERO) < 0 || discountPercent.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Discount percent không hợp lệ");
        }

        BigDecimal paidAmountBeforePercent = unitPrice.multiply(BigDecimal.valueOf(paidQuantityThisUsage));
        BigDecimal percentDiscountAmount = paidAmountBeforePercent
                .multiply(discountPercent)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        BigDecimal discountAmount = freeDiscountAmount.add(percentDiscountAmount).setScale(2, RoundingMode.HALF_UP);
        BigDecimal finalAmount = grossAmount.subtract(discountAmount).max(BigDecimal.ZERO).setScale(2,
                RoundingMode.HALF_UP);

        return new PriceCalculationResult(unitPrice, discountAmount, finalAmount, freeQuantityThisUsage);
    }
}