package com.project.booking.service;

import com.project.booking.dto.convenience.product.ProductUsageRequest;
import com.project.booking.dto.convenience.product.ProductUsageResponse;

import java.util.List;

public interface ProductUsageService {

    ProductUsageResponse create(ProductUsageRequest request);

    List<ProductUsageResponse> getByBookingPassengerId(
            Long bookingPassengerId);

    List<ProductUsageResponse> getByUserId(Long userId);
}