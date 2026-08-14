package com.project.tour.dto.cruise.deck;

import java.util.UUID;

import com.project.tour.model.enums.cruise.CruiseDeckStatus;

public class CruiseDeckResponse {

    private UUID id;

    private UUID cruiseId;

    private String cruiseName;

    private Integer deckNumber;

    private CruiseDeckStatus status;

    public CruiseDeckResponse() {
    }

    public CruiseDeckResponse(
            UUID id,
            UUID cruiseId,
            String cruiseName,
            Integer deckNumber,
            CruiseDeckStatus status) {

        this.id = id;
        this.cruiseId = cruiseId;
        this.cruiseName = cruiseName;
        this.deckNumber = deckNumber;
        this.status = status;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getCruiseId() {
        return cruiseId;
    }

    public void setCruiseId(UUID cruiseId) {
        this.cruiseId = cruiseId;
    }

    public String getCruiseName() {
        return cruiseName;
    }

    public void setCruiseName(String cruiseName) {
        this.cruiseName = cruiseName;
    }

    public Integer getDeckNumber() {
        return deckNumber;
    }

    public void setDeckNumber(Integer deckNumber) {
        this.deckNumber = deckNumber;
    }

    public CruiseDeckStatus getStatus() {
        return status;
    }

    public void setStatus(CruiseDeckStatus status) {
        this.status = status;
    }
}