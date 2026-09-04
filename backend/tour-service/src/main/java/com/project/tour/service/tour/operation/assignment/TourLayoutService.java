package com.project.tour.service.tour.operation.assignment;

import com.project.tour.dto.tour.operation.OperationCruiseLayoutResponse;
import com.project.tour.exception.AppException;
import com.project.tour.mapper.tour.operation.OperationCruiseMapper;
import com.project.tour.model.CruiseArea;
import com.project.tour.model.CruiseDeck;
import com.project.tour.model.Room;
import com.project.tour.model.Tour;
import com.project.tour.model.enums.RoomStatus;
import com.project.tour.model.enums.cruise.CruiseAreaStatus;
import com.project.tour.model.enums.cruise.CruiseDeckStatus;
import com.project.tour.repository.cruise.CruiseAreaRepository;
import com.project.tour.repository.cruise.CruiseDeckRepository;
import com.project.tour.repository.room.RoomRepository;
import com.project.tour.repository.tour.TourRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class TourLayoutService {

        private final TourRepository tourRepository;
        private final CruiseDeckRepository cruiseDeckRepository;
        private final CruiseAreaRepository cruiseAreaRepository;
        private final RoomRepository roomRepository;

        public TourLayoutService(
                        TourRepository tourRepository,
                        CruiseDeckRepository cruiseDeckRepository,
                        CruiseAreaRepository cruiseAreaRepository,
                        RoomRepository roomRepository) {
                this.tourRepository = tourRepository;
                this.cruiseDeckRepository = cruiseDeckRepository;
                this.cruiseAreaRepository = cruiseAreaRepository;
                this.roomRepository = roomRepository;
        }

        /**
         * LẤY LAYOUT DU THUYỀN DÀNH CHO OPERATOR
         */
        public List<OperationCruiseLayoutResponse> getCruiseLayout(UUID tourId) {

                Tour tour = tourRepository.findById(tourId)
                                .orElseThrow(() -> new AppException("Tour not found", HttpStatus.NOT_FOUND));

                if (tour.getCruise() == null) {
                        throw new AppException("Tour has no cruise assigned", HttpStatus.BAD_REQUEST);
                }

                UUID cruiseId = tour.getCruise().getId();

                List<CruiseDeck> decks = cruiseDeckRepository
                                .findAllByCruise_IdAndStatusOrderByDeckNumberAsc(cruiseId, CruiseDeckStatus.ACTIVE);

                return decks.stream()
                                .map(deck -> {
                                        List<CruiseArea> areas = cruiseAreaRepository
                                                        .findAllByCruiseDeck_IdAndStatusOrderByNameAsc(deck.getId(),
                                                                        CruiseAreaStatus.ACTIVE);

                                        List<Room> rooms = roomRepository
                                                        .findAllByCruiseDeck_IdAndStatusOrderByCodeAsc(deck.getId(),
                                                                        RoomStatus.ACTIVE);

                                        return OperationCruiseMapper.toLayoutResponse(deck, areas, rooms);
                                })
                                .toList();
        }
}
