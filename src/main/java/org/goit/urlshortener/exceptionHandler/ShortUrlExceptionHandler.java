package org.goit.urlshortener.exceptionHandler;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestControllerAdvice
public class ShortUrlExceptionHandler {

    @ExceptionHandler(ShortUrlException.class)
    public ResponseEntity<Map<String, ErrorResponse>> handleShortUrlException(ShortUrlException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Map<String, ErrorResponse>> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, ErrorResponse>> handleBadCredentialsException(BadCredentialsException ex) {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, ErrorResponse>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String errorMessage = String.format(
                "Invalid type for parameter '%s'. Expected type: %s.",
                ex.getName(),
                (ex.getRequiredType() != null) ? ex.getRequiredType().getSimpleName() : "unknown"
        );
        return buildErrorResponse(HttpStatus.BAD_REQUEST, errorMessage);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, ErrorResponse>> handleConstraintViolation(ConstraintViolationException ex) {
        String errorMessage = ex.getConstraintViolations().stream()
                .map(violation -> String.format("Field '%s': %s", violation.getPropertyPath(), violation.getMessage()))
                .collect(Collectors.joining("; "));
        return buildErrorResponse(HttpStatus.BAD_REQUEST, errorMessage);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleArgumentNotValid(MethodArgumentNotValidException ex) {
        List<Map<String, Object>> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> Map.of(
                        "field", error.getField(),
                        "message", formatMessage(Objects.requireNonNull(error.getDefaultMessage()))
                ))
                .toList();

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("httpStatus", HttpStatus.BAD_REQUEST.value());
        errorResponse.put("errors", errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", errorResponse));
    }

    private Object formatMessage(String defaultMessage) {
        if (defaultMessage.contains("\n")) {
            return Stream.of(defaultMessage.split("\n"))
                    .map(String::trim)
                    .filter(part -> !part.isBlank())
                    .toList();
        }
        return defaultMessage;
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, ErrorResponse>> handleResponseStatusException(ResponseStatusException ex) {
        return buildErrorResponse((HttpStatus) ex.getStatusCode(), ex.getReason());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, ErrorResponse>> handleGeneralException(Exception ex) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred on the server.");
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, ErrorResponse>> handleRuntimeException(RuntimeException ex) {
        HttpStatus status;

        if (ex.getMessage().contains("expired")) {
            status = HttpStatus.GONE;
        } else if (ex.getMessage().contains("not found") || ex.getMessage().contains("invalid")) {
            status = HttpStatus.NOT_FOUND;
        } else {
            status = HttpStatus.BAD_REQUEST;
        }

        return buildErrorResponse(status, ex.getMessage());
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<Map<String, ErrorResponse>> handleBindException(BindException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> String.format("Field '%s': %s", error.getField(),
                        Objects.requireNonNullElse(error.getDefaultMessage(), "Validation error")))
                .collect(Collectors.joining("; "));
        return buildErrorResponse(HttpStatus.BAD_REQUEST, errorMessage);
    }

    @ExceptionHandler(JwtAuthenticationException.class)
    public ResponseEntity<Map<String, ErrorResponse>> handleJwtAuthenticationException(JwtAuthenticationException ex) {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    private ResponseEntity<Map<String, ErrorResponse>> buildErrorResponse(HttpStatus status, String detail) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .httpStatus(status.value())
                .detail(detail)
                .build();
        return ResponseEntity.status(status).body(Map.of("error", errorResponse));
    }
}