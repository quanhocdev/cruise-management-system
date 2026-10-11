package com.project.tour.service;

import com.project.tour.dto.admin.nfc.NfcCardRequest;
import com.project.tour.dto.admin.nfc.NfcCardResponse;
import com.project.tour.mapper.admin.nfc.NfcCardMapper;
import com.project.tour.model.admin.NfcCard;
import com.project.tour.model.enums.NfcCardStatus;
import com.project.tour.repository.NfcCardRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class NfcCardService {

    private final NfcCardRepository nfcCardRepository;
    private final NfcCardMapper nfcCardMapper;

    public NfcCardService(NfcCardRepository nfcCardRepository, NfcCardMapper nfcCardMapper) {
        this.nfcCardRepository = nfcCardRepository;
        this.nfcCardMapper = nfcCardMapper;
    }

    public NfcCardResponse createCard(NfcCardRequest request) {
        if (nfcCardRepository.existsByCardUid(request.cardUid())) {
            throw new IllegalArgumentException("NFC card UID already exists: " + request.cardUid());
        }
        NfcCard card = new NfcCard();
        card.setCardUid(request.cardUid());
        if (request.status() != null) {
            card.setStatus(request.status());
        }
        NfcCard saved = nfcCardRepository.save(card);
        return nfcCardMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<NfcCardResponse> getAllCards() {
        return nfcCardRepository.findAll().stream()
                .map(nfcCardMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public NfcCardResponse getCardById(UUID id) {
        NfcCard card = nfcCardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("NFC card not found with id: " + id));
        return nfcCardMapper.toResponse(card);
    }

    public NfcCardResponse updateCard(UUID id, NfcCardRequest request) {
        NfcCard card = nfcCardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("NFC card not found with id: " + id));

        if (request.cardUid() != null && !request.cardUid().equals(card.getCardUid())) {
            if (nfcCardRepository.existsByCardUid(request.cardUid())) {
                throw new IllegalArgumentException("NFC card UID already exists: " + request.cardUid());
            }
            card.setCardUid(request.cardUid());
        }

        if (request.status() != null) {
            card.setStatus(request.status());
        }

        NfcCard updated = nfcCardRepository.save(card);
        return nfcCardMapper.toResponse(updated);
    }

    public void deleteCard(UUID id) {
        if (!nfcCardRepository.existsById(id)) {
            throw new RuntimeException("NFC card not found with id: " + id);
        }
        nfcCardRepository.deleteById(id);
    }

    // =====================================================
    // GET AVAILABLE CARDS FOR CHECK-IN
    // =====================================================

    @Transactional(readOnly = true)
    public List<NfcCardResponse> getAllAvailableCards() {
        System.out.println("[SERVICE] Lấy danh sách vòng NFC có sẵn (AVAILABLE)");

        // Lấy tất cả các thẻ đang ở trạng thái AVAILABLE trong kho
        return nfcCardRepository.findAllByStatus(NfcCardStatus.AVAILABLE)
                .stream()
                .map(nfcCardMapper::toResponse)
                .toList();
    }

    public NfcCardResponse assignCard(String cardUid) {
        if (cardUid == null || cardUid.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "NFC card UID must not be blank");
        }

        String normalizedUid = cardUid.trim();

        NfcCard card = nfcCardRepository.findByCardUidForUpdate(normalizedUid)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "NFC card not found: " + normalizedUid));

        if (card.getStatus() != NfcCardStatus.AVAILABLE) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "NFC card is not available. Current status: "
                            + card.getStatus());
        }

        card.setStatus(NfcCardStatus.ASSIGNED);

        NfcCard saved = nfcCardRepository.save(card);

        System.out.println(
                "[NFC] Card " + normalizedUid
                        + " changed from AVAILABLE to ASSIGNED");

        return nfcCardMapper.toResponse(saved);
    }

}