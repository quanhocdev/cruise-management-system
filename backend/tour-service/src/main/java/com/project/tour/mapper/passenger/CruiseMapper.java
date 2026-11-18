package com.project.tour.mapper.passenger;

import com.project.tour.dto.passenger.cruise.PassengerCruiseDTO;
import com.project.tour.dto.passenger.cruise.PassengerCruiseDeckDTO;
import com.project.tour.dto.passenger.cruise.PassengerCruiseAreaDTO;
import com.project.tour.model.admin.Cruise;
import com.project.tour.model.admin.CruiseDeck;
import com.project.tour.model.admin.CruiseArea;

import java.util.List;

public class CruiseMapper {

    public PassengerCruiseDTO toDTO(Cruise cruise) {
        if (cruise == null) {
            return null;
        }

        return new PassengerCruiseDTO(
                cruise.getId(),
                cruise.getName(),
                cruise.getCode(),
                cruise.getDescription(),
                cruise.getMaxPassengers(),
                cruise.getImageUrl());
    }

    public PassengerCruiseDeckDTO toDeckDTO(CruiseDeck deck) {
        if (deck == null) {
            return null;
        }

        return new PassengerCruiseDeckDTO(
                deck.getId(),
                deck.getDeckNumber());
    }

    public PassengerCruiseAreaDTO toAreaDTO(CruiseArea area) {
        if (area == null) {
            return null;
        }

        return new PassengerCruiseAreaDTO(
                area.getId(),
                area.getName(),
                area.getDescription(),
                area.getImageUrl());
    }

    public List<PassengerCruiseDeckDTO> toDeckDTOs(List<CruiseDeck> decks) {
        if (decks == null) {
            return List.of();
        }

        return decks.stream()
                .map(this::toDeckDTO)
                .toList();
    }

    public List<PassengerCruiseAreaDTO> toAreaDTOs(List<CruiseArea> areas) {
        if (areas == null) {
            return List.of();
        }

        return areas.stream()
                .map(this::toAreaDTO)
                .toList();
    }
}