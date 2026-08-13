package com.project.tour.service.cruise;

import com.project.tour.dto.cruise.deck.CreateCruiseDeckRequest;
import com.project.tour.dto.cruise.deck.CruiseDeckResponse;
import com.project.tour.dto.cruise.deck.UpdateCruiseDeckRequest;
import com.project.tour.exception.AppException;
import com.project.tour.mapper.cruise.CruiseDeckMapper;
import com.project.tour.model.Cruise;
import com.project.tour.model.CruiseDeck;
import com.project.tour.repository.cruise.CruiseDeckRepository;
import com.project.tour.repository.cruise.CruiseRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class CruiseDeckService {

        private final CruiseDeckRepository cruiseDeckRepository;
        private final CruiseRepository cruiseRepository;

        public CruiseDeckService(
                        CruiseDeckRepository cruiseDeckRepository,
                        CruiseRepository cruiseRepository) {

                this.cruiseDeckRepository = cruiseDeckRepository;
                this.cruiseRepository = cruiseRepository;
        }

        public CruiseDeckResponse createDeck(
                        CreateCruiseDeckRequest request) {

                Cruise cruise = cruiseRepository.findById(
                                request.getCruiseId()).orElseThrow(
                                                () -> new AppException(
                                                                "Cruise not found",
                                                                HttpStatus.NOT_FOUND));

                if (cruiseDeckRepository.existsByCruise_IdAndDeckNumber(
                                request.getCruiseId(),
                                request.getDeckNumber())) {

                        throw new AppException(
                                        "Deck number already exists in this cruise",
                                        HttpStatus.CONFLICT);
                }

                CruiseDeck deck = CruiseDeckMapper.toEntity(
                                request,
                                cruise);

                CruiseDeck savedDeck = cruiseDeckRepository.save(deck);

                return CruiseDeckMapper.toResponse(savedDeck);
        }

        @Transactional(readOnly = true)
        public CruiseDeckResponse getDeckById(UUID id) {

                CruiseDeck deck = findById(id);

                return CruiseDeckMapper.toResponse(deck);
        }

        @Transactional(readOnly = true)
        public List<CruiseDeckResponse> getDecksByCruise(
                        UUID cruiseId) {

                if (!cruiseRepository.existsById(cruiseId)) {

                        throw new AppException(
                                        "Cruise not found",
                                        HttpStatus.NOT_FOUND);
                }

                return cruiseDeckRepository
                                .findAllByCruise_IdOrderByDeckNumberAsc(cruiseId)
                                .stream()
                                .map(CruiseDeckMapper::toResponse)
                                .toList();
        }

        @Transactional(readOnly = true)
        public List<CruiseDeckResponse> getActiveDecksByCruise(
                        UUID cruiseId) {

                if (!cruiseRepository.existsById(cruiseId)) {

                        throw new AppException(
                                        "Cruise not found",
                                        HttpStatus.NOT_FOUND);
                }

                return cruiseDeckRepository
                                .findAllByCruise_IdAndStatusOrderByDeckNumberAsc(
                                                cruiseId,
                                                com.project.tour.model.enums.cruise.CruiseDeckStatus.ACTIVE)
                                .stream()
                                .map(CruiseDeckMapper::toResponse)
                                .toList();
        }

        public CruiseDeckResponse updateDeck(
                        UUID id,
                        UpdateCruiseDeckRequest request) {

                CruiseDeck deck = findById(id);

                UUID cruiseId = deck.getCruise().getId();

                if (cruiseDeckRepository
                                .existsByCruise_IdAndDeckNumberAndIdNot(
                                                cruiseId,
                                                request.getDeckNumber(),
                                                id)) {

                        throw new AppException(
                                        "Deck number already exists in this cruise",
                                        HttpStatus.CONFLICT);
                }

                CruiseDeckMapper.updateEntity(
                                deck,
                                request);

                CruiseDeck updatedDeck = cruiseDeckRepository.save(deck);

                return CruiseDeckMapper.toResponse(updatedDeck);
        }

        public void deleteDeck(UUID id) {

                CruiseDeck deck = findById(id);

                cruiseDeckRepository.delete(deck);
        }

        private CruiseDeck findById(UUID id) {

                return cruiseDeckRepository.findById(id)
                                .orElseThrow(() -> new AppException(
                                                "Cruise deck not found",
                                                HttpStatus.NOT_FOUND));
        }
}