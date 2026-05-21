package vn.tt.practice.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {

    private String id;  // dùng cho cancel-order endpoint

    @NotEmpty(message = "Order must contain at least one item")
    private List<ProductDTO> items;

    private int totalItemCount;

    @NotBlank(message = "Delivery type is required")
    private String delivery_type;

    private double delivery_type_cost;
    private double cost_before_delivery_rate;
    private double cost_after_delivery_rate;
    private String promo_code;

    @NotBlank(message = "Contact number is required")
    private String contact_number;

    @NotBlank(message = "User ID is required")
    private String user_id;

    private String paymentMethod;
}
