package com.project.booking.service;

import com.project.booking.dto.ProductUsageRequest;
import com.project.booking.dto.ProductUsageResponse;

import java.util.List;

public interface ProductUsageService {

    ProductUsageResponse create(ProductUsageRequest request);

    List<ProductUsageResponse> getByBookingPassengerId(
            Long bookingPassengerId);
}