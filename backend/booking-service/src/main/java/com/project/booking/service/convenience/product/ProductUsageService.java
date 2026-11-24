
package com.project.booking.service.convenience.product;

import java.util.UUID;
import java.util.List;

import com.project.booking.dto.convenience.product.ProductUsageManagementResponse;
import com.project.booking.dto.convenience.product.ProductUsageRequest;
import com.project.booking.dto.convenience.product.ProductUsageResponse;

public interface ProductUsageService {

    ProductUsageResponse create(ProductUsageRequest request, UUID actorId);

    List<ProductUsageResponse> getByBookingPassengerId(
            Long bookingPassengerId);

    List<ProductUsageResponse> getByUserId(UUID userId);

    List<ProductUsageManagementResponse> getManagementUsages();
}
