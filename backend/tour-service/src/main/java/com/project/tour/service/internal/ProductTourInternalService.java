package com.project.tour.service.internal;

import com.project.tour.dto.internal.ProductUsageInfo;

import java.util.UUID;

public interface ProductTourInternalService {

    ProductUsageInfo getProductUsageInfo(
            UUID productTourId,
            UUID tourPackageId);
}