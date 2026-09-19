package com.project.booking.repository;

import com.project.booking.model.BookingPassenger;
import com.project.booking.model.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface BookingPassengerRepository extends JpaRepository<BookingPassenger, Long> {
    List<BookingPassenger> findAllByBooking_IdOrderByIdAsc(Long bookingId);

    List<BookingPassenger> findByBooking_CreatedByUserId(Long userId);

    // Kiểm tra xem hành khách này có đang thuộc bất kỳ đơn hàng CONFIRMED nào không
    boolean existsByPassenger_IdAndBooking_Status(Long passengerId, BookingStatus status);

    // Tìm bản ghi liên kết giữa booking và passenger cụ thể
    Optional<BookingPassenger> findByBooking_IdAndPassenger_Id(Long bookingId, Long passengerId);

}