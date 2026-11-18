
package com.project.tour.service.passenger;

import com.project.tour.dto.passenger.cruise.PassengerCruiseDTO;
import com.project.tour.dto.passenger.cruise.PassengerCruiseDeckDTO;
import com.project.tour.dto.passenger.cruise.PassengerCruiseAreaDTO;
import com.project.tour.mapper.passenger.CruiseMapper;
import com.project.tour.model.admin.Cruise;
import com.project.tour.model.admin.CruiseDeck;
import com.project.tour.model.enums.cruise.CruiseDeckStatus;
import com.project.tour.model.enums.cruise.CruiseAreaStatus;
import com.project.tour.repository.scheduler.TourRepository;
import com.project.tour.repository.cruise.CruiseDeckRepository;
import com.project.tour.repository.cruise.CruiseAreaRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class PassengerCruiseService {

    private final TourRepository tourRepository;
    private final CruiseDeckRepository cruiseDeckRepository;
    private final CruiseAreaRepository cruiseAreaRepository;
    private final CruiseMapper cruiseMapper;

    public PassengerCruiseService(
            TourRepository tourRepository,
            CruiseDeckRepository cruiseDeckRepository,
            CruiseAreaRepository cruiseAreaRepository,
            CruiseMapper cruiseMapper) {
        this.tourRepository = tourRepository;
        this.cruiseDeckRepository = cruiseDeckRepository;
        this.cruiseAreaRepository = cruiseAreaRepository;
        this.cruiseMapper = cruiseMapper;
    }

    /**
     * Lấy thông tin tàu được phân công cho Tour.
     */
    public PassengerCruiseDTO getCruise(UUID tourId) {

        var tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy tour"));

        Cruise cruise = tour.getCruise();

        if (cruise == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Tour chưa được phân công tàu");
        }

        return cruiseMapper.toDTO(cruise);
    }

    /**
     * Lấy danh sách tầng ACTIVE của tàu thuộc Tour.
     */
    public List<PassengerCruiseDeckDTO> getDecks(UUID tourId) {

        var tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy tour"));

        if (tour.getCruise() == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Tour chưa được phân công tàu");
        }

        UUID cruiseId = tour.getCruise().getId();

        List<com.project.tour.model.admin.CruiseDeck> decks = cruiseDeckRepository
                .findAllByCruise_IdAndStatusOrderByDeckNumberAsc(
                        cruiseId,
                        CruiseDeckStatus.ACTIVE);

        return cruiseMapper.toDeckDTOs(decks);
    }

    /**
     * Lấy danh sách khu vực ACTIVE của một tầng.
     * Kiểm tra tầng phải thuộc tàu được phân công cho Tour.
     */
    public List<PassengerCruiseAreaDTO> getAreas(
            UUID tourId,
            UUID deckId) {

        var tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy tour"));

        if (tour.getCruise() == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Tour chưa được phân công tàu");
        }

        UUID cruiseId = tour.getCruise().getId();

        CruiseDeck deck = cruiseDeckRepository
                .findByIdAndCruise_Id(deckId, cruiseId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy tầng thuộc tàu của tour"));

        if (deck.getStatus() != CruiseDeckStatus.ACTIVE) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Tầng hiện không hoạt động");
        }

        var areas = cruiseAreaRepository
                .findAllByCruiseDeck_IdAndStatusOrderByNameAsc(
                        deckId,
                        CruiseAreaStatus.ACTIVE);

        return cruiseMapper.toAreaDTOs(areas);
    }
}
