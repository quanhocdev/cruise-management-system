
package com.project.booking.service.passenger;

import com.project.booking.dto.passenger.benefit.PassengerBenefitDTO;
import com.project.booking.dto.passenger.bill.PassengerBillDTO;
import com.project.booking.dto.passenger.booking.PassengerBookingDTO;
import com.project.booking.dto.passenger.passenger.PassengerBookingPassengerDTO;
import com.project.booking.dto.passenger.usage.PassengerActivityCruiseUsageDTO;
import com.project.booking.dto.passenger.usage.PassengerActivityVisitUsageDTO;
import com.project.booking.dto.passenger.usage.PassengerProductUsageDTO;
import com.project.booking.dto.passenger.usage.PassengerServiceUsageDTO;

import com.project.booking.mapper.passenger.BenefitConsumptionMapper;
import com.project.booking.mapper.passenger.BillMapper;
import com.project.booking.mapper.passenger.BookingMapper;
import com.project.booking.mapper.passenger.BookingPassengerMapper;
import com.project.booking.mapper.passenger.PassengerMapper;
import com.project.booking.mapper.passenger.UsageMapper;

import com.project.booking.model.BenefitConsumption;
import com.project.booking.model.Bill;
import com.project.booking.model.BillItem;
import com.project.booking.model.Booking;
import com.project.booking.model.BookingPassenger;
import com.project.booking.repository.ActivityCruiseUsageRepository;
import com.project.booking.repository.ActivityVisitUsageRepository;
import com.project.booking.repository.BenefitConsumptionRepository;
import com.project.booking.repository.BillItemRepository;
import com.project.booking.repository.BillRepository;
import com.project.booking.repository.BookingPassengerRepository;
import com.project.booking.repository.BookingRepository;
import com.project.booking.repository.ProductUsageRepository;
import com.project.booking.repository.ServiceUsageRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class PassengerTripDetailService {

    private final BookingRepository bookingRepository;
    private final BookingPassengerRepository bookingPassengerRepository;
    private final ProductUsageRepository productUsageRepository;
    private final ServiceUsageRepository serviceUsageRepository;
    private final ActivityCruiseUsageRepository activityCruiseUsageRepository;
    private final ActivityVisitUsageRepository activityVisitUsageRepository;
    private final BenefitConsumptionRepository benefitConsumptionRepository;
    private final BillRepository billRepository;
    private final BillItemRepository billItemRepository;

    private final BookingMapper bookingMapper = new BookingMapper();
    private final PassengerMapper passengerMapper = new PassengerMapper();
    private final BookingPassengerMapper bookingPassengerMapper = new BookingPassengerMapper(passengerMapper);
    private final UsageMapper usageMapper = new UsageMapper();
    private final BenefitConsumptionMapper benefitConsumptionMapper = new BenefitConsumptionMapper();
    private final BillMapper billMapper = new BillMapper();

    public PassengerTripDetailService(
            BookingRepository bookingRepository,
            BookingPassengerRepository bookingPassengerRepository,
            ProductUsageRepository productUsageRepository,
            ServiceUsageRepository serviceUsageRepository,
            ActivityCruiseUsageRepository activityCruiseUsageRepository,
            ActivityVisitUsageRepository activityVisitUsageRepository,
            BenefitConsumptionRepository benefitConsumptionRepository,
            BillRepository billRepository,
            BillItemRepository billItemRepository) {

        this.bookingRepository = bookingRepository;
        this.bookingPassengerRepository = bookingPassengerRepository;
        this.productUsageRepository = productUsageRepository;
        this.serviceUsageRepository = serviceUsageRepository;
        this.activityCruiseUsageRepository = activityCruiseUsageRepository;
        this.activityVisitUsageRepository = activityVisitUsageRepository;
        this.benefitConsumptionRepository = benefitConsumptionRepository;
        this.billRepository = billRepository;
        this.billItemRepository = billItemRepository;
    }

    /**
     * Lấy booking thuộc user đang đăng nhập.
     */
    private Booking getOwnedBooking(Long bookingId, UUID userId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy booking."));

        if (!booking.getCreatedByUserId().equals(userId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Bạn không có quyền truy cập booking này.");
        }

        return booking;
    }

    /**
     * Thông tin tổng quan booking.
     */
    public PassengerBookingDTO getBookingDetail(
            Long bookingId,
            UUID userId) {

        Booking booking = getOwnedBooking(bookingId, userId);

        return bookingMapper.toDTO(booking);
    }

    /**
     * Danh sách hành khách thuộc booking.
     */
    public List<PassengerBookingPassengerDTO> getPassengers(
            Long bookingId,
            UUID userId) {

        getOwnedBooking(bookingId, userId);

        return bookingPassengerRepository
                .findAllByBooking_IdOrderByIdAsc(bookingId)
                .stream()
                .map(bookingPassengerMapper::toDTO)
                .toList();
    }

    /**
     * Lấy các BookingPassenger thuộc booking.
     * Dùng để truy vấn lịch sử sử dụng theo từng hành khách.
     */
    private List<BookingPassenger> getBookingPassengers(Long bookingId) {
        return bookingPassengerRepository
                .findAllByBooking_IdOrderByIdAsc(bookingId);
    }

    /**
     * Lịch sử sử dụng sản phẩm.
     */
    public List<PassengerProductUsageDTO> getProductUsages(
            Long bookingId,
            UUID userId) {

        getOwnedBooking(bookingId, userId);

        List<PassengerProductUsageDTO> result = new ArrayList<>();

        for (BookingPassenger passenger : getBookingPassengers(bookingId)) {
            result.addAll(
                    usageMapper.toProductUsageDTOs(
                            productUsageRepository
                                    .findAllByBookingPassenger_IdOrderByUsedAtDesc(
                                            passenger.getId())));
        }

        return result;
    }

    /**
     * Lịch sử sử dụng dịch vụ.
     */
    public List<PassengerServiceUsageDTO> getServiceUsages(
            Long bookingId,
            UUID userId) {

        getOwnedBooking(bookingId, userId);

        List<PassengerServiceUsageDTO> result = new ArrayList<>();

        for (BookingPassenger passenger : getBookingPassengers(bookingId)) {
            result.addAll(
                    usageMapper.toServiceUsageDTOs(
                            serviceUsageRepository
                                    .findAllByBookingPassenger_IdOrderByUsedAtDesc(
                                            passenger.getId())));
        }

        return result;
    }

    /**
     * Lịch sử sử dụng hoạt động trên tàu.
     */
    public List<PassengerActivityCruiseUsageDTO> getActivityCruiseUsages(
            Long bookingId,
            UUID userId) {

        getOwnedBooking(bookingId, userId);

        List<PassengerActivityCruiseUsageDTO> result = new ArrayList<>();

        for (BookingPassenger passenger : getBookingPassengers(bookingId)) {
            result.addAll(
                    usageMapper.toActivityCruiseUsageDTOs(
                            activityCruiseUsageRepository
                                    .findAllByBookingPassengerIdOrderByUsedAtDesc(
                                            passenger.getId())));
        }

        return result;
    }

    /**
     * Lịch sử sử dụng hoạt động tham quan.
     */
    public List<PassengerActivityVisitUsageDTO> getActivityVisitUsages(
            Long bookingId,
            UUID userId) {

        getOwnedBooking(bookingId, userId);

        List<PassengerActivityVisitUsageDTO> result = new ArrayList<>();

        for (BookingPassenger passenger : getBookingPassengers(bookingId)) {
            result.addAll(
                    usageMapper.toActivityVisitUsageDTOs(
                            activityVisitUsageRepository
                                    .findAllByBookingPassengerIdOrderByUsedAtDesc(
                                            passenger.getId())));
        }

        return result;
    }

    /**
     * Quyền lợi và số lượng đã sử dụng của booking.
     */
    public PassengerBenefitDTO getBenefits(
            Long bookingId,
            UUID userId) {

        getOwnedBooking(bookingId, userId);

        List<BenefitConsumption> consumptions = benefitConsumptionRepository.findAll()
                .stream()
                .filter(item -> bookingId.equals(item.getBookingId()))
                .toList();

        return benefitConsumptionMapper.toBenefitDTO(
                bookingId,
                consumptions);
    }

    /**
     * Danh sách hóa đơn của booking, bao gồm các BillItem.
     */
    public List<PassengerBillDTO> getBills(
            Long bookingId,
            UUID userId) {

        getOwnedBooking(bookingId, userId);

        List<Bill> bills = billRepository
                .findAllByBooking_IdOrderByCreatedAtDesc(bookingId);

        List<BillItem> items = new ArrayList<>();

        for (Bill bill : bills) {
            items.addAll(
                    billItemRepository.findAllByBill_IdOrderByIdAsc(
                            bill.getId()));
        }

        return billMapper.toDTOs(bills, items);
    }
}
