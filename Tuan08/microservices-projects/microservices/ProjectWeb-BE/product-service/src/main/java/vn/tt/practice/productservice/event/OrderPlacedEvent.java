package vn.tt.practice.productservice.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Kafka message payload nhận từ order-service qua topic "order-placed-topic".
 * Phải khớp cấu trúc JSON với OrderPlacedEvent bên order-service.
 *
 * Dùng @NoArgsConstructor để Jackson deserialize được từ JSON.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderPlacedEvent {
    private String orderId;
    private String productId;
    private Integer quantity;
}
