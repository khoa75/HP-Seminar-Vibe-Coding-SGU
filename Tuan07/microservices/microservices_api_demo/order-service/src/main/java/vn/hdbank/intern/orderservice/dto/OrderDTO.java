<<<<<<< HEAD
package vn.hdbank.intern.orderservice.dto;


import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDTO {
    private Long id;
    @Size(max = 255, message = "Error")
    private String orderNumber;
    private List<OrderLineItemsDTO> orderLineItemsDtoList;

}
=======
package vn.hdbank.intern.orderservice.dto;


import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDTO {
    private Long id;
    @Size(max = 255, message = "Error")
    private String orderNumber;
    private List<OrderLineItemsDTO> orderLineItemsDtoList;

}
>>>>>>> 5097b7c3 (Tuan07/Init commit for Tuan07)
