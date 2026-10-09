package com.project.booking.service.service;

import com.project.booking.client.ServiceTourClient.ServiceUsageInfo;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class ServicePricingCalculator {

    // Mỗi lần ServiceUsage tương ứng 1 passenger
    private static final int QUANTITY = 1;

    public record PriceCalculationResult(
            BigDecimal unitPrice,
            BigDecimal discountAmount,
            BigDecimal finalAmount,
            long freeUsage) {
    }

    public PriceCalculationResult calculate(ServiceUsageInfo serviceInfo, long usedBenefitQuantity) {

        int benefitQuantity = serviceInfo.benefitQuantity() == null
                ? 0
                : Math.max(serviceInfo.benefitQuantity(), 0);

        long remainingFreeQuantity = Math.max((long) benefitQuantity - usedBenefitQuantity, 0);
        long freeUsage = Math.min(remainingFreeQuantity, QUANTITY);
        long paidUsage = QUANTITY - freeUsage;

        BigDecimal unitPrice = serviceInfo.unitPrice().setScale(2, RoundingMode.HALF_UP);
        BigDecimal grossAmount = unitPrice.multiply(BigDecimal.valueOf(QUANTITY));

        // Miễn phí theo benefit
        BigDecimal freeDiscountAmount = unitPrice.multiply(BigDecimal.valueOf(freeUsage));

        // Discount percent
        BigDecimal discountPercent = serviceInfo.discountPercent() == null
                ? BigDecimal.ZERO
                : serviceInfo.discountPercent();

        if (discountPercent.compareTo(BigDecimal.ZERO) < 0
                || discountPercent.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Discount percent không hợp lệ");
        }

        BigDecimal paidAmountBeforePercent = unitPrice.multiply(BigDecimal.valueOf(paidUsage));
        BigDecimal percentDiscountAmount = paidAmountBeforePercent
                .multiply(discountPercent)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        BigDecimal discountAmount = freeDiscountAmount
                .add(percentDiscountAmount)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal finalAmount = grossAmount
                .subtract(discountAmount)
                .max(BigDecimal.ZERO)
                .setScale(2, RoundingMode.HALF_UP);

        return new PriceCalculationResult(unitPrice, discountAmount, finalAmount, freeUsage);
    }
}