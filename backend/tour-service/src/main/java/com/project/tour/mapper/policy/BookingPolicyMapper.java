package com.project.tour.mapper.policy;

import com.project.tour.dto.policy.BookingPolicyResponse;
import com.project.tour.dto.policy.CreateBookingPolicyRequest;
import com.project.tour.dto.policy.UpdateBookingPolicyRequest;
import com.project.tour.model.BookingPolicy;
import com.project.tour.model.Policy;

public class BookingPolicyMapper {

    private BookingPolicyMapper() {
    }

    public static BookingPolicy toEntity(
            CreateBookingPolicyRequest request,
            Policy policy) {

        BookingPolicy entity = new BookingPolicy();

        entity.setPolicy(policy);
        entity.setDaysBeforeDeparture(
                request.getDaysBeforeDeparture());
        entity.setDiscountPercent(
                request.getDiscountPercent());

        return entity;
    }

    public static void updateEntity(
            BookingPolicy entity,
            UpdateBookingPolicyRequest request) {

        entity.setDaysBeforeDeparture(
                request.getDaysBeforeDeparture());
        entity.setDiscountPercent(
                request.getDiscountPercent());
        entity.setStatus(
                request.getStatus());
    }

    public static BookingPolicyResponse toResponse(
            BookingPolicy entity) {

        BookingPolicyResponse response = new BookingPolicyResponse();

        response.setId(entity.getId());
        response.setPolicyId(entity.getPolicy().getId());
        response.setDaysBeforeDeparture(
                entity.getDaysBeforeDeparture());
        response.setDiscountPercent(
                entity.getDiscountPercent());
        response.setStatus(
                entity.getStatus());

        return response;
    }
}