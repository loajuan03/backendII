package co.edu.cesde.pps.web.advice;

import co.edu.cesde.pps.exception.AuthenticationException;
import co.edu.cesde.pps.exception.AuthorizationException;
import co.edu.cesde.pps.exception.ValidationException;
import co.edu.cesde.pps.web.dto.error.ApiErrorCode;
import co.edu.cesde.pps.web.dto.error.ApiErrorResponse;
import co.edu.cesde.pps.web.dto.error.ApiFieldErrorResponse;
import co.edu.cesde.pps.web.error.DomainExceptionMapper;
import co.edu.cesde.pps.web.error.ErrorResponseFactory;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class ApiExceptionHandler {

    private final DomainExceptionMapper domainExceptionMapper;
    private final ErrorResponseFactory errorResponseFactory;

    public ApiExceptionHandler(DomainExceptionMapper domainExceptionMapper,
                               ErrorResponseFactory errorResponseFactory) {
        this.domainExceptionMapper = domainExceptionMapper;
        this.errorResponseFactory = errorResponseFactory;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest req) {
        List<ApiFieldErrorResponse> fieldErrors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(fe -> new ApiFieldErrorResponse(fe.getField(), fe.getDefaultMessage()))
                .toList();

        return ResponseEntity.badRequest().body(
                errorResponseFactory.fromFieldErrors(
                        new ValidationException("Validation failed"),
                        req.getRequestURI(),
                        fieldErrors));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiErrorResponse> handleAuthentication(
            AuthenticationException ex,
            HttpServletRequest req) {
        return buildErrorResponse(ex, req);
    }

    @ExceptionHandler(AuthorizationException.class)
    public ResponseEntity<ApiErrorResponse> handleAuthorization(
            AuthorizationException ex,
            HttpServletRequest req) {
        return buildErrorResponse(ex, req);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleAny(Exception ex, HttpServletRequest req) {
        return buildErrorResponse(ex, req);
    }

    private ResponseEntity<ApiErrorResponse> buildErrorResponse(Exception ex, HttpServletRequest req) {
        ApiErrorCode code = domainExceptionMapper.map(ex);
        HttpStatus status = resolveHttpStatus(code);

        return ResponseEntity.status(status)
                .body(errorResponseFactory.fromException(ex, req.getRequestURI()));
    }

    private HttpStatus resolveHttpStatus(ApiErrorCode code) {
        return switch (code) {
            case VALIDATION_ERROR -> HttpStatus.BAD_REQUEST;
            case RESOURCE_NOT_FOUND -> HttpStatus.NOT_FOUND;
            case DUPLICATE_RESOURCE, INSUFFICIENT_STOCK, INVALID_CART_STATE, CART_MERGE_ERROR ->
                    HttpStatus.CONFLICT;
            case UNAUTHORIZED -> HttpStatus.UNAUTHORIZED;
            case FORBIDDEN -> HttpStatus.FORBIDDEN;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }
}
