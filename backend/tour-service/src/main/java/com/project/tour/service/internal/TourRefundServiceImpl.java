package com.project.tour.service.internal;

import com.project.tour.dto.internal.RefundCalculationResponse;
import com.project.tour.model.admin.Policy;
import com.project.tour.model.admin.PolicyRefundRule;
import com.project.tour.model.enums.policy.PolicyStatus;
import com.project.tour.model.enums.policy.PolicyType;
import com.project.tour.model.scheduler.Tour;
import com.project.tour.repository.policy.PolicyRefundRuleRepository;
import com.project.tour.repository.policy.PolicyRepository;
import com.project.tour.repository.scheduler.TourRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class TourRefundServiceImpl implements TourRefundService {

        private final TourRepository tourRepository;
        private final PolicyRepository policyRepository;
        private final PolicyRefundRuleRepository policyRefundRuleRepository;

        public TourRefundServiceImpl(
                        TourRepository tourRepository,
                        PolicyRepository policyRepository,
                        PolicyRefundRuleRepository policyRefundRuleRepository) {
                this.tourRepository = tourRepository;
                this.policyRepository = policyRepository;
                this.policyRefundRuleRepository = policyRefundRuleRepository;
        }

        @Override
        public RefundCalculationResponse calculateRefund(
                        UUID tourId,
                        BigDecimal originalAmount) {
                // 1. Validate số tiền booking
                if (originalAmount == null
                                || originalAmount.compareTo(BigDecimal.ZERO) <= 0) {
                        throw new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "Original booking amount must be greater than zero");
                }

                // 2. Tìm tour
                Tour tour = tourRepository.findById(tourId)
                                .orElseThrow(() -> new ResponseStatusException(
                                                HttpStatus.NOT_FOUND,
                                                "Tour not found: " + tourId));

                LocalDate departureDate = tour.getStartDate();

                if (departureDate == null) {
                        throw new ResponseStatusException(
                                        HttpStatus.CONFLICT,
                                        "Tour departure date is not configured");
                }

                // 3. Tính số ngày còn lại trước khởi hành
                // 3. Chỉ cho phép hoàn tiền trước ngày khởi hành
                LocalDate today = LocalDate.now();

                if (!today.isBefore(departureDate)) {
                        throw new ResponseStatusException(
                                        HttpStatus.CONFLICT,
                                        "Tour has already departed or is departing today. Refund is not allowed.");
                }

                long days = ChronoUnit.DAYS.between(today, departureDate);

                int daysBeforeDeparture = Math.toIntExact(days);

                // 4. Tìm Policy CANCEL đang ACTIVE
                List<Policy> activePolicies = policyRepository.findAllByTypeAndStatusOrderByCreatedAtDesc(
                                PolicyType.CANCEL,
                                PolicyStatus.ACTIVE);

                if (activePolicies.isEmpty()) {
                        throw new ResponseStatusException(
                                        HttpStatus.CONFLICT,
                                        "No active cancellation policy is configured");
                }

                if (activePolicies.size() > 1) {
                        throw new ResponseStatusException(
                                        HttpStatus.CONFLICT,
                                        "Multiple active cancellation policies found");
                }

                Policy policy = activePolicies.get(0);

                // 5. Lấy các rule thuộc Policy
                List<PolicyRefundRule> rules = policyRefundRuleRepository
                                .findByPolicy_IdOrderByMinDaysBeforeDepartureAsc(
                                                policy.getId());

                if (rules.isEmpty()) {
                        throw new ResponseStatusException(
                                        HttpStatus.CONFLICT,
                                        "No refund rules configured for the active cancellation policy");
                }

                // 6. Tìm rule phù hợp với số ngày còn lại
                List<PolicyRefundRule> matchedRules = rules.stream()
                                .filter(rule -> daysBeforeDeparture >= rule.getMinDaysBeforeDeparture()
                                                && (rule.getMaxDaysBeforeDeparture() == null
                                                                || daysBeforeDeparture <= rule
                                                                                .getMaxDaysBeforeDeparture()))
                                .toList();

                if (matchedRules.isEmpty()) {
                        throw new ResponseStatusException(
                                        HttpStatus.CONFLICT,
                                        "No refund rule matches the days before departure: "
                                                        + daysBeforeDeparture);
                }

                if (matchedRules.size() > 1) {
                        throw new ResponseStatusException(
                                        HttpStatus.CONFLICT,
                                        "Multiple refund rules match the days before departure: "
                                                        + daysBeforeDeparture);
                }

                PolicyRefundRule matchedRule = matchedRules.get(0);

                // 7. Kiểm tra tỷ lệ hoàn tiền
                BigDecimal refundPercentage = matchedRule.getRefundPercentage();

                if (refundPercentage == null
                                || refundPercentage.compareTo(BigDecimal.ZERO) < 0
                                || refundPercentage.compareTo(new BigDecimal("100")) > 0) {
                        throw new ResponseStatusException(
                                        HttpStatus.CONFLICT,
                                        "Invalid refund percentage in policy configuration");
                }

                // 8. Tính số tiền hoàn
                BigDecimal calculatedRefundAmount = originalAmount
                                .multiply(refundPercentage)
                                .divide(
                                                new BigDecimal("100"),
                                                2,
                                                RoundingMode.HALF_UP);

                // 9. Trả kết quả cho booking-service
                return new RefundCalculationResponse(
                                matchedRule.getId(),
                                departureDate,
                                daysBeforeDeparture,
                                refundPercentage,
                                calculatedRefundAmount);
        }
}