package vn.tt.practice.orderservice.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * Xử lý tập trung tất cả exception — loại bỏ try/catch lặp lại ở mỗi Controller.
 * Mọi lỗi đều trả về cùng format ApiErrorResponse.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // -------------------------------------------------------------------------
    // 400 Bad Request — lỗi validation DTO (@Valid, @NotBlank, v.v.)
    // -------------------------------------------------------------------------

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        // Thu thập tất cả field errors thành một chuỗi: "field1: message1; field2: message2"
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining("; "));

        ApiErrorResponse body = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(message)
                .path(request.getRequestURI())
                .build();

        log.warn("[GlobalExceptionHandler] Validation failed — path={}, errors={}",
                request.getRequestURI(), message);

        return ResponseEntity.badRequest().body(body);
    }

    // -------------------------------------------------------------------------
    // 503 Service Unavailable — Circuit Breaker mở (product-service sập)
    // -------------------------------------------------------------------------

    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<ApiErrorResponse> handleServiceUnavailable(
            ServiceUnavailableException ex,
            HttpServletRequest request) {

        ApiErrorResponse body = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.SERVICE_UNAVAILABLE.value())
                .error(HttpStatus.SERVICE_UNAVAILABLE.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        log.warn("[GlobalExceptionHandler] Service unavailable — path={}", request.getRequestURI());

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(body);
    }

    // -------------------------------------------------------------------------
    // 500 Internal Server Error — mọi lỗi không xác định còn lại
    // Đặt cuối cùng để không che các handler cụ thể ở trên
    // -------------------------------------------------------------------------

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpectedException(
            Exception ex,
            HttpServletRequest request) {

        ApiErrorResponse body = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .message("An unexpected error occurred. Please contact support.")
                .path(request.getRequestURI())
                .build();

        // Log full stack trace để debug — không expose cho client
        log.error("[GlobalExceptionHandler] Unexpected error — path={}", request.getRequestURI(), ex);

        return ResponseEntity.internalServerError().body(body);
    }
}
