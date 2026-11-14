
package com.project.booking.repository;

import com.project.booking.model.BookingRefund;
import com.project.booking.model.enums.BookingRefundStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BookingRefundRepository extends JpaRepository<BookingRefund, Long> {

    // Lấy toàn bộ yêu cầu hoàn tiền của một booking
    List<BookingRefund> findByBookingIdOrderByCreatedAtDesc(Long bookingId);

    // Lấy yêu cầu hoàn tiền cụ thể của một booking
    Optional<BookingRefund> findFirstByBookingIdOrderByCreatedAtDesc(Long bookingId);

    // Lấy lịch sử yêu cầu hoàn tiền của một khách hàng
    List<BookingRefund> findByRequestedByUserIdOrderByCreatedAtDesc(UUID requestedByUserId);

    // Kiểm tra booking có yêu cầu hoàn tiền đang chờ xử lý hay không
    boolean existsByBookingIdAndStatusIn(
            Long bookingId,
            List<BookingRefundStatus> statuses);

    // Lấy danh sách yêu cầu theo trạng thái
    List<BookingRefund> findByStatusOrderByCreatedAtAsc(BookingRefundStatus status);

    Optional<BookingRefund> findByPaymentClientRequestId(UUID paymentClientRequestId);
}
