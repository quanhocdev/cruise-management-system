package com.project.tour.dto.tour.operation;

import java.util.List;
import java.util.UUID;

public record OperationCruiseLayoutResponse(

        UUID deckId,

        Integer deckNumber,

        List<OperationCruiseAreaResponse> areas,
        List<OperationRoomResponse> rooms

) {
}