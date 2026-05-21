package vn.tt.practice.orderservice.mapper;

import org.springframework.stereotype.Component;
import vn.tt.practice.orderservice.dto.OrderRequest;
import vn.tt.practice.orderservice.dto.OrderResponse;
import vn.tt.practice.orderservice.dto.Payload;
import vn.tt.practice.orderservice.model.Order;

@Component
public class OrderMapper {

    // -------------------------------------------------------------------------
    // Legacy methods — giữ nguyên để không break code hiện tại
    // -------------------------------------------------------------------------

    public Order toEntity(Payload orderDTO) {
        return Order.builder()
                .id(orderDTO.getId())
                .items(orderDTO.getItems())
                .contact_number(orderDTO.getContact_number())
                .cost_after_delivery_rate(orderDTO.getCost_after_delivery_rate())
                .cost_before_delivery_rate(orderDTO.getCost_before_delivery_rate())
                .delivery_type(orderDTO.getDelivery_type())
                .delivery_type_cost(orderDTO.getDelivery_type_cost())
                .promo_code(orderDTO.getPromo_code())
                .totalItemCount(orderDTO.getTotalItemCount())
                .user_id(orderDTO.getUser_id())
                .status(orderDTO.getStatus())
                .paymentMethod(orderDTO.getPaymentMethod())
                .build();
    }

    public Payload toDTO(Order order) {
        return Payload.builder()
                .id(order.getId())
                .items(order.getItems())
                .contact_number(order.getContact_number())
                .delivery_type(order.getDelivery_type())
                .delivery_type_cost(order.getDelivery_type_cost())
                .promo_code(order.getPromo_code())
                .totalItemCount(order.getTotalItemCount())
                .user_id(order.getUser_id())
                .status(order.getStatus())
                .paymentMethod(order.getPaymentMethod())
                .build();
    }

    // -------------------------------------------------------------------------
    // Methods mới theo chuẩn SRP: OrderRequest → Order, Order → OrderResponse
    // -------------------------------------------------------------------------

    /**
     * Chuyển OrderRequest (DTO từ client) sang Order (Entity lưu DB).
     * id và createdAt KHÔNG map từ request — DB/service tự sinh.
     * status được set mặc định là "PENDING" tại tầng Service.
     */
    public Order toEntity(OrderRequest request) {
        return Order.builder()
                .items(request.getItems())
                .totalItemCount(request.getTotalItemCount())
                .delivery_type(request.getDelivery_type())
                .delivery_type_cost(request.getDelivery_type_cost())
                .cost_before_delivery_rate(request.getCost_before_delivery_rate())
                .cost_after_delivery_rate(request.getCost_after_delivery_rate())
                .promo_code(request.getPromo_code())
                .contact_number(request.getContact_number())
                .user_id(request.getUser_id())
                .paymentMethod(request.getPaymentMethod())
                .build();
    }

    /**
     * Chuyển Order (Entity từ DB) sang OrderResponse (DTO trả về client).
     * Chỉ expose các field cần thiết, không lộ internal data.
     */
    public OrderResponse toResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .items(order.getItems())
                .totalItemCount(order.getTotalItemCount())
                .delivery_type(order.getDelivery_type())
                .delivery_type_cost(order.getDelivery_type_cost())
                .cost_before_delivery_rate(order.getCost_before_delivery_rate())
                .cost_after_delivery_rate(order.getCost_after_delivery_rate())
                .promo_code(order.getPromo_code())
                .contact_number(order.getContact_number())
                .user_id(order.getUser_id())
                .paymentMethod(order.getPaymentMethod())
                .status(order.getStatus())
                .build();
    }
}
