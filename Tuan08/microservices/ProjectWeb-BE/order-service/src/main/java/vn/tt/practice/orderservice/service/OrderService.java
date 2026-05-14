package vn.tt.practice.orderservice.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import vn.tt.practice.orderservice.config.ProductClient;
import vn.tt.practice.orderservice.dto.OrderRequest;
import vn.tt.practice.orderservice.dto.OrderResponse;
import vn.tt.practice.orderservice.dto.ProductDTO;
import vn.tt.practice.orderservice.exception.ServiceUnavailableException;
import vn.tt.practice.orderservice.mapper.OrderMapper;
import vn.tt.practice.orderservice.model.Order;
import vn.tt.practice.orderservice.producer.OrderEventProducer;
import vn.tt.practice.orderservice.repository.OrderRepo;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepo orderRepo;
    private final OrderMapper orderMapper;
    private final OrderEventProducer orderEventProducer;
    private final ProductClient productClient;
    private final MongoTemplate mongoTemplate;

    // -----------------------------------------------------------------------
    // Circuit Breaker — bảo vệ Feign call sang product-service
    // -----------------------------------------------------------------------

    @CircuitBreaker(name = "productServiceCircuitBreaker", fallbackMethod = "fallbackGetProduct")
    public ProductDTO checkProductAvailability(String productId) {
        log.info("[CircuitBreaker] Calling product-service — productId={}", productId);
        return productClient.getProductById(productId).getBody();
    }

    public ProductDTO fallbackGetProduct(String productId, Throwable ex) {
        log.warn("[CircuitBreaker] Fallback triggered — productId={}, reason={}", productId, ex.getMessage());
        throw new ServiceUnavailableException(
                "Hệ thống kiểm tra sản phẩm đang quá tải, " +
                "đơn hàng của bạn tạm thời chưa thể xử lý. " +
                "Vui lòng thử lại sau vài phút."
        );
    }

    // -----------------------------------------------------------------------
    // Business logic
    // -----------------------------------------------------------------------

    public OrderResponse placeOrder(OrderRequest request) {

        // ① Validate — Circuit Breaker bảo vệ, fail-fast nếu product-service sập
        request.getItems().forEach(item -> checkProductAvailability(item.getId()));

        // ② Mapper → Entity (status "PENDING" được set tại đây)
        Order orderEntity = orderMapper.toEntity(request);
        orderEntity.setStatus("PENDING");

        // ③ Lưu DB
        Order savedOrder = orderRepo.save(orderEntity);
        log.info("[OrderService] Order saved — orderId={}", savedOrder.getId());

        // ④ Publish Kafka events (async — decoupled với product-service)
        savedOrder.getItems().forEach(item ->
                orderEventProducer.publishOrderPlaced(savedOrder.getId(), item.getId(), item.getQuantity())
        );
        orderEventProducer.sendOrderEvent(
                "Order placed successfully with id: " + savedOrder.getId(),
                savedOrder.getUser_id()
        );

        // ⑤ Mapper → Response
        return orderMapper.toResponse(savedOrder);
    }

    public List<OrderResponse> findByUserId(String userId) {
        Query query = new Query(Criteria.where("user_id").is(userId));
        List<Order> orders = mongoTemplate.find(query, Order.class);

        if (orders == null || orders.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No orders found for user: " + userId);
        }

        return orders.stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    public OrderResponse cancelOrder(String id) {
        Order order = orderRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found with ID: " + id));

        order.setStatus("CANCELED");

        return orderMapper.toResponse(orderRepo.save(order));
    }
}

