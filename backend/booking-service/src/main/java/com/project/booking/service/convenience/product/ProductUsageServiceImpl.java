
package com.project.booking.service.convenience.product;

import com.project.booking.client.ProductTourManagementClient;
import com.project.booking.client.ProductTourManagementClient.ProductUsageManagementInfo;
import com.project.booking.client.TourProductClient;
import com.project.booking.dto.convenience.product.ProductUsageManagementResponse;
import com.project.booking.dto.convenience.product.ProductUsageRequest;
import com.project.booking.dto.convenience.product.ProductUsageResponse;
import com.project.booking.kafka.ProductUsedEventProducer;
import com.project.booking.mapper.ProductUsageMapper;
import com.project.booking.model.BenefitConsumption;
import com.project.booking.model.Booking;
import com.project.booking.model.BookingPassenger;
import com.project.booking.model.PosCommand;
import com.project.booking.model.ProductUsage;
import com.project.booking.repository.PosCommandRepository;
import com.project.booking.repository.ProductUsageRepository;
import com.project.booking.service.common.benefit.BenefitConsumptionService;
import com.project.booking.service.common.pricing.PricingCalculator;
import com.project.common.event.ProductUsedEvent;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ProductUsageServiceImpl implements ProductUsageService {

        private static final String OPERATION_PRODUCT_USAGE = "PRODUCT_USAGE";

        private final ProductUsageRepository productUsageRepository;
        private final PosCommandRepository posCommandRepository;
        private final ProductUsageMapper productUsageMapper;
        private final TourProductClient tourProductClient;
        private final BenefitConsumptionService benefitConsumptionService;
        private final ProductUsedEventProducer productUsedEventProducer;
        private final ProductValidator productValidator;
        private final PricingCalculator pricingCalculator;
        private final ProductTourManagementClient productTourManagementClient;

        public ProductUsageServiceImpl(
                        ProductUsageRepository productUsageRepository,
                        PosCommandRepository posCommandRepository,
                        ProductUsageMapper productUsageMapper,
                        TourProductClient tourProductClient,
                        BenefitConsumptionService benefitConsumptionService,
                        ProductUsedEventProducer productUsedEventProducer,
                        ProductValidator productValidator,
                        PricingCalculator pricingCalculator,
                        ProductTourManagementClient productTourManagementClient) {

                this.productUsageRepository = productUsageRepository;
                this.posCommandRepository = posCommandRepository;
                this.productUsageMapper = productUsageMapper;
                this.tourProductClient = tourProductClient;
                this.benefitConsumptionService = benefitConsumptionService;
                this.productUsedEventProducer = productUsedEventProducer;
                this.productValidator = productValidator;
                this.pricingCalculator = pricingCalculator;
                this.productTourManagementClient = productTourManagementClient;
        }

        @Override
        @Transactional
        public ProductUsageResponse create(
                        ProductUsageRequest request,
                        UUID actorId) {

                if (actorId == null) {
                        throw new ResponseStatusException(
                                        HttpStatus.UNAUTHORIZED,
                                        "Không xác định được nhân viên thực hiện giao dịch.");
                }

                if (request.clientRequestId() == null) {
                        throw new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "Client request ID không được để trống.");
                }

                // 1. Tạo dấu nhận diện giao dịch.
                String payloadHash = calculatePayloadHash(request);

                // 2. Đăng ký giao dịch bằng unique constraint.
                // Nếu request khác đang xử lý cùng UUID,
                // PostgreSQL sẽ xử lý xung đột unique constraint.
                int inserted = posCommandRepository.insertIfAbsent(
                                UUID.randomUUID(),
                                request.clientRequestId(),
                                actorId,
                                OPERATION_PRODUCT_USAGE,
                                payloadHash);

                // 3. Khóa và đọc PosCommand.
                PosCommand command = posCommandRepository
                                .findByClientRequestIdForUpdate(request.clientRequestId())
                                .orElseThrow(() -> new IllegalStateException(
                                                "Không tìm thấy POS command sau khi đăng ký."));

                // 4. Không cho phép sử dụng lại UUID của nhân viên khác.
                if (!actorId.equals(command.getActorId())) {
                        throw new ResponseStatusException(
                                        HttpStatus.CONFLICT,
                                        "Mã giao dịch đã được sử dụng bởi tài khoản khác.");
                }

                // 5. Không cho phép dùng cùng UUID với nội dung khác.
                if (!OPERATION_PRODUCT_USAGE.equals(command.getOperation())
                                || !payloadHash.equals(command.getPayloadHash())) {

                        throw new ResponseStatusException(
                                        HttpStatus.CONFLICT,
                                        "Mã giao dịch đã tồn tại với nội dung khác.");
                }

                // 6. Nếu giao dịch đã hoàn tất, trả lại kết quả cũ.
                if (command.getStatus() == PosCommand.CommandStatus.COMPLETED) {

                        if (command.getProductUsageId() == null) {
                                throw new IllegalStateException(
                                                "POS command đã hoàn tất nhưng thiếu ProductUsage ID.");
                        }

                        ProductUsage existingUsage = productUsageRepository
                                        .findById(command.getProductUsageId())
                                        .orElseThrow(() -> new IllegalStateException(
                                                        "Không tìm thấy ProductUsage của giao dịch đã hoàn tất."));

                        return productUsageMapper.toResponse(existingUsage);
                }

                // 7. PROCESSING không được xử lý lại tùy tiện.
                // Với thiết kế cùng transaction, trạng thái này
                // bình thường chưa được commit riêng lẻ.
                if (command.getStatus() != PosCommand.CommandStatus.PROCESSING) {
                        throw new ResponseStatusException(
                                        HttpStatus.CONFLICT,
                                        "Trạng thái giao dịch POS không hợp lệ.");
                }

                // Nếu insert bằng 0, đây là UUID đã tồn tại.
                // Việc đọc bản ghi đã khóa ở trên sẽ cho biết kết quả.
                // Không xử lý nghiệp vụ lần hai nếu command đã COMPLETED.

                // 8. Validate hành khách bằng NFC + booking.
                BookingPassenger bookingPassenger = productValidator
                                .validateAndGetPassenger(request.nfcCardUid());

                Booking booking = bookingPassenger.getBooking();
                productValidator.validateBooking(booking);

                // 9. Lấy thông tin ProductTour từ Tour Service.
                TourProductClient.ProductUsageInfo productInfo = tourProductClient.getProductUsageInfo(
                                request.productTourId(),
                                booking.getTourPackageId());

                // 10. Validate nghiệp vụ sản phẩm.
                productValidator.validateProduct(booking, productInfo);

                // 11. Lấy thông tin benefit.
                UUID packageBenefitId = productInfo.packageBenefitId();

                int benefitQuantity = productInfo.benefitQuantity() == null
                                ? 0
                                : Math.max(productInfo.benefitQuantity(), 0);

                BenefitConsumption benefitConsumption = null;
                long usedBenefitQuantity = 0;

                // 12. Lock BenefitConsumption để tránh sử dụng benefit đồng thời.
                if (packageBenefitId != null && benefitQuantity > 0) {

                        benefitConsumption = benefitConsumptionService.getForUpdate(
                                        booking.getId(),
                                        packageBenefitId);

                        usedBenefitQuantity = benefitConsumption.getUsedQuantity() == null
                                        ? 0
                                        : benefitConsumption.getUsedQuantity();
                }

                // 13. Tính giá bằng PricingCalculator dùng chung.
                var pricingResult = pricingCalculator.calculate(
                                productInfo.unitPrice(),
                                productInfo.discountPercent(),
                                request.quantity(),
                                benefitQuantity,
                                usedBenefitQuantity);

                // 14. Cập nhật số lượng benefit đã được áp dụng.
                if (benefitConsumption != null
                                && pricingResult.discountedQuantity() > 0) {

                        benefitConsumptionService.consume(
                                        benefitConsumption,
                                        pricingResult.discountedQuantity());
                }

                // 15. Lưu ProductUsage với snapshot giá tại thời điểm sử dụng.
                ProductUsage usage = new ProductUsage();
                usage.setBookingPassenger(bookingPassenger);
                usage.setProductTourId(request.productTourId());
                usage.setQuantity(request.quantity());
                usage.setUnitPrice(pricingResult.unitPrice());
                usage.setDiscountAmount(pricingResult.discountAmount());
                usage.setFinalAmount(pricingResult.finalAmount());
                usage.setUsedAt(LocalDateTime.now());

                ProductUsage savedUsage = productUsageRepository.save(usage);

                // 16. Lấy tên sản phẩm từ Tour Service.
                List<ProductUsageManagementInfo> productInfos = productTourManagementClient
                                .getProductUsageInfo(List.of(request.productTourId()));

                ProductUsageManagementInfo productUsageInfo = productInfos.stream()
                                .filter(info -> request.productTourId().equals(info.productTourId()))
                                .findFirst()
                                .orElseThrow(() -> new IllegalStateException(
                                                "Không tìm thấy thông tin sản phẩm: "
                                                                + request.productTourId()));

                // 17. Đánh dấu command hoàn tất trong cùng transaction.
                command.setProductUsageId(savedUsage.getId());
                command.setStatus(PosCommand.CommandStatus.COMPLETED);

                posCommandRepository.save(command);

                // 18. Chỉ gửi event cho giao dịch mới.
                productUsedEventProducer.sendAfterCommit(
                                new ProductUsedEvent(
                                                booking.getId(),
                                                bookingPassenger.getId(),
                                                booking.getCreatedByUserId(),
                                                request.productTourId(),
                                                savedUsage.getId(),
                                                productUsageInfo.productName(),
                                                request.quantity(),
                                                savedUsage.getUsedAt()));

                return productUsageMapper.toResponse(savedUsage);
        }

        private String calculatePayloadHash(ProductUsageRequest request) {

                String nfcCardUid = request.nfcCardUid();
                String productTourId = request.productTourId().toString();
                String quantity = request.quantity().toString();

                // Length-prefix để tránh nhập nhằng khi ghép các trường.
                String payload = nfcCardUid.length() + ":" + nfcCardUid
                                + productTourId.length() + ":" + productTourId
                                + quantity.length() + ":" + quantity;

                try {
                        MessageDigest digest = MessageDigest.getInstance("SHA-256");

                        byte[] hash = digest.digest(
                                        payload.getBytes(StandardCharsets.UTF_8));

                        return HexFormat.of().formatHex(hash);

                } catch (NoSuchAlgorithmException exception) {
                        throw new IllegalStateException(
                                        "Không thể tạo mã kiểm tra giao dịch.",
                                        exception);
                }
        }

        @Override
        @Transactional(readOnly = true)
        public List<ProductUsageResponse> getByBookingPassengerId(
                        Long bookingPassengerId) {

                return productUsageRepository
                                .findAllByBookingPassenger_IdOrderByUsedAtDesc(bookingPassengerId)
                                .stream()
                                .map(productUsageMapper::toResponse)
                                .toList();
        }

        @Override
        @Transactional(readOnly = true)
        public List<ProductUsageResponse> getByUserId(UUID userId) {

                return productUsageRepository
                                .findAllByBookingPassenger_Booking_CreatedByUserIdOrderByUsedAtDesc(userId)
                                .stream()
                                .map(productUsageMapper::toResponse)
                                .toList();
        }

        @Override
        @Transactional(readOnly = true)
        public List<ProductUsageManagementResponse> getManagementUsages() {

                List<ProductUsage> usages = productUsageRepository
                                .findAllByOrderByUsedAtDesc();

                if (usages.isEmpty()) {
                        return List.of();
                }

                List<UUID> productTourIds = usages.stream()
                                .map(ProductUsage::getProductTourId)
                                .filter(Objects::nonNull)
                                .distinct()
                                .toList();

                List<ProductUsageManagementInfo> productInfos = productTourManagementClient
                                .getProductUsageInfo(productTourIds);

                Map<UUID, ProductUsageManagementInfo> productInfoMap = productInfos.stream()
                                .collect(Collectors.toMap(
                                                ProductUsageManagementInfo::productTourId,
                                                Function.identity(),
                                                (first, second) -> first));

                return usages.stream()
                                .map(usage -> productUsageMapper.toManagementResponse(
                                                usage,
                                                productInfoMap.get(usage.getProductTourId())))
                                .toList();
        }
}
