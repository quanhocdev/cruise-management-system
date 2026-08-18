package com.project.tour.mapper.tour.operation;

import com.project.tour.dto.tour.operation.OperationCruiseAreaResponse;
import com.project.tour.dto.tour.operation.OperationCruiseLayoutResponse;
import com.project.tour.dto.tour.operation.OperationRoomResponse;
import com.project.tour.model.CruiseArea;
import com.project.tour.model.CruiseDeck;
import com.project.tour.model.Room; // Giả định tên Entity Room của bạn

import java.util.Collections;
import java.util.List;

public class OperationCruiseMapper {

    public static OperationCruiseAreaResponse toAreaResponse(CruiseArea area) {
        if (area == null)
            return null;
        return new OperationCruiseAreaResponse(
                area.getId(),
                area.getName(),
                area.getDescription(),
                area.getStatus());
    }

    public static OperationRoomResponse toRoomResponse(Room room) {
        if (room == null)
            return null;
        return new OperationRoomResponse(
                room.getId(),
                room.getCode(),
                room.getStatus());
    }

    public static OperationCruiseLayoutResponse toLayoutResponse(
            CruiseDeck deck,
            List<CruiseArea> activeAreas,
            List<Room> rooms) {

        if (deck == null)
            return null;

        List<OperationCruiseAreaResponse> areaResponses = activeAreas != null
                ? activeAreas.stream().map(OperationCruiseMapper::toAreaResponse).toList()
                : Collections.emptyList();

        List<OperationRoomResponse> roomResponses = rooms != null
                ? rooms.stream().map(OperationCruiseMapper::toRoomResponse).toList()
                : Collections.emptyList();

        return new OperationCruiseLayoutResponse(
                deck.getId(),
                deck.getDeckNumber(),
                areaResponses,
                roomResponses);
    }
}