package co.edu.cesde.pps.web.error;

import co.edu.cesde.pps.exception.ValidationException;
import co.edu.cesde.pps.web.dto.error.ApiErrorCode;
import co.edu.cesde.pps.web.dto.error.ApiErrorResponse;
import co.edu.cesde.pps.web.dto.error.ApiFieldErrorResponse;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ErrorResponseFactory {

    private final DomainExceptionMapper domainExceptionMapper;

    public ErrorResponseFactory(DomainExceptionMapper domainExceptionMapper) {
        this.domainExceptionMapper = domainExceptionMapper;
    }

    public ApiErrorResponse fromException(Throwable throwable, String path) {
        ApiErrorCode code = domainExceptionMapper.map(throwable);
        return new ApiErrorResponse(
                code.name(),
                throwable.getMessage(),
                path,
                LocalDateTime.now(),
                List.of());
    }

    public ApiErrorResponse fromFieldErrors(ValidationException exception,
                                            String path,
                                            List<ApiFieldErrorResponse> fieldErrors) {
        return new ApiErrorResponse(
                ApiErrorCode.VALIDATION_ERROR.name(),
                exception.getMessage(),
                path,
                LocalDateTime.now(),
                fieldErrors);
    }
}
