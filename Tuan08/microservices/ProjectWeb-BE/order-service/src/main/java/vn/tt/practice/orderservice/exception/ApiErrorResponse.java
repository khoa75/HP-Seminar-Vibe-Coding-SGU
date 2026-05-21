package vn.tt.practice.orderservice.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Chuẩn hóa format JSON trả về khi có lỗi trong toàn bộ order-service.
 *
 * Ví dụ response:
 * {
 *   "timestamp": "2024-05-01 10:30:00",
 *   "status": 503,
 *   "error": "Service Unavailable",
 *   "message": "Hệ thống đang quá tải...",
 *   "path": "/v1/api/order"
 * }
 */
@Data
@Builder
public class ApiErrorResponse {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    private int status;       // HTTP status code (vd: 404, 503)
    private String error;     // HTTP status phrase (vd: "Not Found")
    private String message;   // Mô tả lỗi chi tiết
    private String path;      // Request URI gây ra lỗi
}
