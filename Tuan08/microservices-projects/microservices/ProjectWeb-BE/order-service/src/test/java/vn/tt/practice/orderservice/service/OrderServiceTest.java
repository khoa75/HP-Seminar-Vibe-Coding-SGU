package vn.tt.practice.orderservice.service;

import com.mongodb.MongoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.ResponseEntity;
import vn.tt.practice.orderservice.config.ProductClient;
import vn.tt.practice.orderservice.dto.OrderRequest;
import vn.tt.practice.orderservice.dto.OrderResponse;
import vn.tt.practice.orderservice.dto.ProductDTO;
import vn.tt.practice.orderservice.mapper.OrderMapper;
import vn.tt.practice.orderservice.model.Order;
import vn.tt.practice.orderservice.producer.OrderEventProducer;
import vn.tt.practice.orderservice.repository.OrderRepo;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock private OrderRepo         orderRepo;
    @Mock private OrderMapper       orderMapper;
    @Mock private OrderEventProducer orderEventProducer;
    @Mock private ProductClient     productClient;
    @Mock private MongoTemplate     mongoTemplate;

    @InjectMocks
    private OrderService orderService;

    // ── Helper ───────────────────────────────────────────────────────────────

    private ProductDTO product(String id, int qty) {
        return ProductDTO.builder().id(id).name("P-" + id).price(100.0).quantity(qty).build();
    }

    // ── Happy Path ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("placeOrder: Happy Path — lưu DB và publish Kafka đúng 1 lần")
    void testPlaceOrder_Success() {
        ProductDTO prod = product("prod-1", 2);

        OrderRequest request = OrderRequest.builder()
                .items(List.of(prod)).totalItemCount(1)
                .delivery_type("STANDARD").delivery_type_cost(10.0)
                .cost_before_delivery_rate(200.0).cost_after_delivery_rate(210.0)
                .user_id("user-abc").contact_number("0901234567").paymentMethod("COD")
                .build();

        Order entity    = Order.builder().items(List.of(prod)).user_id("user-abc").build();
        Order savedOrder = Order.builder().id("order-123").items(List.of(prod))
                .user_id("user-abc").status("PENDING").build();
        OrderResponse response = OrderResponse.builder()
                .id("order-123").status("PENDING").user_id("user-abc").build();

        when(productClient.getProductById("prod-1")).thenReturn(ResponseEntity.ok(prod));
        when(orderMapper.toEntity(request)).thenReturn(entity);
        when(orderRepo.save(any(Order.class))).thenReturn(savedOrder);
        when(orderMapper.toResponse(savedOrder)).thenReturn(response);

        OrderResponse result = orderService.placeOrder(request);

        assertNotNull(result);
        assertEquals("order-123", result.getId());
        assertEquals("PENDING",   result.getStatus());

        verify(productClient,      times(1)).getProductById("prod-1");
        verify(orderRepo,          times(1)).save(any(Order.class));
        verify(orderEventProducer, times(1)).publishOrderPlaced("order-123", "prod-1", 2);
        verify(orderEventProducer, times(1)).sendOrderEvent(anyString(), eq("user-abc"));
        verifyNoMoreInteractions(orderEventProducer);
    }

    // ── Edge case: nhiều sản phẩm ────────────────────────────────────────────

    @Test
    @DisplayName("placeOrder: Nhiều sản phẩm — publishOrderPlaced gọi đúng N lần")
    void testPlaceOrder_MultipleItems_PublishesEventPerItem() {
        ProductDTO p1 = product("prod-1", 1), p2 = product("prod-2", 3);

        OrderRequest request = OrderRequest.builder()
                .items(List.of(p1, p2)).totalItemCount(2)
                .delivery_type("EXPRESS").user_id("user-xyz").contact_number("0987654321")
                .paymentMethod("TRANSFER").build();

        Order savedOrder = Order.builder().id("order-456").items(List.of(p1, p2))
                .user_id("user-xyz").status("PENDING").build();

        when(productClient.getProductById("prod-1")).thenReturn(ResponseEntity.ok(p1));
        when(productClient.getProductById("prod-2")).thenReturn(ResponseEntity.ok(p2));
        when(orderMapper.toEntity(request)).thenReturn(savedOrder);
        when(orderRepo.save(any())).thenReturn(savedOrder);
        when(orderMapper.toResponse(savedOrder))
                .thenReturn(OrderResponse.builder().id("order-456").status("PENDING").build());

        orderService.placeOrder(request);

        verify(orderEventProducer, times(1)).publishOrderPlaced("order-456", "prod-1", 1);
        verify(orderEventProducer, times(1)).publishOrderPlaced("order-456", "prod-2", 3);
        verify(orderRepo, times(1)).save(any());
    }

    // ── Test case 1: Dữ liệu không hợp lệ (null productId) ──────────────────

    @Test
    @DisplayName("placeOrder: productId null — ném IllegalArgumentException, DB & Kafka không gọi")
    void testPlaceOrder_InvalidData_NullProductId() {
        // Arrange — sản phẩm thiếu id
        ProductDTO badProduct = ProductDTO.builder()
                .id(null).name("Bad").price(50.0).quantity(1).build();

        OrderRequest request = OrderRequest.builder()
                .items(List.of(badProduct)).totalItemCount(1)
                .delivery_type("STANDARD").user_id("user-bad")
                .contact_number("0000000000").paymentMethod("COD").build();

        // Stub: Feign client ném lỗi khi productId null
        when(productClient.getProductById(null))
                .thenThrow(new IllegalArgumentException("productId must not be null or blank"));

        // Act & Assert
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> orderService.placeOrder(request),
                "Phải ném IllegalArgumentException khi productId null"
        );
        assertEquals("productId must not be null or blank", ex.getMessage());

        // DB và Kafka KHÔNG được gọi — exception xảy ra trước bước save
        verify(orderRepo,          never()).save(any());
        verify(orderEventProducer, never()).publishOrderPlaced(any(), any(), anyInt());
        verify(orderEventProducer, never()).sendOrderEvent(any(), any());
    }

    // ── Test case 2: DB lỗi → Kafka Producer không được gọi ─────────────────

    @Test
    @DisplayName("placeOrder: MongoException khi save — Kafka Producer KHÔNG được gọi")
    void testPlaceOrder_DatabaseError_KafkaNotCalled() {
        ProductDTO prod = product("prod-db-fail", 1);

        OrderRequest request = OrderRequest.builder()
                .items(List.of(prod)).totalItemCount(1)
                .delivery_type("STANDARD").user_id("user-db-fail")
                .contact_number("0111111111").paymentMethod("COD").build();

        Order entity = Order.builder().items(List.of(prod)).user_id("user-db-fail").build();

        // Validate thành công
        when(productClient.getProductById("prod-db-fail")).thenReturn(ResponseEntity.ok(prod));
        when(orderMapper.toEntity(request)).thenReturn(entity);

        // DB ném MongoException
        when(orderRepo.save(any(Order.class)))
                .thenThrow(new MongoException("Connection timeout to MongoDB"));

        // Act & Assert — exception phải bubble up
        MongoException ex = assertThrows(
                MongoException.class,
                () -> orderService.placeOrder(request),
                "Phải propagate MongoException khi DB lỗi"
        );
        assertTrue(ex.getMessage().contains("Connection timeout"));

        // save đã được gọi (nhưng throw exception) — Kafka CHƯA bao giờ được gọi
        verify(orderRepo,          times(1)).save(any(Order.class));
        verify(orderEventProducer, never()).publishOrderPlaced(any(), any(), anyInt());
        verify(orderEventProducer, never()).sendOrderEvent(any(), any());
    }
}
