package vn.tt.practice.orderservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import vn.tt.practice.orderservice.dto.OrderRequest;
import vn.tt.practice.orderservice.dto.OrderResponse;
import vn.tt.practice.orderservice.service.OrderService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/place-order")
    public ResponseEntity<OrderResponse> placeOrder(@Valid @RequestBody OrderRequest request) {
        if (request.getUser_id() == null || request.getUser_id().isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Please login first");
        }
        log.info("[OrderController] placeOrder — userId={}, items={}",
                request.getUser_id(), request.getItems().size());
        return ResponseEntity.ok(orderService.placeOrder(request));
    }

    @GetMapping("/{user_id}/get-orders")
    public ResponseEntity<List<OrderResponse>> getOrderByUserId(@PathVariable String user_id) {
        return ResponseEntity.ok(orderService.findByUserId(user_id));
    }

    @PostMapping("/cancel-order")
    public ResponseEntity<OrderResponse> cancelOrder(@RequestBody OrderRequest request) {
        return ResponseEntity.ok(orderService.cancelOrder(request.getId()));
    }
}
