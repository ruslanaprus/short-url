package org.goit.urlshortener.exceptionHandler;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;




import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ShortUrlExceptionHandler {

    // Обработка ShortUrlException
    @ExceptionHandler(ShortUrlException.class)
    public ResponseEntity<String> handleShortUrlException(ShortUrlException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Error: " + ex.getMessage());
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<String> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Error: " + ex.getMessage());
    }
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<String> handleBadCredentialsException(BadCredentialsException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body("Error: " + ex.getMessage());
    }

    // Обработка ошибки типа аргумента (например, неверный ID)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<String> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String errorMessage = String.format(
                "Invalid type for parameter '%s'. Expected type: %s.",
                ex.getName(),
                ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown"
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorMessage);
    }




    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<List<ValidationErrorResponse>> handleConstraintViolation(ConstraintViolationException ex) {
        List<ValidationErrorResponse> errors = ex.getConstraintViolations().stream()
                .map(violation -> new ValidationErrorResponse(
                        violation.getPropertyPath().toString(),
                        violation.getMessage()
                ))
                .collect(Collectors.toList());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errors);
    }


    // Обработка ошибок валидации параметров метода
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleArgumentNotValid(MethodArgumentNotValidException ex) {
        List<ValidationError> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> new ValidationError(error.getDefaultMessage()))
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Invalid request data");
        response.put("errors", errors);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }


    // Обработка ResponseStatusException
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<String> handleResponseStatusException(ResponseStatusException ex) {
        String errorMessage = String.format("Error: %s (HTTP status code: %d)", ex.getReason(), ex.getStatusCode().value());
        return ResponseEntity
                .status(ex.getStatusCode())
                .body(errorMessage);
    }


    // Общий обработчик исключений
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception ex) {
        String customErrorMessage = "An unexpected error occurred on the server. Please try again later or contact support.";
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(customErrorMessage);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<String> handleBindException(BindException ex) {
        StringBuilder errors = new StringBuilder("Validation errors: ");
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.append(error.getField()).append(" - ").append(error.getDefaultMessage()).append("; ");
        });
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errors.toString());
    }

}
