package com.project.tour.dto.operation.assignment;

import java.util.List;
import java.util.UUID;

import com.project.tour.dto.operation.assignment.cruise.OperationCruiseAreaResponse;
import com.project.tour.dto.operation.assignment.room.OperationRoomResponse;

public record OperationCruiseLayoutResponse(

        UUID deckId,

        Integer deckNumber,

        List<OperationCruiseAreaResponse> areas,
        List<OperationRoomResponse> rooms

) {
}