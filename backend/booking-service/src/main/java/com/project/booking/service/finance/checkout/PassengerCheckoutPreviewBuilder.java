package com.project.booking.service.finance.checkout;

import com.project.booking.dto.finance.CheckoutPreviewResponse.PassengerCheckoutPreview;
import com.project.booking.dto.finance.CheckoutPreviewResponse.UsageItem;
import com.project.booking.mapper.CheckoutPreviewMapper;
import com.project.booking.model.BookingPassenger;
import com.project.booking.service.finance.checkout.PassengerUsageReader.PassengerUsages;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class PassengerCheckoutPreviewBuilder {

        private final PassengerUsageReader passengerUsageReader;
        private final CheckoutPreviewMapper checkoutPreviewMapper;

        public PassengerCheckoutPreviewBuilder(
                        PassengerUsageReader passengerUsageReader,
                        CheckoutPreviewMapper checkoutPreviewMapper) {
                this.passengerUsageReader = passengerUsageReader;
                this.checkoutPreviewMapper = checkoutPreviewMapper;
        }

        public PassengerCheckoutPreview build(BookingPassenger bookingPassenger) {

                Long bookingPassengerId = bookingPassenger.getId();
                PassengerUsages usages = passengerUsageReader.read(bookingPassengerId);

                List<UsageItem> activityVisitItems = usages.activityVisits().stream()
                                .map(checkoutPreviewMapper::toUsageItem).toList();
                List<UsageItem> activityCruiseItems = usages.activityCruises().stream()
                                .map(checkoutPreviewMapper::toUsageItem).toList();
                List<UsageItem> serviceItems = usages.services().stream()
                                .map(checkoutPreviewMapper::toUsageItem).toList();
                List<UsageItem> productItems = usages.products().stream()
                                .map(checkoutPreviewMapper::toUsageItem).toList();

                BigDecimal passengerTotal = calculateTotal(activityVisitItems)
                                .add(calculateTotal(activityCruiseItems))
                                .add(calculateTotal(serviceItems))
                                .add(calculateTotal(productItems));

                Long passengerId = bookingPassenger.getPassenger().getId();

                return new PassengerCheckoutPreview(
                                bookingPassengerId,
                                passengerId,
                                activityVisitItems,
                                activityCruiseItems,
                                serviceItems,
                                productItems,
                                passengerTotal);
        }

        private BigDecimal calculateTotal(List<UsageItem> usages) {
                return usages.stream()
                                .map(UsageItem::finalAmount)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
}