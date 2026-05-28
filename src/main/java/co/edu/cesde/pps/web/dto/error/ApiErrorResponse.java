package co.edu.cesde.pps.web.dto.error;

import java.time.LocalDateTime;
import java.util.List;

public record ApiErrorResponse(
        String code,
        String message,
        String path,
        LocalDateTime timestamp,
        List<ApiFieldErrorResponse> fieldErrors
) {
}
