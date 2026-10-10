package com.project.booking.service;

import com.project.booking.model.BenefitConsumption;
import com.project.booking.repository.BenefitConsumptionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class BenefitConsumptionService {

    private final BenefitConsumptionRepository benefitConsumptionRepository;

    public BenefitConsumptionService(
            BenefitConsumptionRepository benefitConsumptionRepository) {
        this.benefitConsumptionRepository = benefitConsumptionRepository;
    }

    @Transactional
    public BenefitConsumption getForUpdate(
            Long bookingId,
            UUID packageBenefitId) {

        benefitConsumptionRepository.createIfNotExists(
                bookingId,
                packageBenefitId);

        return benefitConsumptionRepository
                .findForUpdate(
                        bookingId,
                        packageBenefitId)
                .orElseThrow(() -> new IllegalStateException(
                        "Không tìm thấy BenefitConsumption"));
    }

    @Transactional
    public void consume(
            BenefitConsumption benefitConsumption,
            long quantity) {

        if (quantity <= 0) {
            return;
        }

        int currentUsedQuantity = benefitConsumption.getUsedQuantity() == null
                ? 0
                : benefitConsumption.getUsedQuantity();

        benefitConsumption.setUsedQuantity(
                currentUsedQuantity + (int) quantity);

        benefitConsumptionRepository.save(
                benefitConsumption);
    }
}