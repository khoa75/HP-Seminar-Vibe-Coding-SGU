package vn.tt.practice.productservice.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import vn.tt.practice.productservice.event.OrderPlacedEvent;
import vn.tt.practice.productservice.model.Product;
import vn.tt.practice.productservice.repository.ProductRepo;

/**
 * Kafka Consumer lắng nghe topic "order-placed-topic".
 * Nhận OrderPlacedEvent và tự động trừ tồn kho sản phẩm.
 *
 * Nguyên tắc thiết kế:
 *  - KHÔNG ném Exception ra ngoài → Consumer không bị crash/restart loop
 *  - Ghi log cảnh báo nếu tồn kho không đủ (Eventual Consistency)
 *  - Idempotent-safe: nếu cùng event tới 2 lần → trừ 2 lần (cần idempotency key nếu cần strict)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderEventConsumer {

    private final ProductRepo productRepo;

    @KafkaListener(
            topics          = "order-placed-topic",
            groupId         = "${spring.kafka.consumer.group-id}",
            containerFactory = "orderPlacedKafkaListenerContainerFactory"
    )
    public void handleOrderPlaced(OrderPlacedEvent event) {
        // Guard: bỏ qua message null (có thể xảy ra khi ErrorHandlingDeserializer bắt lỗi)
        if (event == null) {
            log.warn("[Kafka Consumer] Received null event — skipping");
            return;
        }

        log.info("[Kafka Consumer] Received OrderPlacedEvent — orderId={}, productId={}, qty={}",
                event.getOrderId(), event.getProductId(), event.getQuantity());

        try {
            // ① Tìm sản phẩm theo productId
            Product product = productRepo.findById(event.getProductId())
                    .orElse(null);

            if (product == null) {
                log.warn("[Kafka Consumer] Product not found — productId={}, orderId={}. Skipping.",
                        event.getProductId(), event.getOrderId());
                return;
            }

            // ② Kiểm tra tồn kho đủ không
            int currentQty = product.getQuantity() != null ? product.getQuantity() : 0;
            if (currentQty < event.getQuantity()) {
                log.warn("[Kafka Consumer] Insufficient stock — productId={}, currentQty={}, requested={}. " +
                                "orderId={}. Skipping inventory deduction.",
                        event.getProductId(), currentQty, event.getQuantity(), event.getOrderId());
                // Không throw Exception → consumer tiếp tục nhận message tiếp theo
                return;
            }

            // ③ Trừ tồn kho và lưu DB
            product.setQuantity(currentQty - event.getQuantity());
            productRepo.save(product);

            log.info("[Kafka Consumer] Inventory updated — productId={}, newQty={}, orderId={}",
                    event.getProductId(), product.getQuantity(), event.getOrderId());

        } catch (Exception ex) {
            // Bắt mọi exception để Consumer không bị crash
            // Trong production: nên đẩy vào Dead Letter Topic (DLT) để retry sau
            log.error("[Kafka Consumer] Failed to process OrderPlacedEvent — orderId={}, productId={}: {}",
                    event.getOrderId(), event.getProductId(), ex.getMessage(), ex);
        }
    }
}
