
package com.project.tour.dto.chat;

import java.util.List;

public record ChatWelcomeResponse(
        String message,
        String sessionId,
        String intentName,
        List<String> suggestions) {
}
