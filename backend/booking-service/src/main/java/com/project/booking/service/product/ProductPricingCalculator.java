package com.project.booking.service.product;

import com.project.booking.client.TourProductClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class ProductPricingCalculator {

    public record PriceCalculationResult(
            BigDecimal unitPrice,
            BigDecimal discountAmount,
            BigDecimal finalAmount,
            long freeQuantityThisUsage) {
    }

    public PriceCalculationResult calculate(
            TourProductClient.ProductUsageInfo productInfo,
            long usedBenefitQuantity,
            long quantity) {

        int benefitQuantity = productInfo.benefitQuantity() == null
                ? 0
                : Math.max(productInfo.benefitQuantity(), 0);

        // Số lượng được miễn phí trong lần sử dụng này
        long remainingFreeQuantity = Math.max((long) benefitQuantity - usedBenefitQuantity, 0);
        long freeQuantityThisUsage = Math.min(remainingFreeQuantity, quantity);
        long paidQuantityThisUsage = quantity - freeQuantityThisUsage;

        // Tổng tiền trước discount
        BigDecimal unitPrice = productInfo.unitPrice().setScale(2, RoundingMode.HALF_UP);
        BigDecimal grossAmount = unitPrice.multiply(BigDecimal.valueOf(quantity));

        // Discount từ benefit quantity
        BigDecimal freeDiscountAmount = unitPrice.multiply(BigDecimal.valueOf(freeQuantityThisUsage));

        // Discount percent
        BigDecimal discountPercent = productInfo.discountPercent() == null
                ? BigDecimal.ZERO
                : productInfo.discountPercent();

        if (discountPercent.compareTo(BigDecimal.ZERO) < 0
                || discountPercent.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Discount percent không hợp lệ");
        }

        BigDecimal paidAmountBeforePercent = unitPrice.multiply(BigDecimal.valueOf(paidQuantityThisUsage));
        BigDecimal percentDiscountAmount = paidAmountBeforePercent
                .multiply(discountPercent)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        // Tổng discount + final amount
        BigDecimal discountAmount = freeDiscountAmount
                .add(percentDiscountAmount)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal finalAmount = grossAmount
                .subtract(discountAmount)
                .max(BigDecimal.ZERO)
                .setScale(2, RoundingMode.HALF_UP);

        return new PriceCalculationResult(unitPrice, discountAmount, finalAmount, freeQuantityThisUsage);
    }
}