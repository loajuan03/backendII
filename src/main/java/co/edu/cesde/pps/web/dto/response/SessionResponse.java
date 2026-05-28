package co.edu.cesde.pps.web.dto.response;

import java.time.LocalDateTime;

public record SessionResponse(
        Long id,
        String sessionToken,
        Long userId,
        Boolean isGuest,
        LocalDateTime createdAt,
        LocalDateTime expiresAt
) {
}
