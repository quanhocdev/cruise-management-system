
package com.project.tour.controller.finance;

import com.project.tour.dto.tour.TourResponse;
import com.project.tour.dto.tour.schedule.ScheduleResponse;
import com.project.tour.dto.tour.schedule.stop.ScheduleStopResponse;
import com.project.tour.dto.cruise.CruiseResponse;
import com.project.tour.dto.cruise.deck.CruiseDeckResponse;
import com.project.tour.dto.cruise.area.CruiseAreaResponse;
import com.project.tour.dto.room.RoomResponse;
import com.project.tour.dto.roomtype.RoomTypeResponse;
import com.project.tour.dto.NfcCardResponse;
import com.project.tour.service.tour.TourService;
import com.project.tour.service.tour.schedule.ScheduleService;
import com.project.tour.service.tour.schedule.stop.ScheduleStopService;
import com.project.tour.service.cruise.CruiseService;
import com.project.tour.service.cruise.CruiseDeckService;
import com.project.tour.service.cruise.CruiseAreaService;
import com.project.tour.service.room.RoomService;
import com.project.tour.service.room.RoomTypeService;
import com.project.tour.service.NfcCardService;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/finance")
public class FinanceController {

    private final TourService tourService;
    private final ScheduleService scheduleService;
    private final ScheduleStopService scheduleStopService;
    private final CruiseService cruiseService;
    private final CruiseDeckService cruiseDeckService;
    private final CruiseAreaService cruiseAreaService;
    private final RoomService roomService;
    private final RoomTypeService roomTypeService;
    private final NfcCardService nfcCardService;

    public FinanceController(
            TourService tourService,
            ScheduleService scheduleService,
            ScheduleStopService scheduleStopService,
            CruiseService cruiseService,
            CruiseDeckService cruiseDeckService,
            CruiseAreaService cruiseAreaService,
            RoomService roomService,
            RoomTypeService roomTypeService,
            NfcCardService nfcCardService) {
        this.tourService = tourService;
        this.scheduleService = scheduleService;
        this.scheduleStopService = scheduleStopService;
        this.cruiseService = cruiseService;
        this.cruiseDeckService = cruiseDeckService;
        this.cruiseAreaService = cruiseAreaService;
        this.roomService = roomService;
        this.roomTypeService = roomTypeService;
        this.nfcCardService = nfcCardService;
    }

    // 1. Get danh sách các tour
    @GetMapping("/tours")
    public ResponseEntity<List<TourResponse>> getTours(
            @RequestParam(required = false) UUID cruiseId) {
        return ResponseEntity.ok(tourService.getTours(cruiseId, null));
    }

    // 2. Get lịch trình của tour (schedules)
    @GetMapping("/tours/{tourId}/schedules")
    public ResponseEntity<List<ScheduleResponse>> getSchedulesByTour(
            @PathVariable UUID tourId) {
        return ResponseEntity.ok(scheduleService.getAll(tourId));
    }

    // Get lịch dừng của tour (dựa theo scheduleId)
    @GetMapping("/schedules/{scheduleId}/stops")
    public ResponseEntity<List<ScheduleStopResponse>> getScheduleStops(
            @PathVariable UUID scheduleId) {
        return ResponseEntity.ok(scheduleStopService.getAll(scheduleId));
    }

    // 3. Get thuyền (cruise) của tour được chọn (hoặc lấy chi tiết cruise qua ID
    // tàu được gán vào tour)
    @GetMapping("/tours/{tourId}/cruise")
    public ResponseEntity<CruiseResponse> getCruiseByTour(
            @PathVariable UUID tourId) {
        TourResponse tour = tourService.getTourById(tourId);
        // Giả định TourResponse có chứa cruiseId hoặc gọi service tương ứng
        // Nếu TourResponse có cruiseId, ta lấy thông tin cruise:
        CruiseResponse cruise = cruiseService.getCruiseById(tour.cruiseId()); // tuỳ chỉnh theo field DTO thực tế của
                                                                              // bạn
        return ResponseEntity.ok(cruise);
    }

    // 4. Get tầng (decks) của thuyền thuộc tour (hoặc theo cruiseId)
    @GetMapping("/cruises/{cruiseId}/decks")
    public ResponseEntity<List<CruiseDeckResponse>> getDecksByCruise(
            @PathVariable UUID cruiseId) {
        return ResponseEntity.ok(cruiseDeckService.getDecksByCruise(cruiseId));
    }

    // 5. Get khu vực (areas) của tàu thuộc tầng (deck) của thuyền
    @GetMapping("/decks/{deckId}/areas")
    public ResponseEntity<List<CruiseAreaResponse>> getAreasByDeck(
            @PathVariable UUID deckId) {
        return ResponseEntity.ok(cruiseAreaService.getAll(deckId));
    }

    // 6. Get phòng (rooms) của tầng (deck) của thuyền
    @GetMapping("/decks/{deckId}/rooms")
    public ResponseEntity<List<RoomResponse>> getRoomsByDeck(
            @PathVariable UUID deckId) {
        return ResponseEntity.ok(roomService.getRoomsByDeck(deckId));
    }

    // 7. Get kiểu phòng (room-types) của thuyền
    @GetMapping("/room-types")
    public ResponseEntity<List<RoomTypeResponse>> getAllRoomTypes() {
        return ResponseEntity.ok(roomTypeService.getAllRoomTypes());
    }

    // 8. Get danh sách các vòng nfc
    @GetMapping("/nfc-cards")
    public ResponseEntity<List<NfcCardResponse>> getAllNfcCards() {
        return ResponseEntity.ok(nfcCardService.getAllCards());
    }

    @GetMapping("/packages/{tourPackageId}/available-rooms")
    public ResponseEntity<?> getAvailableRoomsByPackage(
            @PathVariable UUID tourPackageId,
            @RequestParam(required = false) Long bookingId) {

        System.out.println("========================================");
        System.out.println("[TOUR-FINANCE] GET available-rooms for tourPackageId = " + tourPackageId);
        System.out.println("[TOUR-FINANCE] bookingId = " + bookingId);
        System.out.println("========================================");

        try {
            List<RoomResponse> availableRooms = roomService.getAvailableRoomsForBooking(bookingId, tourPackageId);
            return ResponseEntity.ok(availableRooms);
        } catch (Exception e) {
            // IN TRỌN VÉN LỖI RA CONSOLE ĐỂ XEM NÓ BỊ Ở ĐÂU
            System.err.println("================ LỖI 500 CHI TIẾT ================");
            e.printStackTrace();
            System.err.println("==================================================");

            // Trả về message lỗi chi tiết luôn cho Frontend dễ debug
            return ResponseEntity.status(500).body(Map.of(
                    "error", true,
                    "message", e.getMessage()));
        }
    }

    // 10. Get danh sách vòng NFC còn trống (Active & Unused) trong Tour cụ thể
    @GetMapping("/tours/{tourId}/available-wristbands")
    public ResponseEntity<List<NfcCardResponse>> getAvailableWristbands(
            @PathVariable UUID tourId) {

        System.out.println("========================================");
        System.out.println("[TOUR-FINANCE] GET available-wristbands for tourId = " + tourId);
        System.out.println("========================================");

        // Gọi nfcCardService lấy các vòng ACTIVE và UNUSED thuộc tour này
        List<NfcCardResponse> availableCards = nfcCardService.getAvailableCardsByTour(tourId);
        return ResponseEntity.ok(availableCards);
    }
}