package com.project.tour.service.cruise;

import com.project.tour.dto.cruise.deck.CreateCruiseDeckRequest;
import com.project.tour.dto.cruise.deck.CruiseDeckResponse;
import com.project.tour.dto.cruise.deck.UpdateCruiseDeckRequest;
import com.project.tour.exception.AppException;
import com.project.tour.mapper.cruise.CruiseDeckMapper;
import com.project.tour.model.Cruise;
import com.project.tour.model.CruiseDeck;
import com.project.tour.model.enums.cruise.CruiseDeckStatus;
import com.project.tour.repository.cruise.CruiseDeckRepository;
import com.project.tour.repository.cruise.CruiseRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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

        /*
         * =====================================================
         * CREATE DECKS
         * =====================================================
         *
         * Admin nhập:
         *
         * {
         * "totalDecks": 5
         * }
         *
         * Backend sẽ đảm bảo cruise có tổng cộng 5 tầng.
         *
         * Nếu chưa có tầng:
         * 1 -> 5
         *
         * Nếu đã có 3 tầng:
         * 4 -> 5
         *
         * Nếu đã có 5 tầng:
         * không tạo thêm.
         */
        public List<CruiseDeckResponse> createDecks(
                        UUID cruiseId,
                        CreateCruiseDeckRequest request) {

                /*
                 * =================================================
                 * 1. KIỂM TRA CRUISE
                 * =================================================
                 */

                Cruise cruise = cruiseRepository.findById(cruiseId)
                                .orElseThrow(() -> new AppException(
                                                "Cruise not found",
                                                HttpStatus.NOT_FOUND));

                int totalDecks = request.getTotalDecks();

                /*
                 * =================================================
                 * 2. TÌM SỐ TẦNG HIỆN TẠI
                 * =================================================
                 */

                Integer maxDeckNumber = cruiseDeckRepository.findMaxDeckNumberByCruiseId(
                                cruiseId);

                int currentMaxDeck = maxDeckNumber == null
                                ? 0
                                : maxDeckNumber;

                /*
                 * =================================================
                 * 3. KHÔNG CHO GIẢM SỐ TẦNG
                 * =================================================
                 */

                if (totalDecks < currentMaxDeck) {

                        throw new AppException(
                                        "Cruise currently has "
                                                        + currentMaxDeck
                                                        + " decks. "
                                                        + "You cannot reduce the total number of decks.",
                                        HttpStatus.BAD_REQUEST);
                }

                /*
                 * =================================================
                 * 4. NẾU ĐÃ ĐỦ TẦNG
                 * =================================================
                 */

                if (totalDecks == currentMaxDeck) {

                        return cruiseDeckRepository
                                        .findAllByCruise_IdOrderByDeckNumberAsc(cruiseId)
                                        .stream()
                                        .map(CruiseDeckMapper::toResponse)
                                        .toList();
                }

                /*
                 * =================================================
                 * 5. TẠO CÁC TẦNG CÒN THIẾU
                 * =================================================
                 */

                List<CruiseDeck> decksToCreate = new ArrayList<>();

                for (int deckNumber = currentMaxDeck + 1; deckNumber <= totalDecks; deckNumber++) {

                        CruiseDeck deck = CruiseDeckMapper.toEntity(
                                        cruise,
                                        deckNumber);

                        deck.setStatus(CruiseDeckStatus.ACTIVE);

                        decksToCreate.add(deck);
                }

                /*
                 * =================================================
                 * 6. LƯU TẤT CẢ
                 * =================================================
                 */

                List<CruiseDeck> savedDecks = cruiseDeckRepository.saveAll(
                                decksToCreate);

                /*
                 * =================================================
                 * 7. TRẢ VỀ RESPONSE
                 * =================================================
                 */

                return savedDecks
                                .stream()
                                .map(CruiseDeckMapper::toResponse)
                                .toList();
        }

        /*
         * =====================================================
         * GET BY ID
         * =====================================================
         */

        @Transactional(readOnly = true)
        public CruiseDeckResponse getDeckById(UUID id) {

                CruiseDeck deck = findById(id);

                return CruiseDeckMapper.toResponse(deck);
        }

        /*
         * =====================================================
         * GET ALL DECKS
         * =====================================================
         */

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

        /*
         * =====================================================
         * GET ACTIVE DECKS
         * =====================================================
         */

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
                                                CruiseDeckStatus.ACTIVE)
                                .stream()
                                .map(CruiseDeckMapper::toResponse)
                                .toList();
        }

        /*
         * =====================================================
         * UPDATE DECK
         * =====================================================
         */

        public CruiseDeckResponse updateDeck(
                        UUID id,
                        UpdateCruiseDeckRequest request) {

                CruiseDeck deck = findById(id);

                UUID cruiseId = deck.getCruise().getId();

                /*
                 * Không cho đổi sang số tầng
                 * đã tồn tại trong cùng cruise.
                 */

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

                return CruiseDeckMapper.toResponse(
                                updatedDeck);
        }

        /*
         * =====================================================
         * DELETE DECK
         * =====================================================
         */

        public void deleteDeck(UUID id) {

                CruiseDeck deck = findById(id);

                cruiseDeckRepository.delete(deck);
        }

        /*
         * =====================================================
         * FIND BY ID
         * =====================================================
         */

        private CruiseDeck findById(UUID id) {

                return cruiseDeckRepository.findById(id)
                                .orElseThrow(() -> new AppException(
                                                "Cruise deck not found",
                                                HttpStatus.NOT_FOUND));
        }
}