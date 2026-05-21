package vn.tt.practice.orderservice.producer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import vn.tt.practice.orderservice.event.OrderPlacedEvent;

@Slf4j
@Service
public class OrderEventProducer {

    private static final String NOTIFICATION_TOPIC  = "notificationTopic";
    private static final String ORDER_PLACED_TOPIC  = "order-placed-topic";

    /** KafkaTemplate<String, String> — dùng cho notification-service */
    private final KafkaTemplate<String, String> stringKafkaTemplate;

    /** KafkaTemplate<String, OrderPlacedEvent> — dùng cho product-service (giảm tồn kho) */
    private final KafkaTemplate<String, OrderPlacedEvent> orderPlacedKafkaTemplate;

    public OrderEventProducer(
            @Qualifier("stringKafkaTemplate")       KafkaTemplate<String, String> stringKafkaTemplate,
            @Qualifier("orderPlacedKafkaTemplate")  KafkaTemplate<String, OrderPlacedEvent> orderPlacedKafkaTemplate
    ) {
        this.stringKafkaTemplate      = stringKafkaTemplate;
        this.orderPlacedKafkaTemplate = orderPlacedKafkaTemplate;
    }

    // -------------------------------------------------------------------------
    // Notification event (giữ nguyên cho notification-service)
    // -------------------------------------------------------------------------

    public void sendOrderEvent(String message, String userId) {
        String jsonMessage = String.format("{\"message\":\"%s\", \"userId\":\"%s\"}", message, userId);
        stringKafkaTemplate.send(NOTIFICATION_TOPIC, jsonMessage);
        log.info("[Kafka] Sent notification event → topic={}, userId={}", NOTIFICATION_TOPIC, userId);
    }

    // -------------------------------------------------------------------------
    // Order placed event (MỚI) — product-service consume để trừ tồn kho
    // -------------------------------------------------------------------------

    /**
     * Publish một {@link OrderPlacedEvent} lên topic "order-placed-topic".
     * Key = orderId để Kafka phân vùng theo đơn hàng.
     *
     * @param orderId   ID đơn hàng
     * @param productId ID sản phẩm cần trừ tồn kho
     * @param quantity  Số lượng cần trừ
     */
    public void publishOrderPlaced(String orderId, String productId, Integer quantity) {
        OrderPlacedEvent event = OrderPlacedEvent.builder()
                .orderId(orderId)
                .productId(productId)
                .quantity(quantity)
                .build();

        orderPlacedKafkaTemplate.send(ORDER_PLACED_TOPIC, orderId, event);
        log.info("[Kafka] Published OrderPlacedEvent → topic={}, orderId={}, productId={}, qty={}",
                ORDER_PLACED_TOPIC, orderId, productId, quantity);
    }
}