package vn.tt.practice.orderservice.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Event payload gửi tới Kafka topic "order-placed-topic".
 * product-service consume event này để trừ tồn kho.
 *
 * Một đơn hàng có nhiều sản phẩm → publish một event cho mỗi sản phẩm.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderPlacedEvent {

    /** ID của đơn hàng (MongoDB ObjectId) */
    private String orderId;

    /** ID của sản phẩm cần trừ tồn kho */
    private String productId;

    /** Số lượng cần trừ */
    private Integer quantity;
}
