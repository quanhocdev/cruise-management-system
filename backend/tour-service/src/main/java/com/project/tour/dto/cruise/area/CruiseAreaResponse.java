package com.project.tour.dto.cruise.area;

import java.util.UUID;

import com.project.tour.model.enums.cruise.CruiseAreaStatus;

public class CruiseAreaResponse {

    private UUID id;

    private UUID cruiseDeckId;

    private Integer deckNumber;

    private String name;

    private String description;

    private CruiseAreaStatus status;

    private String imageUrl;

    private String imagePublicId;

    public CruiseAreaResponse() {
    }

    public CruiseAreaResponse(
            UUID id,
            UUID cruiseDeckId,
            Integer deckNumber,
            String name,
            String description,
            CruiseAreaStatus status,
            String imageUrl,
            String imagePublicId) {

        this.id = id;
        this.cruiseDeckId = cruiseDeckId;
        this.deckNumber = deckNumber;
        this.name = name;
        this.description = description;
        this.status = status;
        this.imageUrl = imageUrl;
        this.imagePublicId = imagePublicId;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getCruiseDeckId() {
        return cruiseDeckId;
    }

    public void setCruiseDeckId(UUID cruiseDeckId) {
        this.cruiseDeckId = cruiseDeckId;
    }

    public Integer getDeckNumber() {
        return deckNumber;
    }

    public void setDeckNumber(Integer deckNumber) {
        this.deckNumber = deckNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public CruiseAreaStatus getStatus() {
        return status;
    }

    public void setStatus(CruiseAreaStatus status) {
        this.status = status;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImagePublicId() {
        return imagePublicId;
    }

    public void setImagePublicId(String imagePublicId) {
        this.imagePublicId = imagePublicId;
    }
}