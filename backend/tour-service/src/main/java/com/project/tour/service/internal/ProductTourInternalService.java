package com.project.tour.service.internal;

import com.project.tour.dto.internal.ProductUsageInfo;
import com.project.tour.dto.internal.ProductUsageManagementInfo;
import java.util.List;
import java.util.UUID;

public interface ProductTourInternalService {

    ProductUsageInfo getProductUsageInfo(
            UUID productTourId,
            UUID tourPackageId);

    List<ProductUsageManagementInfo> getProductUsageManagementInfo(
            List<UUID> productTourIds);
}