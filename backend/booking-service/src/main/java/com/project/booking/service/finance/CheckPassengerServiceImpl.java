package com.project.booking.service.finance;

import com.project.booking.dto.finance.PassengerCheckInRequest;
import com.project.booking.exception.AppException;
import com.project.booking.model.BookingPassenger;
import com.project.booking.model.enums.BookingPassengerStatus;
import com.project.booking.model.enums.BookingStatus;
import com.project.booking.repository.BookingPassengerRepository;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Service
public class CheckPassengerServiceImpl implements CheckPassengerService {
    private final BookingPassengerRepository repository;
    private final EntityManager entityManager;
    private final RestClient tourClient;

    public CheckPassengerServiceImpl(BookingPassengerRepository repository, EntityManager entityManager,
            @Value("${tour-service.url}") String tourUrl) {
        this.repository = repository;
        this.entityManager = entityManager;
        this.tourClient = RestClient.builder().baseUrl(tourUrl).build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<?> getAvailableWristbands() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof JwtAuthenticationToken jwt)) {
            throw new AppException("Phiên đăng nhập không hợp lệ", HttpStatus.UNAUTHORIZED);
        }
        try {
            List<?> cards = tourClient.get().uri("/api/finance/available-wristbands")
                    .headers(h -> h.setBearerAuth(jwt.getToken().getTokenValue())).retrieve().body(List.class);
            var used = repository.findAllByStatus(BookingPassengerStatus.CHECKED_IN).stream()
                    .map(BookingPassenger::getNfcCardUid).collect(java.util.stream.Collectors.toSet());
            return cards == null ? List.of() : cards.stream()
                    .filter(item -> item instanceof Map<?, ?> m && !used.contains(m.get("cardUid"))).toList();
        } catch (RestClientException ex) {
            throw new AppException("Chưa tải được danh sách vòng NFC", HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    @Override
    @Transactional
    public void processSinglePassengerCheckIn(Long bookingId, PassengerCheckInRequest request) {
        if (request.getPassengerId() == null || request.getRoomId() == null
                || request.getNfcCode() == null || request.getNfcCode().isBlank()) {
            throw new AppException("Vui lòng chọn hành khách, phòng và vòng NFC", HttpStatus.BAD_REQUEST);
        }
        String uid = request.getNfcCode().trim();
        // Serialize assignments in this local PostgreSQL transaction, including concurrent requests.
        entityManager.createNativeQuery("select pg_advisory_xact_lock(9282026)").getSingleResult();
        BookingPassenger assignment = repository.findByBooking_IdAndPassenger_Id(bookingId, request.getPassengerId())
                .orElseThrow(() -> new AppException("Hành khách không thuộc booking này", HttpStatus.NOT_FOUND));
        if (assignment.getBooking().getStatus() != BookingStatus.CONFIRMED) {
            throw new AppException("Booking chưa được xác nhận", HttpStatus.CONFLICT);
        }
        if (assignment.getStatus() == BookingPassengerStatus.CHECKED_IN) {
            if (Objects.equals(assignment.getRoomId(), request.getRoomId()) && Objects.equals(assignment.getNfcCardUid(), uid)) return;
            throw new AppException("Khách đã check-in; không thể ghi đè phòng hoặc vòng NFC", HttpStatus.CONFLICT);
        }
        if (assignment.getStatus() != BookingPassengerStatus.PENDING) {
            throw new AppException("Trạng thái hành khách không cho phép check-in", HttpStatus.CONFLICT);
        }
        if (repository.existsByNfcCardUidAndStatus(uid, BookingPassengerStatus.CHECKED_IN)) {
            throw new AppException("Vòng NFC đang được sử dụng bởi hành khách khác", HttpStatus.CONFLICT);
        }
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof JwtAuthenticationToken jwt)) {
            throw new AppException("Phiên đăng nhập không hợp lệ", HttpStatus.UNAUTHORIZED);
        }
        try {
            List<?> rooms = tourClient.get().uri("/api/finance/packages/{id}/available-rooms", assignment.getBooking().getTourPackageId())
                    .headers(h -> h.setBearerAuth(jwt.getToken().getTokenValue())).retrieve().body(List.class);
            boolean validRoom = rooms != null && rooms.stream().anyMatch(item -> item instanceof Map<?, ?> m
                    && request.getRoomId().toString().equals(String.valueOf(m.get("id"))));
            if (!validRoom) throw new AppException("Phòng không phù hợp với gói hoặc tàu của booking", HttpStatus.CONFLICT);
            var occupants = repository.findAllByStatus(BookingPassengerStatus.CHECKED_IN).stream()
                    .filter(p -> Objects.equals(p.getBooking().getTourId(), assignment.getBooking().getTourId())).toList();
            var inRoom = occupants.stream().filter(p -> Objects.equals(p.getRoomId(), request.getRoomId())).toList();
            if (inRoom.stream().anyMatch(p -> !Objects.equals(p.getBooking().getId(), bookingId))) {
                throw new AppException("Phòng đang được sử dụng bởi booking khác", HttpStatus.CONFLICT);
            }
            long assignedRooms = occupants.stream().filter(p -> Objects.equals(p.getBooking().getId(), bookingId))
                    .map(BookingPassenger::getRoomId).distinct().count();
            if (inRoom.isEmpty() && assignedRooms >= assignment.getBooking().getNumberOfRooms()) {
                throw new AppException("Booking đã được gán đủ số phòng", HttpStatus.CONFLICT);
            }
            Map<?, ?> chosenRoom = (Map<?, ?>) rooms.stream().filter(item -> item instanceof Map<?, ?> m
                    && request.getRoomId().toString().equals(String.valueOf(m.get("id")))).findFirst().orElseThrow();
            List<?> types = tourClient.get().uri("/api/finance/room-types")
                    .headers(h -> h.setBearerAuth(jwt.getToken().getTokenValue())).retrieve().body(List.class);
            Map<?, ?> type = types == null ? null : (Map<?, ?>) types.stream().filter(item -> item instanceof Map<?, ?> m
                    && Objects.equals(m.get("id"), chosenRoom.get("roomTypeId"))).findFirst().orElse(null);
            if (type == null || !(type.get("capacity") instanceof Number capacity) || capacity.intValue() <= inRoom.size()) {
                throw new AppException("Phòng đã đầy hoặc chưa có sức chứa hợp lệ", HttpStatus.CONFLICT);
            }

            List<?> cards = tourClient.get().uri("/api/finance/available-wristbands")
                    .headers(h -> h.setBearerAuth(jwt.getToken().getTokenValue())).retrieve().body(List.class);
            boolean validCard = cards != null && cards.stream().anyMatch(item -> item instanceof Map<?, ?> m && uid.equals(m.get("cardUid")));
            if (!validCard) throw new AppException("Vòng NFC không khả dụng", HttpStatus.CONFLICT);
        } catch (RestClientException ex) {
            throw new AppException("Chưa kiểm tra được phòng và vòng NFC. Vui lòng thử lại sau", HttpStatus.SERVICE_UNAVAILABLE);
        }
        assignment.setRoomId(request.getRoomId());
        assignment.setNfcCardUid(uid);
        assignment.setStatus(BookingPassengerStatus.CHECKED_IN);
        assignment.setCheckedInAt(LocalDateTime.now());
        repository.save(assignment);
    }
}
